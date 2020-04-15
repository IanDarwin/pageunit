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

/** Test handling of META redirects */
@RunWith(Parameterized.class)
public class ParseTestMeta {

	final static Object[][] TESTDATA = {
		{"<html><head><META HTTP-EQUIV=\"Refresh\" content=\"0; URL=barcode_list.jsp\"><body>..."},
		{"<html><head><meta http-equiv=\"Refresh\" content=\"0; URL=barcode_list.jsp\"><body>..."},
		{"<html><head><meta http-equiv=\"refresh\" content=\"0; URL=barcode_list.jsp\"><body>..."}
	};

	private HTMLPage page;
	
	@Parameters
	public static List<Object[]> getAsList() {
		return Arrays.asList(TESTDATA);
	}
	
	public ParseTestMeta(String data) throws Exception {
		page = new HTMLParser().parse(new StringReader(data));
	}
	
	@Test
	public void testFindMeta() throws Throwable {
		System.out.println("ParserTest4.testFindMeta()");
		assertNotNull(page);
		List<HTMLComponent> children = page.getChildren();
		
		for (HTMLComponent c : children) {
			if (c instanceof HTMLMeta) {
				System.out.println("Found: " + c);
				HTMLMeta m = (HTMLMeta)c;
				assertEquals("META equiv", "refresh", m.getMetaEquiv().toLowerCase());
				assertEquals("META content", "0; URL=barcode_list.jsp", m.getMetaContent());
				return; // Only one instance
			}
		}
	}
}
