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
			String restOfLine = line.substring(2);
			String page;

			char firstChar = restOfLine.charAt(0);
			switch(c) {
			case 'D':	// debug on/off
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
			case 'T':	// page contains text
				theLink = null;
				if (theResult == null) {
					throw new IOException("Invalid test.txt: requested txt before getting page");
				}
				assertTrue("page contains text", 
						TestUtils.checkResultForPattern(theResult.getResponseBodyAsString(), restOfLine));
				break;
			case 'L':	// page contains Link
				ReadTag r = new ReadTag(theResult.getResponseBodyAsStream());
				r.setWantedTags(new String[] { "a" });
				List l = r.readTags();
				for (Iterator iter = l.iterator(); iter.hasNext();) {
					Element tag = (Element) iter.next();
					String h = tag.getAttribute("href");
					if (h == null) {
						// Presumably, a named anchor, like "<a name='foo'>". 
						// Nothing wrong with this, but we can't use it as a goto target, so just ignore.
						continue;
					}
					if (h.indexOf(restOfLine) != -1) {
						theLink = h;
						break;
					}
					String n = tag.getAttribute("name");
					if (n.indexOf(restOfLine) != -1) {
						theLink = h;
						break;
					}
				}
				fail("link " + restOfLine + " not found");
				break;
			case 'G':
				assertNotNull("found link before gotoLink", theLink);
				theResult = TestUtils.getProtectedPage(session, host, port, theLink, login, pass);
				break;
			case 'S':	// start new session
				session = new HttpClient();
				theLink = null;
				break;
			}
			
		}
	}
}
