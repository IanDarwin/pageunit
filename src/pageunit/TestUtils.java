package regress.webtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Trying to build a simple but usable test engine out of JUnit and Jakarta HttpClent
 * 
 * @version $Id$
 */
public class TestUtils {
	
	private static final String TCPTEST_PROPERTIES_FILENAME = ".tcptest.properties";
	private static Properties props  = new Properties();
	private static boolean debug;
	
	static {
		String home = System.getProperty("user.home");
		String propsFileName = home + File.separator +TCPTEST_PROPERTIES_FILENAME;
		
		try {
			props.load(new FileInputStream(propsFileName));
		} catch (IOException ex) {
			System.err.println("Can't load " + propsFileName);
		}
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
		if (debug) {
			System.out.println("Initial Page get: "
				+ initialGet.getStatusLine().toString());
		}

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
		if (debug) {
				System.out.println("Initial Page get: "
				+ interaction.getStatusLine().toString());
		}
		int statusCode = interaction.getStatusCode();
        
		if (!isRedirectCode(statusCode)) {
			throw new IllegalStateException("Requested page did not redirect");
		}

		// shortcut: instead of parsing the form, we "know" that
		// J2EE container-managed-security always uses this hard-coded URL:
		PostMethod loginPost = new PostMethod("/j_security_check");
		loginPost.setFollowRedirects(false);

		NameValuePair[] request = {
				new NameValuePair("j_username", login),
				new NameValuePair("j_password", pass)
		};
		loginPost.setRequestBody(request);

		statusCode = session.executeMethod(loginPost); // SEND THE LOGIN
		if (debug) {
			System.out.println("Login return " + loginPost.getStatusLine());
		}

		// Should be yet another redirect, back to original request page
		if (!isRedirectCode(statusCode)) {
			throw new IllegalStateException("Login page did not redirect");
		}
		String redirectURL = getRedirectURL(loginPost);
		if (debug) {
			System.out.println("Login page redirects to " + redirectURL);
		}
		interaction = new GetMethod(redirectURL);
		session.executeMethod(interaction);
		interaction.setFollowRedirects(false);
		
		return interaction;
	}

	/**
	 * @param interaction
	 * @return
	 */
	public static String getRedirectURL(HttpMethod interaction) {
		
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
		if (debug) {
			System.out.println("Logout status: " + statusCode);
		}
	}

	/** Test the input against a pattern.
	 * @param sb The input sequence
	 * @param expect The string (which can be a Java 1.4 regex).
	 */
	public static boolean checkResultForPattern(CharSequence sb, String expect) {
		Pattern pE = Pattern.compile(expect);
		Matcher mE = pE.matcher(sb);
		return mE.find();
	}
	

	
	/** Retrieve a property from Util, either from the System Properties (consulted first, to allow overriding on the command line)
	 * or in the user's property file (${user.home} + TCPTEST_PROPERTIES_FILENAME);
	 * @param property the key to look up
	 * @return The value corresponding to the given key.
	 */
	public static String getProperty(String property) {
		if (System.getProperty(property) != null)
			return System.getProperty(property);
		String s = props.getProperty(property);
		return s;
	}

	/**
	 * @param string
	 * @return
	 */
	public static int getIntProperty(String string) {
		String intStr = getProperty(string);
		if (intStr == null) {
			throw new IllegalArgumentException("getIntProperty: " + string + " does not exist");
		}
		return Integer.parseInt(intStr);
	}
	
	/**
	 * @return Returns the debug.
	 */
	public static boolean isDebug() {
		return debug;
	}
	/**
	 * @param debug The debug to set.
	 */
	public static void setDebug(boolean debug) {
		TestUtils.debug = debug;
	}
}
