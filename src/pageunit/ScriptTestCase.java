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
	static String host = "localhost";	// XX get from properties
	static int port = 8080;

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
		HttpMethod result;
		String login = TestUtils.getProperty("admin_login");
		String pass = TestUtils.getProperty("admin_passwd");
		
		while (testsIterator.hasNext()) {
			String line = (String) testsIterator.next();
			System.out.println("TEST: " + line);
			StringTokenizer st = new StringTokenizer(line);
			if (st.countTokens() < 2) {
				throw new IOException("invalid line " + line);
			}
			String cmd = st.nextToken();
			if (cmd.length() != 1) {
				throw new IOException("invalid line " + line);
			}
			char c = cmd.charAt(0);
			String page;

			switch(c) {
			case 'U':	// get Unprotected page
				page = st.nextToken();
				session = new HttpClient(); // XXX ??
				result = TestUtils.getSimplePage(session, host, port, page);
				break;
			case 'P':	// get protected page
				page = st.nextToken();
				session = new HttpClient(); // XXX ??
				result = TestUtils.getProtectedPage(session, host, port, page, login, pass);
				break;
			case 'S':	// start new session
				session = new HttpClient();
			}
			
		}
	}
}
