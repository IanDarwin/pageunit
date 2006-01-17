package pageunit.linkchecker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pageunit.html.HTMLComponent;
import pageunit.html.HTMLParseException;

/** A simple HTML Link Checker. 
 * Needs Properties to set depth, URLs to check. etc.
 * Responses not adequate; need to check at least for 404-type errors!
 * When all that is (said and) done, display in a Tree instead of a TextArea.
 * Then use Color coding to indicate errors.
 * <p>
 * TODO: convert to use more of PageUnit HTML parsing framework.
 *
 * @author Ian Darwin, Darwin Open Systems, www.darwinsys.com.
 * @version $Id$
 */
public class LinkChecker {

	protected static int indent = 0;
	protected static Map<String,Object> urlCache = new HashMap<String,Object>();
	private final static Object BEEN_THERE_DONE_THAT = new Object();
  
	/** Start checking, given a URL by name.
	 * Calls checkLink to check each link.
	 */
	public synchronized static void checkStartingAt(String rootURLString) throws IOException {
		URL rootURL = null;
		
		if (rootURLString == null) {
			System.out.println("checkOut(null) isn't very useful");
			return;
		}
		if (urlCache.get(rootURLString) == BEEN_THERE_DONE_THAT) {
			return;	// already visited
		}
		urlCache.put(rootURLString, BEEN_THERE_DONE_THAT);

		// Open the root URL for reading. May be a filename or a real URL.
		try {
			try {
				rootURL = new URL(rootURLString);
			} catch (MalformedURLException e) {
				// If not a valid URL, try again as a file.
				rootURL = new File(rootURLString).toURL();
			}
		} catch (IOException e) {
			System.out.println("openStream " + rootURLString + " " + e + "\n");
			return;
		}
		checkStartingAt(rootURL);		
	}
	
	public synchronized static void checkStartingAt(URL rootURL) throws IOException {
		System.out.printf("LinkChecker.checkStartingAt(%s)%n", rootURL);
		
		String rootURLString = rootURL.toString();
		// If we're still here, the root URL given is OK.
		// Next we make up a "directory" URL from it.
		String rootURLdirString;
		if (rootURLString.endsWith("/") ||
			rootURLString.endsWith("\\"))
				rootURLdirString = rootURLString;
		else {
			rootURLdirString = rootURLString.substring(0, 
				rootURLString.lastIndexOf('/'));	// XXX or \
		}

		try {
			List<HTMLComponent> urlTags = new HTMLParser().parse(new InputStreamReader((InputStream)rootURL.getContent()));
			for (HTMLComponent tag : urlTags) {
				System.out.printf("TAG %s%n", tag);
						
				String href = extractHREF(tag.toString());

				for (int j=0; j<indent; j++)
					System.out.println("\t");
				System.out.println(href + " -- ");

				// Can't really validate these!
				if (href == null) {
					System.out.println(" null? !!\n");
					continue;
				}
				if (href.startsWith("mailto:")) {
					System.out.println(href + " -- not checking\n");
					continue;
				}

				if (href.startsWith("..") || href.startsWith("#")) {
					System.out.println(href + " -- not checking\n");
					// nothing doing!
					continue; 
				}

				URL hrefURL = new URL(rootURL, href);

				// TRY THE URL.
				// (don't combine previous System.out.println with this one,
				// since this one can throw an exception)
				System.out.println(checkOneLine(hrefURL));

				// There should be an option to control whether to
				// "try the url" first and then see if off-site, or
				// vice versa, for the case when checking a site you're
				// working on on your notebook on a train in the Rockies
				// with no web access available.

				// Now see if the URL is off-site.
				if (!hrefURL.getHost().equals(rootURL.getHost())) {
					System.out.println("-- OFFSITE -- not following\n");
					continue;
				}
				System.out.println();

				// If HTML, check it recursively. No point checking
				// PHP, CGI, JSP, etc., since these usually need forms input.
				// If a directory, assume HTML or something under it will work.
				if (href.endsWith(".htm") ||
					href.endsWith(".html") ||
					href.endsWith("/")) {
						++indent;
						if (href.indexOf(':') != -1)
							checkStartingAt(href);			// RECURSE
						else {
							String newRef = 
								 rootURLdirString + '/' + href;
							checkStartingAt(newRef);		// RECURSE
						}
						--indent;
				}
			}
		} catch (IOException e) {
			System.err.println("Error: (" + e +")");
		} catch (HTMLParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** Check one link, given its DocumentBase and the tag */
	public static String checkOneLine(URL linkURL) {
		System.out.printf("LinkChecker.checkLink(%s)%n", linkURL);
		try { 
			// Open it; if the open fails we'll likely throw an exception
			URLConnection luf = linkURL.openConnection();
			if (linkURL.getProtocol().equals("http")) {
				HttpURLConnection huf = (HttpURLConnection)luf;
				String s = huf.getResponseCode() + " " + huf.getResponseMessage();
				if (huf.getResponseCode() == -1)
					return "Server error: bad HTTP response";
				return s;
			} else if (linkURL.getProtocol().equals("file")) {
				InputStream is = luf.getInputStream();
				is.close();
				// If that didn't throw an exception, the file is probably OK
				return "(File)";
			} else
				return "(non-HTTP)";
		}
		catch (SocketException e) {
			return "DEAD: " + e.toString();
		}
		catch (IOException e) {
			return "DEAD";
		}
    }
 
	/** Extract the URL from <sometag attrs HREF="http://foo/bar" attrs ...> 
	 * We presume that the HREF is correctly quoted!!!!!
	 * TODO: Handle Applets.
	 */
	public static String extractHREF(String tag) throws MalformedURLException {
		String caseTag = tag.toLowerCase(), attrib;
		int p1, p2, p3, p4;

		if (caseTag.startsWith("<a") && 
			Character.isWhitespace(caseTag.charAt(2))) {
			attrib = "href";		// A
		} else if (caseTag.startsWith("<applet ")){
			attrib = "code";
		} else
			attrib = "src";			// image, frame
		// XXX refactor to use 1.5 enum here
		if (attrib.equals("href") && caseTag.indexOf("name") != -1) {
			return null;		// silently ignore <a name=...>
		}
		p1 = caseTag.indexOf(attrib);
		if (p1 < 0) {
			throw new MalformedURLException("Can't find " + attrib + " in " + tag);
		}
		p2 = tag.indexOf ("=", p1);

		// This fails to handle unquoted href, which some dinosaurs insist
		// on using, saying the parser can sort it out. Phhhhhhhht!!!!
		// XXX should handle single-quoted hrefs here
		p3 = tag.indexOf("\"", p2);
		p4 = tag.indexOf("\"", p3+1);
		if (p3 < 0 || p4 < 0) {
			throw new MalformedURLException("Invalid " + attrib + " in " + tag);
		}
		String href = tag.substring(p3+1, p4);
		return href;
	}
}
