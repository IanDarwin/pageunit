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
import pageunit.html.HTMLScript;
import pageunit.html.HTMLStyle;

/** Test handling of META redirects */
public class ParserTest4 extends TestCase {
	final String testData = "<html><head><title>AIS - Index</title>" + 
	"<style>A.navbar1HREFSTYLE{font-family: Verdana, Arial, Helvetica, sans-serif;}</style>" +
	"<SCRIPT LANGUAGE=JavaScript1.2>var highlight; var highlightSelected;</script>" +
	"<META HTTP-EQUIV=\"Refresh\" content=\"0; URL=barcode_list.jsp\">" +
	"<body><form action='/foo' method='post' onSubmit='crazy eights'>" +
	"<input type='submit' name='submit'/></form>" +
	"<p><a href='/bar' name='froo'>Link <b>Text</b></a></p>";
	
	private HTMLPage page;
	
	public void setUp() throws IOException, HTMLParseException {
		// System.err.println("Analyze THIS: " + testData);
		page = new HTMLParser().parse(new StringReader(testData));
	}
	
	public void testFindMeta() throws Throwable {
		System.out.println("ParserTest4.testFindMeta()");
		assertNotNull(page);
		boolean found = false;
		List<HTMLComponent> children = page.getChildren();
		
		for (HTMLComponent c : children) {
			if (c instanceof HTMLMeta) {
				found = true;
				System.out.println("Found: " + c);
			}
		}
		assertTrue("Look for meta element", found);
	}
	
	public void testFindScript() throws Throwable {
		System.out.println("ParserTest4.testFindScript()");
		assertNotNull(page);
		List<HTMLComponent> children = page.getChildren();
		
		for (HTMLComponent c : children) {
			if (c instanceof HTMLScript) {
				System.out.println("Found: " + c);
				assertEquals("var highlight; var highlightSelected;", c.getBody());
				return;
			}
		}
		fail("look for <script>");
	}
	
	public void testFindStyle() throws Throwable {
		System.out.println("ParserTest4.testFindStyle()");
		List<HTMLComponent> children = page.getChildren();
		
		for (HTMLComponent c : children) {
			if (c instanceof HTMLStyle) {
				System.out.println("Found: " + c);
				assertEquals("A.navbar1HREFSTYLE{font-family: Verdana, Arial, Helvetica, sans-serif;}", c.getBody());
				return;
			}
		}
		fail("look for <style>");
	}
}
