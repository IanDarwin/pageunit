package regress.webtest;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

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
		HttpClient session = new HttpClient();
		String host = TestUtils.getProperty("host");
		int port = TestUtils.getIntProperty("port");
		HttpMethod res = TestUtils.getSimplePage(session, host, port, "/index.jsp");
		
		assertEquals("index page load", 200, res.getStatusCode());
		
		assertTrue("contains title", TestUtils.checkResultForPattern(res.getResponseBodyAsString(),
				"Toronto Centre for Phenogenomics"));
	}
}
