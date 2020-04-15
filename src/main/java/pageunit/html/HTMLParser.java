package pageunit.html;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Stack;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.log4j.Logger;

/**
 * Try to build a simple-enough HTML parser using the one true built-in HTML
 * parser, that is, Swing's HTMLEditorKit (this saves several dependencies on
 * external APIs).
 * For the inspirational genius behind this, the reader is referred to
 * the article
 * <a href="http://java.sun.com/products/jfc/tsc/articles/bookmarks/">
 * The Swing HTML Parser</a> on Sun's JFC web site.
 * Note: This class is NOT thread-safe; for use in one thread at a time!
 * XXX Replace with TagSoup?
 */
public class HTMLParser extends HTMLEditorKit.ParserCallback {
	private static Logger logger = Logger.getLogger(HTMLParser.class);
		
	private HTMLPage currentPage;
	
	static final HTML.Tag[] wantedComplexTags = {
			HTML.Tag.HTML,
			HTML.Tag.FORM,
			HTML.Tag.SELECT,
			HTML.Tag.A,
			HTML.Tag.TITLE,
			HTML.Tag.SCRIPT,
			HTML.Tag.STYLE,
	};
	static HTML.Tag[] wantedSimpleTags = {
			HTML.Tag.INPUT,	// Input is treated as simple tag by the Swing HTML Parser
			HTML.Tag.META,
			HTML.Tag.OPTION,
	};
	
	/** Tags that should be discarded because otherwise they would cause
	 * text to be assigned to the wrong parent tag.
	 */
	private final HTML.Tag[] totallyIgnoreTags = {
			HTML.Tag.B,
			HTML.Tag.I,
			HTML.Tag.EM,
			HTML.Tag.STRIKE,
			HTML.Tag.STRONG,
			HTML.Tag.DIV,
			HTML.Tag.TABLE,
			HTML.Tag.TR,
			HTML.Tag.TD
	};
	
	private HTMLForm currentForm;		// for addInput()
	
	public HTMLParser() {
		// Nothing to do this time.
	}
	
	public static HTML.Tag[] getWantedSimpleTags() {
		return wantedSimpleTags.clone();
	}

	public static HTML.Tag[] getWantedComplexTags() {
		return wantedComplexTags.clone();
	}
	
	// This variable and three methods implement a semi-opaque stack of HTML containers
	private Stack<HTMLContainer> containerStack = new Stack<HTMLContainer>();
	
	void pushContainer(HTMLContainer newbie) {
		logger.info(String.format("HTMLParser.pushContainer(%s) [%d]", newbie, containerStack.size()));
		if (newbie == null) {
			throw new IllegalArgumentException("may not push null");
		}
		containerStack.push(newbie);
	}
	
	HTMLContainer popContainer() {
		HTMLContainer c = containerStack.pop();
		logger.info(String.format("HTMLParser.popContainer(%s) [%d]", c, containerStack.size()));
		return c;
	}
	
	HTMLContainer currentContainer() {	// for addChild()
		return containerStack.peek();
	}
	
	// Same deal for current component, so body text goes in correctly:
	// This variable and three methods implement a semi-opaque stack of HTML containers
	private Stack<HTMLComponent> componentStack = new Stack<HTMLComponent>();
	
	void pushComponent(HTMLComponent newbie) {
		if (newbie == null) {
			throw new IllegalArgumentException("may not push null");
		}
		componentStack.push(newbie);
		logger.info(String.format("HTMLParser.pushComponent(%s) [%d]", newbie, componentStack.size()));
	}
	
	HTMLComponent popComponent() {
		HTMLComponent c = componentStack.pop();
		logger.info(String.format("HTMLParser.popComponent(%s) [%d]", c, componentStack.size()));
		return c;
	}
	
	HTMLComponent currentComponent() {
		return componentStack.isEmpty() ? null : componentStack.peek();
	}
	
	@Override
	public void handleStartTag(HTML.Tag tag, MutableAttributeSet attrs, int pos) {
		logger.info("StartTag: " + tag);
		HTMLComponent comp = null;
		if (isWantedComplexTag(tag)) {
			comp = HTMLComponentFactory.create(tag, attrs);
		}
		// If not a known start tag, but not utterly trivial, make generic tag.
		if (comp == null) {
			if (totallyIgnoreThisTag(tag)) {
				return;
			}
			comp = new GenericHTMLContainer(null, tag.toString());
		}
		logger.info("DOING COMPLEX: " + comp);
		pushComponent(comp);
		handleCommon(comp);
		
		if (comp instanceof HTMLAnchor) {
			currentPage.addAnchor((HTMLAnchor)comp);
		}
		if (comp instanceof HTMLForm) {
			currentForm = (HTMLForm)comp;
			currentPage.addForm(currentForm);
		}				
		if (comp instanceof HTMLTitle && ((HTMLPageImpl)currentPage).getTitle() == null) {
			((HTMLPageImpl)currentPage).setTitle((HTMLTitle)comp);
		}

		return;
	}

	private boolean isWantedComplexTag(Tag tag) {
		for (HTML.Tag t : wantedComplexTags) {
			if (tag == t)
				return true;
		}
		return false;
	}
	private boolean totallyIgnoreThisTag(Tag tag) {
		for (HTML.Tag t : totallyIgnoreTags) {
			if (t == tag) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attrs, int pos) {
		logger.info("SimpleTag: " + tag);
		HTMLComponent comp = null;
		for (HTML.Tag t : wantedSimpleTags) {
			if (t == tag) {				
				comp = HTMLComponentFactory.create(tag, attrs);
				break;
			}
		}
		if (comp == null && !totallyIgnoreThisTag(tag)) {
			comp = new GenericHTMLComponent(null, tag.toString());		
		}
		logger.info("DOING SIMPLE: " + comp);

		handleCommon(comp);
		return;
	}

	/**
	 * Handle special goo that may need application to both simple and complex tag types.
	 * @param comp
	 */
	private void handleCommon(HTMLComponent comp) {
		currentPage.addChild(comp);
		if (currentPage != currentContainer()) {
			currentContainer().addChild(comp);
		}
		if (comp instanceof HTMLContainer) {
			pushContainer((HTMLContainer)comp);
		}
		if (comp instanceof HTMLInput && currentForm != null) {
			((HTMLFormImpl)currentForm).addInput((HTMLInput)comp);
		}
	}

	@Override
	/** If the HTML.tag that we get represents a class in an HTMLContainer in *my* hierarchy,
	 * then I need to pop this container, so we don't get the whole body appearing in the TITLE
	 * element (as happened prior to this revision).
	 */
	public void handleEndTag(HTML.Tag tag, int pos) {
		logger.info(String.format("HTMLParser.handleEndTag(%s)", tag));
		Class<?> classForTagType = HTMLComponentFactory.classForTagType(tag);
		HTMLComponent curComp = currentComponent();
		
		// If this end tag matches the current component, pop it.
		// The test may be redundant but I'm wrestling with stackunderflow...
		if (curComp.getClass().isAssignableFrom(classForTagType)) {
			popComponent();
		}
		// If this end tag represents a container, pop that as well.
		if (HTMLContainer.class.isAssignableFrom(classForTagType)) {
			popContainer();
		}
	}
	
	@Override
	public void handleText(char[] data, int pos) {
		final String bodyContent = new String(data);
		logger.info("TEXT += " + bodyContent);
		if (">".equals(bodyContent))
			return;	// A glitch in the Java 5.0 parser causes this with abbreviated tags.
		currentComponent().appendBody(bodyContent);
		logger.info("TEXT NOW " + currentContainer().getBody());
	}
	
	/** 
	 * The Swing HTML parser sends the body of all SCRIPT tags as comments, even if they are
	 * non-comment Script elements. So convert them here.
	 */
	@Override
	public void handleComment(char[] data, int pos) {
		logger.info("HTMLParser.handleComment()");
		if (currentComponent() instanceof HTMLScript) {
			currentComponent().appendBody(new String(data));
		} else {
			logger.info("LOSING THIS, because curComp = " + currentComponent() + " " + new String(data));
		}
	}
	
	private String content = null;
	
	/** Parse an HTML page given a Reader. Since we will 99% likely need the page as a String,
	 * read it all into a String (content), and pass it to parse(String).
	 * @param reader The input
	 * @return The HTMLPage object
	 * @throws IOException If something fails to read
	 * @throws HTMLParseException If something fails to parse
	 */
	public HTMLPage parse(Reader reader) throws IOException, HTMLParseException {
		StringBuffer sb = new StringBuffer();
		int ch;
		while ((ch = reader.read()) != -1) {
			sb.append((char)ch);
		}
		content = sb.toString();
		return parse(content);
	}
	
	/** Parse an HTML page given it as a (long) String.
	 * Create a StringReader and pass that to ParserDelegator().parse().
	 * @param s The input page as a string
	 * @return The HTMLPage
	 * @throws IOException If something fails to read
	 * @throws HTMLParseException If something fails to parse
	 */
	public HTMLPage parse(String s) throws IOException, HTMLParseException {
		currentPage = new HTMLPageImpl("Outer Page");
		pushContainer(currentPage);
		currentPage.setContent(content);
		new ParserDelegator().parse(new StringReader(s), this, true);
		return currentPage;
	}
}
