package pageunit.html;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Stack;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
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
		
	private HTMLPage page;
	
	private final HTML.Tag[] wantedComplexTags = {
			HTML.Tag.HTML,
			HTML.Tag.FORM,
			HTML.Tag.INPUT,	// MUST appear in both lists, sorry.
			HTML.Tag.A,
			HTML.Tag.TITLE,
			HTML.Tag.SCRIPT
	};
	private final HTML.Tag[] wantedSimpleTags = {
			HTML.Tag.INPUT,	// Input is treated as simple tag!!
	};
	
	private HTMLForm currentForm;
	
	public HTMLParser() {
		// Nothing to do this time.
	}

	private boolean debug = true;
	
	// This variable and three methods implement a semi-opaque stack of HTML containers
	private Stack<HTMLContainer> containerStack = new Stack<HTMLContainer>();
	
	private void pushContainer(HTMLContainer newbie) {
		containerStack.push(newbie);
	}
	
	private HTMLContainer popContainer() {
		return containerStack.pop();
	}
	
	private HTMLContainer currentContainer() {
		return containerStack.peek();
	}
	
	@Override
	public void handleStartTag(HTML.Tag tag, MutableAttributeSet attrs, int pos) {
		if (debug) {
			System.out.println("StartTag: " + tag);
		}
		for (HTML.Tag t : wantedComplexTags) {
			if (t==tag) {
				HTMLComponent tmp = HTMLComponentFactory.create(tag, attrs);
				
				currentContainer().addChild(tmp);
				
				if (tmp instanceof HTMLContainer) {
					pushContainer((HTMLContainer)tmp);
				}
				
				if (tmp instanceof HTMLAnchor) {
					page.addAnchor((HTMLAnchor)tmp);
				}
				if (tmp instanceof HTMLForm) {
					currentForm = (HTMLForm)tmp;
					page.addForm(currentForm);
				}
				if (tmp instanceof HTMLInput && currentForm != null) {
					((HTMLFormImpl)currentForm).addInput((HTMLInput)tmp);
				}
				if (tmp instanceof HTMLTitle && ((HTMLPageImpl)page).getTitle() == null) {
					((HTMLPageImpl)page).setTitle((HTMLTitle)tmp);
				}
			}
		}
	}
	
	@Override
	/** If the HTML.tag that we get represents a class in an HTMLContainer in my hierarchy,
	 * then I need to pop this container, so we don't get the whole body appearing in the TITLE
	 * element (as happened prior to this revision).
	 */
	public void handleEndTag(HTML.Tag tag, int pos) {		
		if (tag instanceof HTMLContainer) {
			popContainer();
		}
	}
	
	@Override
	public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attrs, int pos) {
		if (debug) {
			System.out.println("SimpleTag: " + tag);
		}
		for (HTML.Tag t : wantedSimpleTags) {
			if (t == tag) {
				if (HTML.Tag.INPUT == t) {
					// Logic for Input tag fits better in HandleStartTag.
					handleStartTag(tag, attrs, pos);
				} else {
					if (debug) {
						System.out.print("SIMPLE: ");
					}
					currentContainer().addChild(HTMLComponentFactory.create(tag, attrs));
				}
			}
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
		currentContainer().appendBody(bodyContent);
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
		page = new HTMLPageImpl("Outer Page");
		pushContainer(page);
		page.setContent(content);
		new ParserDelegator().parse(new StringReader(s), this, true);
		return page;
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
