package regress.webtest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

import junit.framework.TestCase;

/**
 * Run the classes named in tests.txt
 * 
 * @version $Id$
 */
public class TestRunner extends TestCase {
	
	static List tests = new ArrayList();

	private static final String TESTS_FILE = "tests.txt";
	static {
		try {
			BufferedReader is = new BufferedReader(new FileReader(TESTS_FILE));
			String line;
			while ((line = is.readLine()) != null) {
				tests.add(line);
			}
		} catch (IOException e) {
			System.err.println("Cannot open " + TESTS_FILE);
			System.err.println(e);
			throw new IllegalArgumentException("Cannot open tests file");
		}
	}
	
	public void testListedTests() throws Exception {
		Iterator testsIterator = tests.iterator();
		HttpClient session = new HttpClient();
		HttpMethod theResult = null;
		String theLink = null;
		String login = TestUtils.getProperty("admin_login");
		assertNotNull("login", login);
		String pass = TestUtils.getProperty("admin_passwd");
		assertNotNull("pass", pass);
		String host = TestUtils.getProperty("host");
		assertNotNull("hostname", host);
		int port = TestUtils.getIntProperty("port");
		
		while (testsIterator.hasNext()) {
			String line = (String) testsIterator.next();
			if (line.length() == 0)
				continue;
			if (line.charAt(0) == '#')
				continue;
			System.out.println("TEST: " + line);
			StringTokenizer st = new StringTokenizer(line);
			if (st.countTokens() < 1) {
				throw new IOException("invalid line " + line);
			}
			String cmd = st.nextToken();
			if (cmd.length() != 1) {
				throw new IOException("invalid line " + line);
			}
			char c = cmd.charAt(0);
			String restOfLine = line.length() > 2 ? line.substring(2) : "";
			String page;


			switch(c) {
			case 'D':	// debug on/off
				char firstChar = restOfLine.charAt(0);
				if (firstChar == 't' || firstChar == '1') {
					TestUtils.setDebug(true);
				} else if (firstChar == 'f' || firstChar == '0')	{
					TestUtils.setDebug(false);
				} else {
					System.err.println("Warning: invalid Debug setting in " + line);
				}
			case 'U':	// get Unprotected page
				page = restOfLine;
				session = new HttpClient(); // XXX ??
				theResult = TestUtils.getSimplePage(session, host, port, page);
				assertEquals("unprotected page load", 200, theResult.getStatusCode());
				break;
			case 'P':	// get protected page
				theLink = null;
				page = restOfLine;
				session = new HttpClient(); // XXX ??
				theResult = TestUtils.getProtectedPage(session, host, port, page, login, pass);
				assertEquals("protected page code", 200, theResult.getStatusCode());
				assertEquals("protected page redirect", theResult.getPath(), page);
				break;
			case 'M':	// page contains text
				// PreCondition: theResult has been set by the U or P code above
				theLink = null;
				assertNotNull("Invalid test.txt: requested txt before getting page", theResult);
				assertTrue("page contains text", 
						TestUtils.checkResultForPattern(theResult.getResponseBodyAsString(), restOfLine));
				break;
			case 'T':	// page contains tag with text (in bodytext or attribute value)
				// PreCondition: theResult has been set by the U or P code above
				assertNotNull("Invalid test.txt: requested txt before getting page", theResult);
				int i = restOfLine.indexOf(' ');
				assertTrue("tag in line", i > 0);
				String tagName = restOfLine.substring(0, i);
				restOfLine = restOfLine.substring(i + 1);
				ReadTag rdr = new ReadTag(theResult.getResponseBodyAsStream());
				rdr.addWantedTag(tagName);
				List tags = rdr.readTags();
				boolean found = false;
				outer: for (Iterator iter = tags.iterator(); iter.hasNext();) {
					Element element = (Element) iter.next();
					String bodyText = element.getBodyText();
					if (bodyText != null && bodyText.indexOf(restOfLine) != -1) {
						found = true;
						break outer;
					}
					for (Iterator iterator = element.keySet().iterator(); iterator.hasNext();) {
						String key = (String) iterator.next();
						if (element.getAttribute(key).indexOf(restOfLine) != -1) {
							found = true;
							break outer;
						}
					}
				}
				assertTrue("did not find text: ", found);
				break;
			case 'L':	// page contains Link
				// PreCondition: theResult has been set by the U or P code above
				theLink = null;
				ReadTag r = new ReadTag(theResult.getResponseBodyAsStream());
				r.setWantedTags(new String[] { "a" });
				List l = r.readTags();
				for (Iterator iter = l.iterator(); iter.hasNext();) {
					Element tag = (Element) iter.next();
					
					// Check in the href first
					String h = tag.getAttribute("href");
					if (h == null) {
						// Presumably, a named anchor, like "<a name='foo'>". 
						// Nothing wrong with this, but we can't use it as a goto target, so just ignore.
						continue;
					}
					if (h.indexOf(restOfLine) != -1) {
						System.out.println("MATCH HREF");
						theLink = h;
						break;
					}
					
					// Check in the Name attribute, if any
					String n = tag.getAttribute("name");
					if (n != null && n.indexOf(restOfLine) != -1) {
						System.out.println("MATCH NAME");
						theLink = h;
						break;
					}
					
					// Check in the body text, if any.
					// Note: will fail if body text is nested in e.g., font tag!
					String t = tag.getBodyText();
					if (t != null && t.indexOf(restOfLine) != -1) {
						System.out.println("MATCH BODYTEXT");
						theLink = h;
						break;
					}
				}
				assertNotNull("link not found" ,  theLink);
				break;
			case 'G':
				// PreCondition: theLink has been set by the 'L' case above.
				assertNotNull("found link before gotoLink", theLink);
				if (!theLink.startsWith("/")) {
					String oldPath = theResult.getPath();
					theLink = oldPath.substring(0, oldPath.lastIndexOf("/")) + "/" + theLink;
				}
				System.out.println("Trying to go to " + theLink);
				// Even if we are inside a protected area, we don't need to login here.
				theResult = TestUtils.followLink(session, theLink);
				assertEquals("go to link response code", 200, theResult.getStatusCode());
				break;
			case 'N':	// start new session
				session = new HttpClient();
				theLink = null;
				break;
			case 'F':
			case 'R':
			case 'S':
				fail("code for " + c + " not written yet");
				break;
			}
			
		}
	}
}
