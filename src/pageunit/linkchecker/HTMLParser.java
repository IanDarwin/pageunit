package pageunit.linkchecker;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import pageunit.html.HTMLComponent;
import pageunit.html.HTMLComponentFactory;
import pageunit.html.HTMLParseException;

/**
 * Build a list of elements that can be checked.
 * @version $Id$
 */
public class HTMLParser extends HTMLEditorKit.ParserCallback {
		
	private static final HTML.Tag[] wantedComplexTags = {
			HTML.Tag.FORM,
			HTML.Tag.A,
			HTML.Tag.APPLET,
			HTML.Tag.IMG
	};
	
	// XXX static, but returned from synchronized instance method...
	private static List<HTMLComponent> pageElements = new ArrayList<HTMLComponent>();
	
	public HTMLParser() {
	}
	
	private boolean debug;
		
	@Override
	public void handleStartTag(HTML.Tag tag, MutableAttributeSet attrs, int pos) {
		for (HTML.Tag t : wantedComplexTags) {
			if (t==tag) {
				if (debug) {
					System.out.print("COMPLEX: ");
				}
				HTMLComponent comp = HTMLComponentFactory.create(tag, attrs);
				if (debug) {
					System.out.println(comp);
				}
				pageElements.add(comp);				
			}
		}
	}
	
	public synchronized List<HTMLComponent> parse(Reader reader) throws IOException, HTMLParseException {
		new ParserDelegator().parse(reader, this, true);
		return pageElements;
	}
	
	public synchronized List<HTMLComponent> parse(String s) throws IOException, HTMLParseException {
		return parse(new StringReader(s));
	}
}
