package regress.html;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;
import pageunit.html.HTMLComponent;
import pageunit.html.HTMLMeta;
import pageunit.html.HTMLPage;
import pageunit.html.HTMLParseException;
import pageunit.html.HTMLParser;

/** Test handling of META redirects */
public class ParserTest4 extends TestCase {
	final String testData = "<html><head><title>AIS - Index</title>" + 
	"<META HTTP-EQUIV=\"Refresh\" content=\"0; URL=barcode_list.jsp\">" +
	"<body><form action='/foo' method='post' onSubmit='crazy eights'>" +
	"<input type='submit' name='submit'/></form>" +
	"<p><a href='/bar' name='froo'>Link <b>Text</b></a></p>";
	
	private HTMLPage page;
	
	public void setUp() throws IOException, HTMLParseException {
		// System.err.println("Analyze THIS: " + testData);
		page = new HTMLParser().parse(new StringReader(testData));
	}
	
	public void testScript() throws Throwable {
		assertNotNull(page);
		boolean found = false;
		List<HTMLComponent> children = page.getChildren();
		
		for (HTMLComponent c : children) {
			// System.out.println(c);
			if (c instanceof HTMLMeta) {
				found = true;
				System.out.println("Found: " + c);
			}
		}
		assertTrue("Look for meta element", found);
	}
}
