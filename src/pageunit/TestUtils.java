package pageunit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.httpclient.HttpStatus;

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
	 * @param targetHost
	 * @param targetPort
	 * @param targetPage
	 * @return
	 * @throws MalformedURLException
	 */
	public static URL qualifyURL(final String targetHost, final int targetPort, String targetPage) throws MalformedURLException {
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

	public static URL completeURL(String u) throws MalformedURLException {
		return completeURL(new URL(u));
	}

}
