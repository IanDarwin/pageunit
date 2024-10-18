package pageunit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.Properties;

import com.darwinsys.util.VariableMap;

/**
 * Looking for a few good utils. Parts are pageunit-specific.
 */
public class Utilities {
	
	private static final String PAGEUNIT_PROPERTIES_FILENAME = ".pageunit.properties";
	public static final String PROP_USER = "USER";
	public static final String PROP_PASS = "PASS";
	public static final String PROP_PROTOCOL = "PROTOCOL";
	public static final String PROP_HOST = "HOST";
	public static final String PROP_PORT = "PORT";
	public static final String PROP_DEBUG = "DEBUG";
	private static final String DEFAULT_PROTO = "http";
	private static final int DEFAULT_PORT = 80;
	private static final String DEFAULT_PATH = "/";
	
	/** A Standard Properties mechanism: the following props are expected:<br>
	 * <pre>HOST=host.dom</pre>
	 * <p>The following properties are optional:<br>
	 * <pre>PORT=8080 # defaults to 80
	 * USER=myName
	 * PASS=myPassword</pre>
	 */
	private static Properties props  = new Properties();
	private static boolean debug;
	
	static {
		String home = System.getProperty("user.home");
		String propsFileName = home + File.separator + PAGEUNIT_PROPERTIES_FILENAME;
		try {
			props.load(new FileInputStream(propsFileName));
			if (getProperty(PROP_DEBUG) != null) {
				setDebug(Boolean.parseBoolean(getProperty(PROP_DEBUG)));
			}
		} catch (IOException ex) {
			final String message = "Warning: Can't load " + propsFileName;
			System.err.println(message);
		}
	}

	public static URL qualifyURL(final VariableMap map, final String target) throws MalformedURLException {
		String protocol = map.getVar(PROP_PROTOCOL, "https");
		String targetHost = map.getVar(PROP_HOST, "localhost");
		int targetPort = map.getIntVar(PROP_PORT, 80);
		return qualifyURL(protocol, targetHost, targetPort, target);
	}
	
	/**
	 * Qualify a URL which may be relative or absolute
	 * @param targetHost The hostname
	 * @param targetPort The port number
	 * @param targetPage The destination
	 * @return The final URL
	 * @throws MalformedURLException If the inputs don't add up to a valid URL
	 */
	public static URL qualifyURL(final String protocol, final String targetHost, final int targetPort, String targetPage) throws MalformedURLException {
		final URL url;
		if (targetPage.startsWith("http:") || targetPage.startsWith("https:")) {
			url = new URL(targetPage);
		} else {
			if (!targetPage.startsWith("/")) {
				// System.err.println("Warning: link " + targetPage + ": leading slash added");
				targetPage = "/" + targetPage;
			}
			url = new URL(protocol, targetHost, targetPort, targetPage);
		}
		return url;
	}
	
	/**
	 * Return true iff the given status code is one that indicates redirection, e.g., 3xx codes.
	 * @param statusCode The numeric HTTP status code (200, 404, ...)
	 * @return true if the status code is a redirect code
	 */
	public static boolean isRedirectCode(int statusCode) {
		return (statusCode == HttpURLConnection.HTTP_MOVED_TEMP)
				|| (statusCode == HttpURLConnection.HTTP_MOVED_PERM)
				|| (statusCode == HttpURLConnection.HTTP_SEE_OTHER);
	}

	public static boolean isErrorCode(int statusCode) {
		return statusCode >= 400;
	}
	
	/** Retrieve a property, either from the System Properties
	 * (consulted first, to allow overriding via command line -D)
	 * or in the user's property file (loaded in the static block above).
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
		if (val == null && PROP_PORT.equals(key)) {
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
		Utilities.debug = debug;
	}

	/** Convert partial URLs to full URLS, providing defaults
	 * from getProperties() and then from baked-in defaults.
	 * @param u The partial URL to be converted
	 * @return The completed URL
	 * @throws MalformedURLException If the partial URL doen't cut it.
	 */
	public static URL completeURL(URL u) throws MalformedURLException {
		String prot = u.getProtocol();
		if (prot == null)
			prot = DEFAULT_PROTO;
		String host = u.getHost();
		if (host == null)
			host = getProperty(PROP_HOST);
		int port = u.getPort();
		if (port == -1)
			port = getIntProperty(PROP_PORT);
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

	public static Properties getProperties() {		
		return props;
	}
}
