package regress.webtest;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Trying to build a simple but usable test engine out of JUnit and Jakarta HttpClent
 * 
 * @version $Id$
 */
public class TestUtils {
	
	public static HttpClient getHttpClient(String username, String pass) {
		HttpClient client = new HttpClient();
		Credentials creds = new UsernamePasswordCredentials(username, pass);
		client.getState().setCredentials(AuthScope.ANY, creds);
		return client;
	}

	/**
	 * Get an unprotected page
	 * 
	 * @param session
	 *            The HTTP Session (HttpClient object)
	 * @param targetHost
	 *            The name (or maybe IP as a String) for the host
	 * @param targetPort
	 *            The port number, 80 for default
	 * @param targetPage
	 *            The pathname part of the URL
	 * @return An HttpMethod object containing the response.
	 * @throws IOException
	 */
	public static HttpMethod getSimplePage(HttpClient session,
			String targetHost, int targetPort, String targetPage)
			throws IOException {

		session.getHostConfiguration().setHost(targetHost, targetPort, "http");

		GetMethod initialGet = new GetMethod(targetPage);

		session.executeMethod(initialGet); // request protected page.
		System.out.println("Initial Page get: "
				+ initialGet.getStatusLine().toString());

		return initialGet;
	}

	/**
	 * Get an HTML page that is protected by J2EE Container-based Forms
	 * Authentication.
	 * 
	 * @param session
	 *            The HTTP Session (HttpClient object)
	 * @param targetHost
	 *            The name (or maybe IP as a String) for the host
	 * @param targetPort
	 *            The port number, 80 for default
	 * @param targetPage
	 *            The pathname part of the URL
	 * @return An HttpMethod object containing the response.
	 * @throws IOException
	 */
	public static HttpMethod getProtectedPage(HttpClient session,
			final String targetHost, final int targetPort,
			/* not final */String targetPage, final String login,
			final String pass) throws IOException {

		session.getHostConfiguration().setHost(targetHost, targetPort, "http");

		// Set the credentials here in case there is Basic Auth used.
		Credentials creds = new UsernamePasswordCredentials(login, pass);
		session.getState().setCredentials(AuthScope.ANY, creds);

		HttpMethod interaction = new GetMethod(targetPage);
		interaction.setFollowRedirects(false);

		session.executeMethod(interaction); // request protected page, and
											// handle redirection here.
		System.out.println("Initial Page get: "
				+ interaction.getStatusLine().toString());
		int statusCode = interaction.getStatusCode();

		CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
		Cookie sessionCookie = null;
        Cookie[] initcookies = cookiespec.match(
            targetHost, targetPort, "/", false, session.getState().getCookies());
        for (int i = 0; i < initcookies.length; i++) {
			Cookie cookie = initcookies[i];
			if (cookie.getPath().equals("JSESSIONID"))
				sessionCookie  = cookie;
				break;
		}
        
//		if (!isRedirectCode(statusCode)) {
//			throw new IllegalStateException("Requested page did not redirect");
//		}
//		String newuri = getRedirectURL(interaction);
//		
//		// Grab the form just to keep the J2EE happy??
//		session.executeMethod(new GetMethod(newuri));
		
		// shortcut: instead of parsing the form, we "know" that
		// J2EE container-managed-security always uses this hard-coded URL:
		PostMethod loginPost = new PostMethod("/j_security_check");
		loginPost.setFollowRedirects(false);
		session.getState().addCookie(sessionCookie);
		loginPost.addRequestHeader("Referer", "http://localhost:8080/login.jsp"); // XXX

		NameValuePair[] request = {
				new NameValuePair("j_username", login),
				new NameValuePair("j_password", pass)
		};
		loginPost.setRequestBody(request);

		statusCode = session.executeMethod(loginPost); // SEND THE LOGIN
		System.out.println("Login return " + loginPost.getStatusLine());

		// Should be yet another redirect, back to original request page
		if (!isRedirectCode(statusCode)) {
			throw new IllegalStateException("Login page did not redirect");
		}
		String redirectURL = getRedirectURL(loginPost);
		System.out.println("Login page redirects to " + redirectURL);
		interaction = new GetMethod(redirectURL);
		session.executeMethod(interaction);
		interaction.setFollowRedirects(false);
		
		return interaction;
	}

	/**
	 * @param interaction
	 * @return
	 */
	private static String getRedirectURL(HttpMethod interaction) {
		Header header = interaction.getResponseHeader("location");
		if (header == null) {
			throw new IllegalStateException("no redirect location found");
		}
		String newuri = header.getValue();
		if ((newuri == null) || (newuri.equals(""))) {
			newuri = "/";
		}
		return newuri;
	}

	static boolean isRedirectCode(int statusCode) {
		return (statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
				|| (statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
				|| (statusCode == HttpStatus.SC_SEE_OTHER)
				|| (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT);
	}

	public static void doLogout(HttpClient session) throws Exception {

		GetMethod logoutGet = new GetMethod("/LogoutServlet");
		int statusCode = session.executeMethod(logoutGet);
		System.out.println("Logout status: " + statusCode);
	}

	/**
	 * @param sb
	 */
	public static boolean checkResultForPattern(CharSequence sb, String expect) {
		Pattern pE = Pattern.compile(expect);
		Matcher mE = pE.matcher(sb);
		return mE.find();
	}
}
