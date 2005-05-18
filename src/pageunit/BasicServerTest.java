package pageunit;

import junit.framework.TestCase;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Just confirm that the server is running.
 * 
 * @version $Id$
 */
public class BasicServerTest extends TestCase {
	
	/** Test that we can get to the index page correctly.
	 * @throws Exception
	 */
	public void testIndexPage() throws Exception {
		System.out.println("TestTest.testIndexPage()");
		WebClient session = new WebClient();
		String host = TestUtils.getProperty("host");
		int port = TestUtils.getIntProperty("port");
		
		HtmlPage page = TestUtils.getSimplePage(session, host, port, "/index.jsp");
		
		WebResponse resp = page.getWebResponse();
		assertEquals("index page load", 200, resp.getStatusCode());
		
		assertTrue("contains title", TestUtils.checkResultForPattern(resp.getContentAsString(),
				"Toronto Centre for Phenogenomics"));
	}
}
