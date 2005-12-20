package pageunit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpStatus;

import pageunit.http.WebSession;
import pageunit.http.WebResponse;
import pageunit.html.HTMLForm;
import pageunit.html.HTMLPage;

/**
 * Trying to build a simple but usable test engine out of JUnit and Jakarta HttpClent
 * 
 * @version $Id$
 */
public class TestUtils {
	
	private static final String PAGEUNIT_PROPERTIES_FILENAME = ".pageunit.properties";
	private static Properties props  = new Properties();
	private static boolean debug;
	
	static {
		String home = System.getProperty("user.home");
		String propsFileName = home + File.separator + PAGEUNIT_PROPERTIES_FILENAME;
		
		try {
			props.load(new FileInputStream(propsFileName));
		} catch (IOException ex) {
			final String message = "Can't load " + propsFileName;
			System.err.println(message);
			throw new RuntimeException(message);
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
	public static HTMLPage getSimplePage(WebSession webClient,
			String targetHost, int targetPort, String targetPage)
			throws IOException {

		if (!targetPage.startsWith("/")) {
			System.err.println("Warning: link " + targetPage + ": leading slash added, this is a browser");
			targetPage = "/" + targetPage;
		}

		final URL url = new URL("http", targetHost, targetPort, targetPage);
		
		return getSimplePage(webClient, url);

	}
	
	/**
	 * Get an unprotected page given its URL
	 * @param webclient
	 * @param newLocation
	 * @return
	 */
	public static HTMLPage getSimplePage(WebSession session, URL url) throws IOException {
		final HTMLPage page = (HTMLPage) session.getPage(url);
		System.out.println("Got to simple page: " + session.getWebResponse().getUrl());
		
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
	public static HTMLPage getProtectedPage(WebSession webClient,
			final String targetHost, final int targetPort,
			/* not final */String targetPage, final String login,
			final String pass) throws IOException {
		
		if (!targetPage.startsWith("/")) {
			System.err.println("Warning: link " + targetPage + ": leading slash added, this is a browser");
			targetPage = "/" + targetPage;
		}

		final URL url = new URL("http", targetHost, targetPort, targetPage);
		
		// request protected page, and let HtmlUnit handle redirection here.
		final HTMLPage page1 = (HTMLPage) webClient.getPage(url);	// Ask for one page, really get login page
		
		if (debug) {
				System.out.println("Protected Page get: " + page1.getTitleText());
		}
		WebResponse interaction = page1.getWebResponse();
		int statusCode = interaction.getStatusCode();
        if (debug) {
        	System.out.println("Protected Page Get status: " + statusCode);
        }

		HTMLForm form = page1.getFormByName("loginForm");	// dependency on our form page

		form.getInputByName("j_username").setValueAttribute(login);
		form.getInputByName("j_password").setValueAttribute(pass);
		
		HTMLPage formResultsPage = (HTMLPage)form.submit();   // SEND THE LOGIN
		if (debug) {
			System.out.println("Login return " + formResultsPage.getTitleText());
		}

		// Should be yet another redirect, back to original request page
		WebResponse res2 = formResultsPage.getWebResponse();
		statusCode = res2.getStatusCode();

		return formResultsPage;	// HtmlUnit handles redirection for us
	}
	
	/**
	 * Return true iff the given status code is one that indicates redirection, e.g., 3xx codes.
	 * @param formResultsPage
	 * @return The redirect location, or null
	 */
	public static boolean isRedirectCode(int statusCode) {
		return (statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
				|| (statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
				|| (statusCode == HttpStatus.SC_SEE_OTHER)
				|| (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT);
	}

	public static boolean isErrorCode(int statusCode) {
		int group = statusCode / 100;
		switch(group) {
		case 4: case 5:
			return true;
		default:
			return false;
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
	 * @return Returns the debug level.
	 */
	public static boolean isDebug() {
		return debug;
	}
	/**
	 * @param debug The debug level to set.
	 */
	public static void setDebug(boolean debug) {
		TestUtils.debug = debug;
	}




}
