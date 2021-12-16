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
import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import pageunit.Utilities;
import pageunit.html.HTMLAnchor;
import pageunit.html.HTMLComponent;
import pageunit.html.HTMLForm;
import pageunit.html.HTMLIMG;
import pageunit.html.HTMLParseException;

/** A simple HTML Link Checker. 
 * Should have Properties to set depth, URLs to check. etc.
 * Responses not adequate; need to check at least for 404-type errors!
 * <br>
 * XXX Move cache into a ThreadLocal, then un-synchronize all the methods.
 * @author Ian Darwin, http://darwinsys.com/
 */
public class LinkChecker {

	private static int indent = 0;
	final static List<String> cache = new ArrayList<String>();
	private static boolean verbose = false;
  
	/**
	 * Start checking, given a URL by name.
	 * Calls checkLink to check each link.
	 * @param rootURLString Where to start checking
	 * @throws IOException if the reading fails
	 */
	public synchronized static void checkStartingAt(String rootURLString) throws IOException {
		URL rootURL = null;
		
		if (rootURLString == null) {
			System.out.println("check(null) isn't very useful");
			return;
		}

		try {
			rootURL = new URL(rootURLString);
		} catch (MalformedURLException e) {
			// If not a valid URL, try again as a file.
			rootURL = new File(rootURLString).toURL();
		}
		System.out.printf("LinkChecker.checkStartingAt(%s)%n", rootURL);
		
		try {
			List<HTMLComponent> urlTags = new LinkExtractor().parse(new InputStreamReader((InputStream)rootURL.getContent()));
			for (HTMLComponent tag : urlTags) {
				// System.out.printf("TAG %s%n", tag);
						
				String href = null;
				if (tag instanceof HTMLAnchor) {
					href = ((HTMLAnchor)tag).getURL();
				}
				if (tag instanceof HTMLForm) {
					href = ((HTMLForm)tag).getAction();
				}
				if (tag instanceof HTMLIMG) {
					href = ((HTMLIMG)tag).getSrc();
				}				
		
				// Can't really validate these!
				if (href == null) {
					System.out.println("href is null?!!\n");
					continue;
				}
		
				if (href.equals("/") || href.startsWith("..") || href.startsWith("#")) {
					if (verbose) {
						System.out.println(href + " -- not checking");
					}
					// nothing doing!
					continue; 
				}

				// We're gonna try it, so print indentation and the URL:
				for (int j=0; j<indent; j++) {
					System.out.println("\t");
				}
				System.out.print(href + " -- ");
				
				URL hrefURL = null;
				try {
					hrefURL = new URL(rootURL, href);
				} catch (MalformedURLException mue) {
					System.out.println(rootURL + "--" + href + ": invalid URL");
					continue;
				}
				if (hrefURL.getProtocol().equals("mailto:")) {
					System.out.println(href + " -- not checking");
					continue;
				}
		
				// Now see if the URL is off-site.
				if (!hrefURL.getHost().equals(rootURL.getHost())) {
					System.out.print("-- OFFSITE -- ");
					System.out.println(checkOneLink(hrefURL));
					continue;
				}
		
				// CHECK THE URL.
				System.out.println(checkOneLink(hrefURL));
		
				// If a directory, assume HTML or something under it will work.
				if (href.endsWith("/")) {
						++indent;
						if (href.indexOf(':') != -1) {
							checkStartingAt(href);			// RECURSE
						}
						--indent;
				}
			}
		} catch (IOException e) {
			System.err.println("IO Error: (" + e +")");
		} catch (HTMLParseException e) {
			System.err.println("HTML Parse Exception:");
			e.printStackTrace();
		}		
	}
	
	/**
	 * Check one link, given its DocumentBase and the tag
	 * @param linkURL The link to be checked
	 * @return a String containing the status
	 */
	public static synchronized String checkOneLink(URL linkURL) {
		// System.out.printf("LinkChecker.checkLink(%s)%n", linkURL);
		try { 
			final String canonURLString = URLCanonicalizer.getCanonicalURL(linkURL.toString());
			if (cache.contains(canonURLString))
				return "(already checked)";
			cache.add(canonURLString);
			linkURL = new URL(canonURLString);

			// Open it; if the open fails we'll likely throw an exception
			URLConnection luf = linkURL.openConnection();
			String proto = linkURL.getProtocol();
			if (proto.equals("http") || proto.equals("https")) {
				HttpURLConnection huf = (HttpURLConnection)luf;
				int response = huf.getResponseCode();
				if (response == -1)
					return "Server error: bad HTTP response";
				if (Utilities.isRedirectCode(response)) {
					String newUrl = huf.getHeaderField("location"); // XXX??
					return "(redirect to " + newUrl + ")" + ' ' + checkOneLink(new URL(newUrl));
				}
				return huf.getResponseCode() + " " + huf.getResponseMessage();
			} else if (proto.equals("file")) {
				// Only useful on localhost
				return "(File; ignored)";
			} else
				return "(non-HTTP; ignored)";
		}
		catch (SocketException e) {
			return "DEAD: " + e.toString();
		}
		catch (IOException e) {
			return "DEAD";
		}
    }
}
