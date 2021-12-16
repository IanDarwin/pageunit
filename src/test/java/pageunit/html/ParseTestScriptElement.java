package pageunit.html;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;

/** Test handling of Script elements */
public class ParseTestScriptElement extends TestCase {
	
	final static String testData = "<html><head><title>AIS - Index</title>" + 
	"<style>A.navbar1HREFSTYLE{font-family: Verdana, Arial, Helvetica, sans-serif;}</style>" +
	"<script name=fred LANGUAGE=JavaScript1.2>var highlight; var highlightSelected;</script>" +
	"<META HTTP-EQUIV=\"Refresh\" content=\"0; URL=barcode_list.jsp\">" +
	"<body><form action='/foo' method='post' onSubmit='crazy eights'>" +
	"<input type='submit' name='submit'/></form>" +
	"<p><a href='/bar' name='froo'>Link <b>Text</b></a></p>";
	
	private HTMLPage page;
	
	public void setUp() throws IOException, HTMLParseException {
		// System.err.println("Analyze THIS: " + testData);
		page = new HTMLParser().parse(new StringReader(testData));
	}
	
	public void testFindScript() throws Throwable {
		System.out.println("ParserTest4.testFindScript()");
		assertNotNull(page);
		List<HTMLComponent> children = page.getChildren();
		
		for (HTMLComponent c : children) {
			if (c instanceof HTMLForm) {
				System.out.printf("FORM: onSubmit=%s%n", ((HTMLForm)c).getOnSubmit());
			}
			if (c instanceof HTMLScript) {
				System.out.println("Found: " + c);
				assertEquals("var highlight; var highlightSelected;", c.getBody());
				return;
			}
		}
		fail("look for <script>");
	}
}
