package regress.webtest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Run the classes listed in tests.txt
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
	
	/** Run ALL the tests in the given "test.txt" or similar file.
	 * @throws Exception
	 */
	public void testListedTests() throws Exception {
		
		Iterator testsIterator = tests.iterator();
		WebClient session = new WebClient();
		WebResponse theResult = null;
		HtmlPage thePage = null;
		HtmlAnchor theLink = null;
		String login = TestUtils.getProperty("admin_login");
		assertNotNull("login", login);
		String pass = TestUtils.getProperty("admin_passwd");
		assertNotNull("pass", pass);
		String host = TestUtils.getProperty("host");
		assertNotNull("hostname", host);
		int port = TestUtils.getIntProperty("port");
		
		while (testsIterator.hasNext()) {
			String line = (String) testsIterator.next();
			if (line.length() == 0) {
				System.out.println();
				continue;
			}
			if (line.charAt(0) == '#') {
				System.out.println(line);
				continue;
			}
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
				if (session == null) {
					System.err.println("Warning: no Session before Get Unprotected Page");
					session = new WebClient();
				}
				thePage = TestUtils.getSimplePage(session, host, port, page);
				theResult = thePage.getWebResponse();
				assertEquals("unprotected page load", 200, theResult.getStatusCode());
				break;
			case 'P':	// get protected page
				theLink = null;
				page = restOfLine;
				if (session == null) {
					System.err.println("Warning: no Session before Get Protected Page");
					session = new WebClient();
				}
				thePage = TestUtils.getProtectedPage(session, host, port, page, login, pass);
				theResult = thePage.getWebResponse();
				assertEquals("protected page code", 200, theResult.getStatusCode());
				// assertEquals("protected page redirect", page, theResult.getUrl().getPath());
				break;
			case 'M':	// page contains text
				// PreCondition: theResult has been set by the U or P code above
				theLink = null;
				assertNotNull("Invalid test.txt: requested txt before getting page", thePage);
				theResult = thePage.getWebResponse();
				assertTrue("page contains text", 
						TestUtils.checkResultForPattern(theResult.getContentAsString(), restOfLine));
				break;
			case 'T':	// page contains tag with text (in bodytext or attribute value)
				// PreCondition: theResult has been set by the U or P code above
				assertNotNull("Invalid test.txt: requested txt before getting page", theResult);
				int i = restOfLine.indexOf(' ');
				assertTrue("tag in line", i > 0);
				String tagName = restOfLine.substring(0, i);
				restOfLine = restOfLine.substring(i + 1);
				
				// special case for "title" tag
				if ("title".equals(tagName)) {
					assertEquals("T title", restOfLine, thePage.getTitleText());
					break; // out of case 'T'
				}
				
				// look for "tagName";
				boolean found = false;
				
				for (Iterator iter = thePage.getChildIterator(); iter.hasNext();) {
					HtmlElement element = (HtmlElement) iter.next();
					String bodyText = element.getNodeValue();
					System.out.println("T LOOP: " + bodyText);
					if (bodyText != null && bodyText.indexOf(restOfLine) != -1) {
						found = true;
						break; // out of this for loop
					}
				}
				assertTrue("did not find text: ", found);
				break;
			case 'L':	// page contains Link
				// PreCondition: theResult has been set by the U or P code above
				theLink = thePage.getAnchorByHref(restOfLine);
				if (theLink != null) {
					System.out.println("MATCH HREF by Href");
					break;
				}

				Iterator iter = thePage.getAnchors().iterator();
				while (iter.hasNext()) {
					HtmlAnchor tag = (HtmlAnchor) iter.next();
					
					// Check in the Name attribute, if any
					String n = tag.getNameAttribute();
					if (n != null && n.indexOf(restOfLine) != -1) {
						System.out.println("MATCH NAME");
						theLink = tag;
						break;
					}
					
					// Check in the body text, if any.
					// Note: will fail if body text is nested in e.g., font tag!
//					String t = tag.get
//					if (t != null && t.indexOf(restOfLine) != -1) {
//						System.out.println("MATCH BODYTEXT");
//						theLink = h;
//						break;
//					}
				}
				assertNotNull("link not found" ,  theLink);
				break;
			case 'G':
				// PreCondition: theLink has been set by the 'L' case above.
				assertNotNull("found link before gotoLink", theLink);
//				if (!theLink.getHrefAttribute(). startsWith("/")) {
//					String oldPath = theResult.getUrl().getPath();
//					theLink = oldPath.substring(0, oldPath.lastIndexOf("/")) + "/" + theLink;
//				}
				System.out.println("Trying to go to " + theLink);
				// Even if we are inside a protected area, we don't need to login here.
//				theResult = TestUtils.followLink(session, theLink);
//				assertEquals("go to link response code", 200, theResult.getStatusCode());
				break;
			case 'N':	// start new session
				session = new WebClient();
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
