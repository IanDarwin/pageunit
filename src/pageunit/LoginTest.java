package pageunit;

import junit.framework.TestCase;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Trying to build a usable test engine using JUnit and 
 * Jakarta HttpClient directly; just kept around as an existence
 * proof (this is NOT going to be part of the final pageunit package).
 * @version $Id$
 */
public class LoginTest extends TestCase {
	
	private static final String TARGET_PATH = "/view/PersonList.jsp";
	
	/** Test that a good login and password gets us past the login screen.
	 * @throws Exception
	 */
	public void testGoodJ2EELogin() throws Exception {
		System.out.println("TestTest.testGoodLogin()");

		String login = TestUtils.getProperty("admin_login");
		String pass = TestUtils.getProperty("admin_passwd");
		String host = TestUtils.getProperty("host");
		int port = TestUtils.getIntProperty("port");
		WebClient session = new WebClient();

		assertNotNull("login", login);
		assertNotNull("pass", pass);

		HtmlPage page = TestUtils.getProtectedPage(session, host, 8080,
				TARGET_PATH, login, pass);
		WebResponse result = page.getWebResponse();
		assertEquals("login code", 200, result.getStatusCode());
		System.out.println(result.getUrl());
		// assertEquals("login page", result.getPath(), TARGET_PATH);
		
	}

	/** Test that a bad login redirects back to the login page.
	 */
	public void testBadJ2EELogin() throws Exception {
		System.out.println("TestTest.testBadLogin()");
		WebClient session = new WebClient();

		String login = "uttar nan sense";
		String pass = "complete gibberish";

		HtmlPage result = TestUtils.getProtectedPage(session, "localhost", 8080, TARGET_PATH, login, pass);
		WebResponse resp = result.getWebResponse();
		final String path = resp.getUrl().getPath();
		assertTrue("Bad login status", path.indexOf("/login.jsp") != -1); // should wind up back here...
		
	}
}
