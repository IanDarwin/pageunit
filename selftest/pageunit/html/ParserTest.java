package regress.html;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;
import pageunit.html.HTMLComponent;
import pageunit.html.HTMLForm;
import pageunit.html.HTMLHTML;
import pageunit.html.HTMLPage;
import pageunit.html.HTMLParseException;
import pageunit.html.HTMLParser;
import pageunit.html.HTMLTitle;

public class ParserTest extends TestCase {
	final String testData = "<html><head><title>Fool</title></head><body>" +
	"<form action='/foo' method='post'><input type='submit' name='submit'/></form>" +
	"<a href='/bar' name='froo'>Link <b>Text</b></a>";
	
	private HTMLPage page;
	
	public void setUp() throws IOException, HTMLParseException {
		page = new HTMLParser().parse(new StringReader(testData));
	}

	public void testParse() throws Throwable {
		assertNotNull(page);
	}
	
	public void testPageChild() {
		List<HTMLComponent> allItems = page.getChildren();
		assertEquals("Children of Page", 8, allItems.size());
		for (Object o : allItems) {
			System.out.println("Child of Page: " + o);
		}
		HTMLHTML htmlTop = (HTMLHTML)allItems.get(0);
		assertTrue("child of HTML is title", htmlTop.getChildren().get(0) instanceof HTMLTitle);
	}
	
	public void testForm() {
		HTMLForm form = page.getFormByURL("/foo");
		assertNotNull("find form in page", form);
		assertTrue("form has inputs", form.getChildren().size() >= 1);
		assertNotNull("get input by name", form.getInputByName("submit"));
	}
	
	public void testGetAnchors() {
		assertNotNull("get anchor by href", page.getAnchorByURL("b.*r"));
		assertNotNull("get anchor by name", page.getAnchorByName("oo"));
		assertNotNull("get anchor by text", page.getAnchorByText("ext"));
	}
	
	public void testGetTitle() {
		assertEquals("getTitle", "Fool", page.getTitleText());
	}
}
