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
 * XXX TODO Optimize: don't require construction for each page!
 * (Issue is that PAGE is used throughout, but don't want to make it static...).
 * @author ian
 * @version $Id$
 */
public class HTMLParser extends HTMLEditorKit.ParserCallback {
		
	private final HTMLPage PAGE;
	
	private final HTML.Tag[] wantedComplexTags = {
			HTML.Tag.HTML,
			HTML.Tag.FORM,
			HTML.Tag.INPUT,	// MUST appear in both lists, sorry.
			HTML.Tag.A,
			HTML.Tag.TITLE
	};
	private HTML.Tag[] wantedSimpleTags = {
			HTML.Tag.INPUT,	// Input is treated as simple tag!!
	};
	
	private HTMLForm currentForm;
	
	public HTMLParser() {
		PAGE = new HTMLPageImpl("Outer Page");
		pushContainer(PAGE);
	}
	
	// This variable and three methods implement a semi-opaque stack of HTML containers
	private Stack<HTMLContainer> containerStack = new Stack<HTMLContainer>();

	private boolean debug;
	
	void pushContainer(HTMLContainer newbie) {
		containerStack.push(newbie);
	}
	
	HTMLContainer popContainer() {
		return containerStack.pop();
	}
	
	HTMLContainer currentContainer() {
		return containerStack.peek();
	}
	
	@Override
	public void handleStartTag(HTML.Tag tag, MutableAttributeSet attrs, int pos) {
		if (debug) {
			System.out.println("StartTag: " + tag);
		}
		for (HTML.Tag t : wantedComplexTags) {
			if (t==tag) {
				System.out.print("COMPLEX: ");
				HTMLComponent tmp = doTag(tag, attrs);
				
				currentContainer().addChild(tmp);
				
				if (tmp instanceof HTMLContainer) {
					pushContainer((HTMLContainer)tmp);
				}
				
				if (tmp instanceof HTMLAnchor) {
					PAGE.addAnchor((HTMLAnchor)tmp);
				}
				if (tmp instanceof HTMLForm) {
					currentForm = (HTMLForm)tmp;
					PAGE.addForm(currentForm);
				}
				if (tmp instanceof HTMLInput && currentForm != null) {
					((HTMLFormImpl)currentForm).addInput((HTMLInput)tmp);
				}
				if (tmp instanceof HTMLTitle) {
					((HTMLPageImpl)PAGE).setTitle((HTMLTitle)tmp);
				}
			}
		}
	}
	
	@Override
	public void handleEndTag(HTML.Tag t, int pos) {
		if (t instanceof HTMLContainer) {
			popContainer();
		}
	}
	
	@Override
	public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attrs, int pos) {
		for (HTML.Tag t : wantedSimpleTags) {
			if (t == tag) {
				if (HTML.Tag.INPUT == t) {
					// Logic for Input tag fits better in HandleStartTag.
					handleStartTag(tag, attrs, pos);
				} else {
					System.out.print("SIMPLE: ");
					currentContainer().addChild(doTag(tag, attrs));
				}
			}
		}
	}
	
	private HTMLComponent doTag(HTML.Tag tag,  MutableAttributeSet attrs) {
		HTMLComponent comp = HTMLComponentFactory.create(tag, attrs);
		System.out.println(comp);
		return comp;
	}
	
	@Override
	public void handleText(char[] data, int pos) {
		final String bodyContent = new String(data);
		if (">".equals(bodyContent))
			return;	// A glitch in the parser causes this with abbreviated tags.
		System.out.println("TEXT: " + bodyContent);
		currentContainer().setBody(bodyContent);
	}
	
	public HTMLPage parse(Reader reader) throws IOException, HTMLParseException {
		new ParserDelegator().parse(reader, this, true);
		return PAGE;
	}
	
	public HTMLPage parse(String s) throws IOException, HTMLParseException {
		return parse(new StringReader(s));
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
