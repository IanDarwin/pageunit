package regress.html;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;
import pageunit.html.HTMLComponent;
import pageunit.html.HTMLContainer;
import pageunit.html.HTMLForm;
import pageunit.html.HTMLHTML;
import pageunit.html.HTMLParser;
import pageunit.html.HTMLTitle;

public class ParserTest extends TestCase {
	/*
	 * Test method for 'pageunit.html.HTMLParser.parse(Reader)'
	 */
	public void testParse() throws Throwable {
		String test1 = "<html><head><title>Foo</title></head><body>" +
			"<form action='/foo' method='post'><input type='submit'/></form>" +
			"<a href='/bar'>Link <b>Text</b></a>'";
		Reader r = new StringReader(test1);
		HTMLContainer cont = new HTMLParser().parse(r);
		assertNotNull(cont);
		List<HTMLComponent> topLevelItems = cont.getChildren();
		assertEquals("Children of TOP", 1, topLevelItems.size());
		HTMLHTML page = (HTMLHTML)topLevelItems.get(0);
		assertTrue("child of HTML is title", page.getChildren().get(0) instanceof HTMLTitle);
		HTMLForm form = (HTMLForm)page.getChildren().get(1);
		assertEquals("this form has one child", 1, form.getChildren().size());
	}
}
