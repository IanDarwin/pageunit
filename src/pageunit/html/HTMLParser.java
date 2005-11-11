package pageunit.html;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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
 * @author ian
 * @version $Id$
 */
public class HTMLParser {

	static class PPCallback extends HTMLEditorKit.ParserCallback {
		
		List<String> tags = new ArrayList<String>();
		
		private HTML.Tag[] wantedComplexTags = {
				HTML.Tag.HTML,
				HTML.Tag.FORM,
				HTML.Tag.A,
		};
		
		public void handleStartTag(HTML.Tag tag, MutableAttributeSet attrs, int pos) {
			
			for (HTML.Tag t : wantedComplexTags) {
				if (t==tag) {
					if (t == HTML.Tag.A)
						continue;
					System.out.print("COMPLEX: ");
					doTag(tag, attrs);
				}
			}
		}
		
		public void handleEndTag(HTML.Tag t, int pos) {
			
		}
		
		private HTML.Tag[] wantedSimpleTags = {
			HTML.Tag.INPUT
		};
		
		public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attrs, int pos) {
			for (HTML.Tag t : wantedSimpleTags) {
				if (t == tag) {
					System.out.print("SIMPLE: ");
					doTag(tag, attrs);
				}
			}
		}
		
		public void doTag(HTML.Tag tag,  MutableAttributeSet attrs) {
			System.out.print(tag);
			System.out.print(' ');
			System.out.println("name=" + attrs.getAttribute(HTML.Attribute.NAME));
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		HTMLEditorKit.ParserCallback callback = new PPCallback();
		for (String fileName : args) {
			System.out.println("** START FILE: " + fileName);
			Reader reader = new FileReader(fileName);
			new ParserDelegator().parse(reader, callback, true);
		}
	}
}