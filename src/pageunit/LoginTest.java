package regress.webtest;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

/**
 * Trying to build a usable test engine using JUnit and Jakarta HttpClient directly.
 * @version $Id$
 */
public class LoginTest extends TestCase {
	
	private static final String TARGET_PATH = "/Jsp/view/PersonList.jsp";
	
	/** Test that a good login and password gets us past the login screen.
	 * @throws Exception
	 */
	public void testGoodLogin() throws Exception {
		System.out.println("TestTest.testGoodLogin()");

		String login = TestUtils.getProperty("admin_login");
		String pass = TestUtils.getProperty("admin_passwd");
		String host = TestUtils.getProperty("host");
		int port = TestUtils.getIntProperty("port");
		HttpClient session = new HttpClient();

		assertNotNull("login", login);
		assertNotNull("pass", pass);

		HttpMethod result = TestUtils.getProtectedPage(session, host, 8080,
				TARGET_PATH, login, pass);
		assertEquals("login code", 200, result.getStatusCode());
		System.out.println(result.getPath());
		assertEquals("login page", result.getPath(), TARGET_PATH);
		
		TestUtils.doLogout(session);
	}

	/** Test that a bad login redirects back to the login page.
	 */
	public void testBadLogin() throws Exception {
		System.out.println("TestTest.testBadLogin()");
		HttpClient session = new HttpClient();

		String login = "uttar nan sense";
		String pass = "complete gibberish";

		HttpMethod result = TestUtils.getProtectedPage(session, "localhost", 8080, TARGET_PATH, login, pass);
		assertEquals("Bad login status", result.getPath(), "/loginfailure.jsp");
		
		TestUtils.doLogout(session);	  // show that logout is harmless if you're not logged in.
	}
}
