package regress.webtest;

import junit.framework.TestCase;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

import regress.http.Util;

/**
 * Trying to build a usable test engine using JUnit and Jakarta HttpClient directly.
 * @version $Id$
 */
public class TestTest extends TestCase {
	
	private static final String TARGET_PATH = "/Jsp/view/PersonList.jsp";
	
	/** Test that we can get to the index page correctly.
	 * @throws Exception
	 */
	public void NOtestIndexPage() throws Exception {
		System.out.println("TestTest.testIndexPage()");
		HttpClient session = new HttpClient();
		HttpMethod res = TestUtils.getSimplePage(session, "localhost", 8080, "/index.jsp");
		assertEquals("index page load", 200, res.getStatusCode());
		assertTrue("contains title", TestUtils.checkResultForPattern(res.getResponseBodyAsString(),
				"Toronto Centre for Phenogenomics"));
	}
	
	/** Test that a good login and password gets us past the login screen.
	 * @throws Exception
	 */
	public void testGoodLogin() throws Exception {
		System.out.println("TestTest.testGoodLogin()");

		String login = Util.getInstance().getProperty("admin_login");
		String pass = Util.getInstance().getProperty("admin_passwd");
		HttpClient session = TestUtils.getHttpClient(login, pass);

		assertNotNull("login", login);
		assertNotNull("pass", pass);

		HttpMethod result = TestUtils.getProtectedPage(session, "localhost", 8080,
				TARGET_PATH, login, pass);
		int statusCode = result.getStatusCode();
		assertEquals("login code", 200, statusCode);
		Header header = result.getResponseHeader("location");
		if (header != null) {
			String redirect = header.getValue();
			assertEquals("Good login status", TARGET_PATH, redirect);
		}
		
		TestUtils.doLogout(session);
	}

	/** Test that a bad login redirects back to the login page.
	 */
	public void NOtestBadLogin() throws Exception {
		System.out.println("TestTest.testBadLogin()");
		HttpClient session = new HttpClient();

		String login = "uttar nan sense";
		String pass = "complete gibberish";

		HttpMethod result = TestUtils.getProtectedPage(session, "localhost", 8080, TARGET_PATH, login, pass);
		assertTrue("Bad login status", result.getPath().indexOf("/login.jsp") != -1);
		
	}
}
