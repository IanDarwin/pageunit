package pageunit.html;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import pageunit.Utilities;

/** Test handling of META redirects */
@RunWith(Parameterized.class)
public class ParseMetaFullTest {

	final static Object[][] TESTDATA = {
		{"<html><head><meta http-equiv='Refresh' content='0; URL=home.seam'/></head></html>", 
			"http://androidcookbook.com:80/home.seam"}
	};

	private HTMLPage page;
	private String expected;
	
	@Parameters
	public static List<Object[]> getAsList() {
		return Arrays.asList(TESTDATA);
	}
	
	public ParseMetaFullTest(String input, String expected) throws Exception {
		this.page = new HTMLParser().parse(new StringReader(input));
		this.expected = expected;
	}
	
	@Test
	public void testFindMeta() throws Throwable {
		System.out.println("ParseMetaFullTest.testFindMeta()");
		assertNotNull(page);
		List<HTMLComponent> children = page.getChildren();
		
		for (HTMLComponent c : children) {
			if (c instanceof HTMLMeta) {
				System.out.println("Found: " + c);
				HTMLMeta m = (HTMLMeta)c;
				assertEquals("META equiv", "refresh", m.getMetaEquiv().toLowerCase());
				String baseUrl = m.getMetaContent().replaceAll(".*=", "");
				assertEquals(expected, Utilities.qualifyURL("http", "androidcookbook.com", 80, baseUrl).toExternalForm());
				return; // Only one instance
			}
		}
	}
}
