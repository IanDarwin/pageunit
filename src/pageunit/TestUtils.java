package regress.webtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Trying to build a simple but usable test engine out of JUnit and Jakarta HttpClent
 * 
 * @version $Id$
 */
public class TestUtils {
	
	private static final String TCPTEST_PROPERTIES_FILENAME = ".tcptest.properties";
	private static Properties props  = new Properties();
	private static boolean debug;
	private static HttpState state;
	
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
	 *            The HTTP Session
	 * @param targetHost
	 *            The name (or maybe IP as a String) for the host
	 * @param targetPort
	 *            The port number, 80 for default
	 * @param targetPage
	 *            The pathname part of the URL
	 * @return An HttpMethod object containing the response.
	 * @throws IOException
	 */
	public static HtmlPage getSimplePage(WebClient webClient,
			String targetHost, int targetPort, String targetPage)
			throws IOException {

		if (!targetPage.startsWith("/")) {
			System.err.println("Warning: link " + targetPage + ": leading slash added, this is a browser");
			targetPage = "/" + targetPage;
		}

		final URL url = new URL("http", targetHost, targetPort, targetPage);
		final HtmlPage page = (HtmlPage) webClient.getPage(url);
		System.out.println("Got to page: " + page.getTitleText());
		
		return page;
	}
	


	/**
	 * Get an HTML page that is protected by J2EE Container-based Forms
	 * Authentication.
	 * 
	 * @param session
	 *            The HTTP Session
	 * @param targetHost
	 *            The name (or maybe IP as a String) for the host
	 * @param targetPort
	 *            The port number, 80 for default
	 * @param targetPage
	 *            The pathname part of the URL
	 * @return An HttpMethod object containing the response.
	 * @throws IOException
	 */
	public static HtmlPage getProtectedPage(WebClient webClient,
			final String targetHost, final int targetPort,
			/* not final */String targetPage, final String login,
			final String pass) throws IOException {
		
		if (!targetPage.startsWith("/")) {
			System.err.println("Warning: link " + targetPage + ": leading slash added, this is a browser");
			targetPage = "/" + targetPage;
		}

		final URL url = new URL("http", targetHost, targetPort, targetPage);
		
		// request protected page, and handle redirection here.
		final HtmlPage page = (HtmlPage) webClient.getPage(url);
		
		if (debug) {
				System.out.println("Initial Page get: "
				+ page.getTitleText());
		}
		WebResponse interaction = page.getWebResponse();
		int statusCode = interaction.getStatusCode();
        System.out.println("XXX " + statusCode);
		if (statusCode == 200) {
			System.err.println("protected page " + targetPage + " did not require login");
			return page;
		}
		
		if (!isRedirectCode(statusCode)) {
			throw new IllegalStateException("Requested page did not redirect");
		}

		// shortcut: instead of parsing the form, we "know" that
		// the J2EE login page will have only one form.

		HtmlForm form = (HtmlForm)page.getAllForms().get(0); 
		if (!"/j_security_check".equals(form.getTargetAttribute())) {
			throw new IOException("expected J2EE login form but got " + form.getTargetAttribute());
		}
		
		form.getInputByName("j_username").setValueAttribute(login);
		form.getInputByName("j_password").setValueAttribute(pass);
		
		HtmlPage formResultsPage = (HtmlPage)form.submit();   // SEND THE LOGIN
		if (debug) {
			System.out.println("Login return " + formResultsPage.getTitleText());
		}

		// Should be yet another redirect, back to original request page
		WebResponse res2 = formResultsPage.getWebResponse();
		statusCode = res2.getStatusCode();
		if (!isRedirectCode(statusCode)) {
			throw new IllegalStateException("Login page did not redirect");
		}
		
		final String redirectURL = getRedirectURL(formResultsPage);
		if (debug) {
			System.out.println("Login page redirects to " + redirectURL);
		}
		final URL url3 = new URL("http", targetHost, targetPort, targetPage);
		
		// request protected page, and handle redirection here.
		final HtmlPage page3 = (HtmlPage) webClient.getPage(url3);
		
		if (debug) {
				System.out.println("Redirect got: "
				+ page3.getTitleText());
		}
		return page3;
	}
	
	/**
	 * Return the redirect location from the given response page
	 * @param formResultsPage
	 * @return The redirect location, or null
	 */
	public static String getRedirectURL(HtmlPage page)  {
	
		WebResponse resp = page.getWebResponse();
		
		String redirectLocation = resp.getResponseHeaderValue("location");

		if (redirectLocation.equals("")) {
			redirectLocation = "/";
		}
		return redirectLocation;
	}

	static boolean isRedirectCode(int statusCode) {
		return (statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
				|| (statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
				|| (statusCode == HttpStatus.SC_SEE_OTHER)
				|| (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT);
	}

	public static void doLogout(WebClient webClient) throws Exception {

		String logoutURL = "/LogoutServlet";

//		final HtmlPage page = (HtmlPage) webClient.getPage("/");
//		System.out.println("Got to page: " + page.getTitleText());
//		GetMethod logoutGet = new GetMethod("/LogoutServlet");
//		
//		int statusCode = session.executeMethod(logoutGet);
//		if (debug) {
//			System.out.println("Logout status: " + statusCode);
//		}
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
	

	
	/** Retrieve a property, either from the System Properties (consulted first, to allow overriding on the command line)
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
