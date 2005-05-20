package pageunit;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import org.apache.xerces.xni.XNIException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

/**
 * Run the classes listed in tests.txt. This is set up as a JUnit "test case" not as a JUnit Test Runner,
 * until we find a way to do the latter, so (a) it all appears as one ginormous test, and 
 * (b) it also has a Main method so you can run it standalone.
 * <br>
 * TODO: Maybe make this class be a JUnit "TestRunner" so each test is reported separately?
 * TODO: Add "Back button" functionality!
 * @version $Id$
 */
public class TestRunner extends TestCase {

	private static final String TESTS_FILE = "tests.txt";
	
	private static final int HTTP_STATUS_OK = 200;
	
	private int nTests;
	private int nSucceeded;
	private int nFailures;
	
	public void testAllTests() throws Exception {
		run(TESTS_FILE);
	}
	
	public static void main(String[] args) {
		TestRunner t = new TestRunner();
		try {
			if (args.length == 0) {
				t.run(TESTS_FILE);
			} else {
				for (int i = 0; i < args.length; i++) {
					t.run(args[i]);
				}
			}
			System.out.println("SUCCESS");
		} catch (Exception e) {
			System.out.println("FAILED: caught Exception " + e);
		}
	}	

	private WebClient session = new WebClient();
	private WebResponse theResult = null;
	private HtmlPage thePage = null;
	private HtmlAnchor theLink = null;
	private HtmlForm theForm = null;
	private boolean debug;
	private List<TestFilter> filters = new ArrayList<TestFilter>();
	
	/** Run ALL the tests in the named test file.
	 * @param fileName the test script file name.
	 * @throws Exception
	 */
	public void run(String fileName) throws Exception {			

		String login = TestUtils.getProperty("admin_login");
		assertNotNull("login", login);
		String pass = TestUtils.getProperty("admin_passwd");
		assertNotNull("pass", pass);
		String host = TestUtils.getProperty("host");
		assertNotNull("hostname", host);
		int port = TestUtils.getIntProperty("port");
		
		System.out.println("*****************************************************************");
		System.out.println(getClass().getName());
		System.out.println("Test run with http://" + host + ":" + port);
		System.out.println("Run at " + new Date());
		System.out.println("*****************************************************************");

		LineNumberReader is = new LineNumberReader(new FileReader(fileName));
		String line;
		while ((line = is.readLine()) != null) {
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
			boolean done = false;
			
			// Handle declarative (non-test) requests here
			switch(c) {
			case 'X':	// XTENTION or PLUG-IN
				String className = restOfLine;
				if (className == null || className.length() == 0) {
					throw new IllegalArgumentException("Plug-In Command must have class name");
				} else {
					Object o = Class.forName(className).newInstance();
					if (!(o instanceof TestFilter)) {
						throw new IllegalArgumentException("class " + className + " does not implement TestFilter");
					}
					filters.add((TestFilter)o);
				}				
				done = true;
				break;
			
			case 'Y':	// REMOVE XTENTION or PLUG-IN
				String clazzName = restOfLine;
				for (Object o : filters) {
					if (clazzName.equals(o.getClass().getName())) {
						filters.remove(o);
					}
				}
				break;
				
			case 'D':	// debug on/off
				char firstChar = restOfLine.charAt(0);
				if (firstChar == 't' || firstChar == '1') {
					setDebug(true);
				} else if (firstChar == 'f' || firstChar == '0')	{
					setDebug(false);
				} else {
					System.err.println("Warning: invalid Debug setting in " + line);
				}
				done = true; 
				break;
				
			case 'N':	// start new session
				session = new WebClient();
				session.setThrowExceptionOnFailingStatusCode(false);
				theLink = null;
				done = true;
				break;	
				
			case 'C':	// Credentials
				String[] cred = getTwoArgs("credentials", restOfLine, ' ');
				login = cred[0];
				pass = cred[1];
				done = true;
				break;
				
			case 'Q':
				System.out.println("*****************************************************************");
				System.out.println("*   Test Run Terminated by 'Q' command, others may be skipped   *");
				System.out.println("*****************************************************************");
				report();
				done = true;
				return;
			}
			
			if (done) {
				continue;
			}
			
			// Handle actual tests here
			try {
				this.testStarted(line);
				switch (c) {
				
				case 'U':	// get Unprotected page
					resetForPage();
					page = restOfLine;
					assertValidRURL(page);
					
					thePage = TestUtils.getSimplePage(session, host, port, page);
					theResult = thePage.getWebResponse();
					filterPage(thePage, theResult);
					assertEquals("unprotected page load", HTTP_STATUS_OK, theResult.getStatusCode());
					break;
					
				case 'P':	// get protected page
					resetForPage();
					page = restOfLine;
					assertValidRURL(page);
					
					thePage = TestUtils.getProtectedPage(session, host, port, page, login, pass);
					theResult = thePage.getWebResponse();
					filterPage(thePage, theResult);
					assertEquals("protected page status", HTTP_STATUS_OK, theResult.getStatusCode());
					assertEquals("protected page redirect", page, theResult.getUrl().getPath());
					break;
					
				case 'M':	// page contains text
					// PreCondition: theResult has been set by the U or P code above
					theLink = null;
					assertNotNull("Invalid test.txt: requested txt before getting page", thePage);
					theResult = thePage.getWebResponse();
					String contentAsString = theResult.getContentAsString();
					assertTrue("page contains text <" + restOfLine + ">", 
							TestUtils.checkResultForPattern(contentAsString, restOfLine));
					break;
					
				case 'T':	// page contains tag with text (in bodytext or attribute value)
					// PreCondition: theResult has been set by the U or P code above
					assertNotNull("Invalid test.txt: requested txt before getting page", theResult);
					
					String[] ttmp = getTwoArgs("tag", restOfLine, ' ');
					String tagType = ttmp[0];
					String tagText = ttmp[1];
					
					// special case for "title" tag; assume only one <title> tag per HTML page
					if ("title".equals(tagType)) {
						String titleText = thePage.getTitleText();
						System.out.println("TITLE = " + titleText);
						assertTrue("T title " + tagText, titleText.indexOf(tagText) != -1);
						break; // out of case 'T'
					}
					
					// look for tag;
					boolean found = false;
					
					for (Iterator<HtmlElement> iter = thePage.getChildIterator(); iter.hasNext();) {
						HtmlElement element = iter.next();
						String bodyText = element.getNodeValue();
						if (bodyText != null && bodyText.indexOf(tagText) != -1) {
							found = true;
							break; // out of this for loop
						}
					}
					assertTrue("did not find tag type " + tagType + " witth text: " + tagText, found);
					break;
					
				case 'L':	// page contains Link
					// PreCondition: theResult has been set by the U or P code above
					String linkURL = restOfLine;
					theLink = null;
					Iterator<HtmlAnchor> iter = thePage.getAnchors().iterator();
					while (iter.hasNext()) {
						HtmlAnchor oneLink = iter.next();
						
						// Check in the Name attribute, if any
						String n = oneLink.getNameAttribute();
						if (n != null && n.indexOf(linkURL) != -1) {
							System.out.println("MATCH NAME");
							theLink = oneLink;
							break;
						}
						
						// Check the Href attribute too
						n = oneLink.getHrefAttribute();
						if (n != null && n.indexOf(linkURL) != -1) {
							System.out.println("MATCH NAME");
							theLink = oneLink;
							break;
						}
						
						// Check in the body text, if any.
						// Note: will fail if body text is nested in e.g., font tag!
						String t = oneLink.asText();

						if (t != null && t.indexOf(linkURL) != -1) {
							System.out.println("MATCH BODYTEXT");
							theLink = oneLink;
							break;
						}
					}
					assertNotNull("link not found" ,  theLink);
					assertValidRURL(theLink.toString());
					break;
					
				case 'G':	// Go to link
					// PreCondition: theLink has been set by the 'L' case above.
					assertNotNull("found link before gotoLink", theLink);
					thePage = (HtmlPage)theLink.click();
					
					assertEquals("go to link response code", HTTP_STATUS_OK, thePage.getWebResponse().getStatusCode());
					break;
					
					// FORMS
					
				case 'F':
					// Find Form By Name - don't use getFormByName as SOFIA puts junk at start of form name.
					String formName = restOfLine;
					List theForms = thePage.getAllForms();
					for (Iterator<HtmlForm> iterator = theForms.iterator(); iterator.hasNext();) {
						HtmlForm oneForm = iterator.next();
						if (oneForm.getNameAttribute().indexOf(formName) != -1) {
							theForm = oneForm;	// "You are the One"
						}
					}
					assertNotNull("Find form named " + formName, theForm);
					break;
					
				case 'R':	// set parameter to value
					// PRECONDITION: theForm has been set by a previous F command
					assertNotNull("find a form before setting Parameters", theForm);
					
					String[] rtmp = getTwoArgs("name and value", restOfLine, '=');				
					String attrName = rtmp[0];
					String attrValue = rtmp[1];
					if (attrValue.indexOf('$') != -1) {
						// this could be more elegant/efficient for a greater range of substitutions
						attrValue = attrValue.replace("${USER}", login).replace("${PASS}", pass);
					}
					
					if (debug) {
						System.err.println("Name=" + attrName + "; value=" + attrValue);
					}
					Iterator inputs = theForm.getChildIterator();
					while (inputs.hasNext()) {
						Object element = (Object) inputs.next();
						System.out.println("LIST CONTAINS " + element);
					}
					HtmlInput theButton = theForm.getInputByName(attrName);
					System.out.println("GETTING IT EXPLICITLY --> " + theButton);
					theButton.setValueAttribute(attrValue);
					
					break;
					
				case 'S':
					assertNotNull("Form found before submit", theForm);
					
					String submitValue = restOfLine;
					
					if (submitValue == null || "".equals(submitValue)) {
						thePage = (HtmlPage)theForm.submit();   // SEND THE LOGIN
					} else {
						final HtmlSubmitInput button = (HtmlSubmitInput)theForm.getInputByName(submitValue);
						thePage = (HtmlPage)button.click();
					}
					
					// Should take us to a new page; HtmlUnit handles redirections automatically on regular
					// page gets, but for some reason not on form submits. I dunno, ask Brian.
					WebResponse formResponse = thePage.getWebResponse();
					int statusCode = formResponse.getStatusCode();
					
					if (TestUtils.isRedirectCode(statusCode)) {
						String newLocation = formResponse.getResponseHeaderValue("location");
						System.out.println(newLocation);
						assertNotNull("form submit->redirection: location header", newLocation);
						thePage = TestUtils.getSimplePage(session, new URL(newLocation));
						theResult = thePage.getWebResponse();
						assertEquals("form with redirect: page load", HTTP_STATUS_OK, theResult.getStatusCode());
					}				
					
					break;				
					
				default:
					fail("Unknown request: " + line);
				}
				this.testPassed(line);

			} catch (final NullPointerException e) {
				// Should not happen: indicates logic or coding error in the framework or a plugin
				e.printStackTrace();
			} catch (final XNIException e) {
				// Older Xerces XNIException has own getException(), not J2SE standard 
				this.testFailed(line);
				final Throwable exception = e.getException();
				System.err.println("XERCES FAILURE: " + line + e.getMessage() + "--" + exception);
				exception.printStackTrace();

			} catch (final Throwable e) {
				final Throwable exception = e.getCause();
				this.testFailed(line);
				System.err.println("FAILURE: " + fileName + ":" + is.getLineNumber() + " (" + e + ")");
			}
		}
		report();
	}
	
	private void filterPage(HtmlPage thePage, WebResponse theResult) throws Exception {
		for (TestFilter filter : filters) {
			filter.filterPage(thePage, theResult);
		}
	}

	private void assertValidRURL(String page) throws URISyntaxException {
		// XXX FAILURE: tests.txt:38 (java.net.URISyntaxException: Illegal character 
		//   in path at index 10: HtmlAnchor[<a href="PersonDetail.jsp?person_key=58" ...
		//URI url;
		//url = new URI(page);
		//if (url.isAbsolute()) {
		//	throw new IllegalArgumentException("URL may not be absolute: " + url);
		//}
	}

	/**
	 * @param line
	 */
	private void testStarted(String line) {
		++nTests;
	}

	/**
	 * @param line
	 */
	private void testFailed(String line) {
		++nFailures;
	}

	/**
	 * @param line
	 */
	private void testPassed(String line) {
		++nSucceeded;
	}


	/**
	 * Reset common fields for pages, and do some common error checking.
	 */
	private void resetForPage() {
		theLink = null;
		
		if (session == null) {
			System.err.println("Warning: no Session before Get Unprotected Page");
			session = new WebClient();
		}
	}
	
	private void report() {
		System.out.println("RUNS " + nTests + "; FAILURES " + nFailures);
		assertTrue(nFailures + " script test failures", nFailures == 0);
	}
	
	/**
	 * Split the rest of the line into a tag and the rest of the line, based on delim
	 * @param wordDescription a short description of what you are looking for, e.g., "tag" or "name" or ...
	 * @param lineAfterCommand The "restOfLine" variable, that is, the input line minus the one-letter command. 
	 * @param delim A character such as ' ' or '=' to split the line on.
	 * @return String[2] containing the tag and the rest of the line
	 */
	private String[] getTwoArgs(String wordDescription, String lineAfterCommand, char delim) {
		int i = lineAfterCommand.indexOf(delim);
		assertTrue(wordDescription + " in line", i > 0);
		String verb = lineAfterCommand.substring(0, i);
		String args = lineAfterCommand.substring(i + 1);
		return new String[] { verb, args };
	}
	/**
	 * Returns true if debug is enabled.
	 */
	public boolean isDebug() {
		return debug;
	}
	
	/**
	 * Enable debugging both here and in TestUtils.
	 * @param debug The debug setting.
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
		TestUtils.setDebug(debug);
	}
}
