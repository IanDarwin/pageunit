package pageunit.html;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Stack;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * Try to build a simple-enough HTML parser using the one true built-in HTML
 * parser, that is, Swing's HTMLEditorKit (this saves several dependencies on
 * external APIs).
 * For the inspirational genius behind this, the reader is referred to
 * the article
 * <a href="http://java.sun.com/products/jfc/tsc/articles/bookmarks/">
 * The Swing HTML Parser</a> on Sun's JFC web site.
 * @note This class is NOT thread-safe; for use in one thread at a time!
 * @version $Id$
 */
public class HTMLParser extends HTMLEditorKit.ParserCallback {
	
	private boolean debug = false;
		
	private HTMLPage currentPage;
	
	private final HTML.Tag[] wantedComplexTags = {
			HTML.Tag.HTML,
			HTML.Tag.FORM,
			HTML.Tag.SELECT,
			HTML.Tag.A,
			HTML.Tag.TITLE,
			HTML.Tag.SCRIPT,
			HTML.Tag.STYLE,
	};
	private final HTML.Tag[] wantedSimpleTags = {
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
	};
	
	private HTMLForm currentForm;		// for addInput()
	
	public HTMLParser() {
		// Nothing to do this time.
	}
	
	// This variable and three methods implement a semi-opaque stack of HTML containers
	private Stack<HTMLContainer> containerStack = new Stack<HTMLContainer>();
	
	private void pushContainer(HTMLContainer newbie) {
		containerStack.push(newbie);
	}
	
	private HTMLContainer popContainer() {
		return containerStack.pop();
	}
	
	private HTMLContainer currentContainer() {	// for addChild()
		return containerStack.peek();
	}
	
	// Same deal for current component, so body text goes in correctly:
	// This variable and three methods implement a semi-opaque stack of HTML containers
	private Stack<HTMLComponent> componentStack = new Stack<HTMLComponent>();
	
	private void pushComponent(HTMLComponent newbie) {
		//System.out.printf("HTMLParser.pushComponent(%s)%n", newbie);
		componentStack.push(newbie);
	}
	
	private HTMLComponent popComponent() {
		//System.out.printf("HTMLParser.popComponent() ==> %s%n", componentStack.peek());
		return componentStack.pop();
	}
	
	private HTMLContainer currentComponent() {	// for setBody()
		return containerStack.peek();
	}
	
	@Override
	public void handleStartTag(HTML.Tag tag, MutableAttributeSet attrs, int pos) {
		if (debug) {
			System.out.println("StartTag: " + tag);
		}
		HTMLComponent comp = null;
		boolean found = false;
		for (HTML.Tag t : wantedComplexTags) {
			if (t==tag) {
				comp = HTMLComponentFactory.create(tag, attrs);
				found = true;
				break;				
			}
		}
		if (!found && !totallyIgnoreThisTag(tag)) {
			comp = new GenericHTMLContainer(null, tag.toString());
		}
		if (debug) {
			System.out.println("DOING COMPLEX: " + comp);
		}
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
		if (debug) {
			System.out.println("SimpleTag: " + tag);
		}
		HTMLComponent comp = null;
		boolean found = false;
		for (HTML.Tag t : wantedSimpleTags) {
			if (t == tag) {				
				comp = HTMLComponentFactory.create(tag, attrs);
				found = true;
				break;
			}
		}
		if (!found && !totallyIgnoreThisTag(tag)) {
			comp = new GenericHTMLComponent(null, tag.toString());		
		}
		if (debug) {
			System.out.println("DOING SIMPLE: " + comp);
		}

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
	/** If the HTML.tag that we get represents a class in an HTMLContainer in my hierarchy,
	 * then I need to pop this container, so we don't get the whole body appearing in the TITLE
	 * element (as happened prior to this revision).
	 */
	public void handleEndTag(HTML.Tag tag, int pos) {
		if (debug) {
			System.out.printf("HTMLParser.handleEndTag(%s)%n", tag);
		}
		popComponent();
		if (HTMLContainer.class.isAssignableFrom(HTMLComponentFactory.classForTagType(tag))) {
			popContainer();
		}
	}
	
	@Override
	public void handleText(char[] data, int pos) {
		final String bodyContent = new String(data);
		if (">".equals(bodyContent))
			return;	// A glitch in the Java 5.0 parser causes this with abbreviated tags.
		if (debug) {
			System.out.println("TEXT += " + bodyContent);
		}
		currentComponent().appendBody(bodyContent);
		if (debug) {
			System.out.println("TEXT == " + currentContainer().getBody());
		}
	}
	
	private String content = null;
	
	/** Parse an HTML page given a Reader. Since we will 99% likely need the page as a String,
	 * read it all into a String (content), and pass it to parse(String).
	 * @param reader
	 * @return
	 * @throws IOException
	 * @throws HTMLParseException
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
	 * @param s
	 * @return
	 * @throws IOException
	 * @throws HTMLParseException
	 */
	public HTMLPage parse(String s) throws IOException, HTMLParseException {
		currentPage = new HTMLPageImpl("Outer Page");
		pushContainer(currentPage);
		currentPage.setContent(content);
		new ParserDelegator().parse(new StringReader(s), this, true);
		return currentPage;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		HTMLEditorKit.ParserCallback callback = new HTMLParser();
		int n = 0;
		for (String fileName : args) {
			System.out.println("** START FILE: " + fileName);
			Reader reader = new FileReader(fileName);
			new ParserDelegator().parse(reader, callback, true);
			++n;
		}
		System.out.printf("Parsed %d files%n", n);
	}
}
