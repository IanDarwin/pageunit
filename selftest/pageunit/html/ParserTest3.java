package regress.html;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;
import pageunit.html.HTMLComponent;
import pageunit.html.HTMLForm;
import pageunit.html.HTMLPage;
import pageunit.html.HTMLParseException;
import pageunit.html.HTMLParser;
import pageunit.html.HTMLScript;

public class ParserTest3 extends TestCase {
	final String testData = "<html><head><title>Fool</title></head>" +
	"<script language='javascript'>function(bleah) if (a < b) return -1;</script>" +
	"<body><form action='/foo' method='post' onSubmit='crazy eights'>" +
	"<input type='submit' name='submit'/></form>" +
	"<p><a href='/bar' name='froo'>Link <b>Text</b></a></p>";
	
	private HTMLPage page;
	
	public void setUp() throws IOException, HTMLParseException {
		page = new HTMLParser().parse(new StringReader(testData));
	}
	
	public void testScript() throws Throwable {
		assertNotNull(page);
		boolean found = false;
		List<HTMLComponent> children = page.getChildren();
		for (HTMLComponent c : children) {
			System.out.printf("HTML Page Child %s%n", c);
			if (c instanceof HTMLForm) {
				System.out.printf("FORM: onSubmit=%s%n", ((HTMLForm)c).getOnSubmit());
			}
			if (c instanceof HTMLScript) {
				found = true;
				HTMLScript script = (HTMLScript) c;
				System.out.println("Script:" + script.getBody());
			}
		}
		assertTrue("Look for Script element", found);
	}
}
