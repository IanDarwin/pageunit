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
		HttpMethod result = null;
		String login = TestUtils.getProperty("admin_login");
		String pass = TestUtils.getProperty("admin_passwd");
		String host = TestUtils.getProperty("host");
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

			switch(c) {
			case 'D':	// debug on/off
				if (restOfLine.charAt(0) == 't') {
					TestUtils.setDebug(true);
				} else if (restOfLine.charAt(0) == 'f')	{
					TestUtils.setDebug(false);
				}
			case 'U':	// get Unprotected page
				page = restOfLine;
				session = new HttpClient(); // XXX ??
				result = TestUtils.getSimplePage(session, host, port, page);
				assertEquals("unprotected page load", 200, result.getStatusCode());
				break;
			case 'P':	// get protected page
				page = restOfLine;
				session = new HttpClient(); // XXX ??
				result = TestUtils.getProtectedPage(session, host, port, page, login, pass);
				assertEquals("protected page code", 200, result.getStatusCode());
				assertEquals("protected page redirect", result.getPath(), page);
				break;
			case 'T':	// page contains text
				if (result == null) {
					throw new IOException("Invalid test.txt: ask for txt before getting page");
				}
				assertTrue("page contains text", 
						TestUtils.checkResultForPattern(result.getResponseBodyAsString(), restOfLine));
				break;
			case 'S':	// start new session
				session = new HttpClient();
			}
			
		}
	}
}
