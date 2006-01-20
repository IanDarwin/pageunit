package pageunit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpStatus;

import pageunit.http.WebSession;
import pageunit.http.WebResponse;
import pageunit.html.HTMLForm;
import pageunit.html.HTMLPage;
import pageunit.html.HTMLParseException;

/**
 * Trying to build a simple but usable test engine out of JUnit and Jakarta HttpClent
 * 
 * @version $Id$
 */
public class TestUtils {
	
	private static final String PAGEUNIT_PROPERTIES_FILENAME = ".pageunit.properties";
	private static final String DEFAULT_PROTO = "http";
	private static final int DEFAULT_PORT = 80;
	private static final String DEFAULT_PATH = "/";
	
	/** A Standard Properties mechanism: the following props are expected:<br/>
	 * <pre>host=host.dom</pre>
	 * <p>The following properties are optional:<br/>
	 * <pre>port=8080 # defaults to 80
	 * login=myName
	 * password=myPassword</pre>
	 */
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
	 * @throws HTMLParseException 
	 */
	public static HTMLPage getSimplePage(WebSession webClient,
			String targetHost, int targetPort, String targetPage)
			throws IOException, HTMLParseException {

		final URL url = qualifyURL(targetHost, targetPort, targetPage);
		
		return getSimplePage(webClient, url);

	}
	
	/**
	 * Get an unprotected page given its URL
	 * @param webclient
	 * @param newLocation
	 * @return
	 * @throws HTMLParseException 
	 */
	public static HTMLPage getSimplePage(WebSession session, URL url) throws IOException, HTMLParseException {
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
	 * @throws HTMLParseException 
	 */
	public static HTMLPage getProtectedPage(WebSession session,
			final String targetHost, final int targetPort,
			/* not final */String targetPage, final String login,
			final String pass) throws IOException, HTMLParseException {
		
		final URL url = qualifyURL(targetHost, targetPort, targetPage);
		
		// request protected page, and let WebSession handle redirection here.
		final HTMLPage page1 = (HTMLPage) session.getPage(url);	// Ask for one page, really get login page
		
		if (debug) {
				System.out.println("Protected Page get: " + page1.getTitleText());
		}
		WebResponse interaction = session.getWebResponse();
		int statusCode = interaction.getStatus();
        if (debug) {
        	System.out.println("Protected Page Get status: " + statusCode);
        }

		HTMLForm form = page1.getFormByName("loginForm");	// dependency on our form page

		form.getInputByName("j_username").setValue(login);
		form.getInputByName("j_password").setValue(pass);
		
		HTMLPage formResultsPage = (HTMLPage)session.submitForm(form);   // SEND THE LOGIN
		if (debug) {
			System.out.println("Login return " + formResultsPage.getTitleText());
		}

		// Should be yet another redirect, back to original request page
		WebResponse res2 = session.getWebResponse();
		statusCode = res2.getStatus();

		return formResultsPage;	// HtmlUnit handles redirection for us
	}

	/**
	 * @param targetHost
	 * @param targetPort
	 * @param targetPage
	 * @return
	 * @throws MalformedURLException
	 */
	private static URL qualifyURL(final String targetHost, final int targetPort, String targetPage) throws MalformedURLException {
		final URL url;
		if (targetPage.startsWith("http:")) {
			url = new URL(targetPage);
		} else {
			if (!targetPage.startsWith("/")) {
				System.err.println("Warning: link " + targetPage + ": leading slash added, this is a browser");
				targetPage = "/" + targetPage;
			}
			url = new URL("http", targetHost, targetPort, targetPage);
		}
		return url;
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
		switch((statusCode / 100)) {
		case 4:
		case 5:
			return true;
		default:
			return false;
		}
	}
	
	/** Retrieve a property, either from the System Properties
	 * (consulted first, to allow overriding via command line -D)
	 * or in the user's property file (${user.home} + TCPTEST_PROPERTIES_FILENAME);
	 * @param key the key to look up
	 * @return The value corresponding to the given key.
	 */
	public static String getProperty(String key) {
		if (System.getProperty(key) != null)
			return System.getProperty(key);
		return props.getProperty(key);
	}
	
	public static int getIntProperty(String key) {
		String val = getProperty(key);
		if (val == null && "port".equals(key)) {
			return 80;
		}
		if (val == null) {
			throw new IllegalArgumentException(
				"getIntProperty: " + key + " does not exist");
		}
		return Integer.parseInt(val);
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

	/** Convert partial URLs to full URLS, providing defaults
	 * from getProperties() and then from baked-in defaults.
	 * @param u
	 * @return
	 * @throws MalformedURLException
	 */
	public static URL completeURL(URL u) throws MalformedURLException {
		String prot = u.getProtocol();
		if (prot == null)
			prot = DEFAULT_PROTO;
		String host = u.getHost();
		if (host == null)
			host = getProperty("host");
		int port = u.getPort();
		if (port == -1)
			port = getIntProperty("port");
		if (port /* still */ == -1) 
			port = DEFAULT_PORT;
		String path = u.getPath();
		if (path == null)
			path = DEFAULT_PATH;
		return new URL(prot, host, port, path);
	}

	public static URL completeURL(String u) {
		try {
			return completeURL(new URL(u));
		} catch (MalformedURLException e) {
			throw new RuntimeException("Invalid URL " + u);
		}
	}

}
