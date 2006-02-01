package pageunit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

import org.apache.commons.httpclient.HttpStatus;

import pageunit.html.HTMLAnchor;
import pageunit.html.HTMLComponent;
import pageunit.html.HTMLForm;
import pageunit.html.HTMLInput;
import pageunit.html.HTMLPage;
import pageunit.http.WebResponse;
import pageunit.http.WebSession;
import pageunit.linkchecker.LinkChecker;

import com.darwinsys.util.VariableMap;

/**
 * Run the tests listed in the input files. Set up as a JUnit "test case" not as a JUnit Test Runner,
 * until we find a way to do the latter. Result: (a) it all appears as one ginormous test, and 
 * (b) it also has a Main method so you can run it standalone.
 * @version $Id$
 */
public class ScriptTestCase extends TestCase {	

	private String fileName;
	private final List<String> lines = new ArrayList<String>();
	
	private WebSession session;
	private WebResponse theResult = null;
	private HTMLPage thePage = null;
	private HTMLAnchor theLink = null;
	private HTMLForm theForm = null;
	private boolean debug;
	private List<TestFilter> filterList = new ArrayList<TestFilter>();
	private VariableMap variables = new VariableMap();

	public final static int PORT_MAX_IPV4 = Short.MAX_VALUE;
	/** Number of M variables created by last successful M command */
	private int mHighWater = 0;
	private int tests;
	
	/** Run ALL the tests in the named test file.
	 * @param fileName the test script file name.
	 * @throws Exception
	 */
	public ScriptTestCase(String fileName) throws Exception {			
		this(new File(fileName), fileName);
	}

	public ScriptTestCase(File theFile, String fileName) throws IOException {		
		this(new BufferedReader(new FileReader(theFile)), fileName);
	}
	
	public ScriptTestCase(Reader r, String fileName) throws IOException {
		this.fileName = fileName;
		BufferedReader is = new BufferedReader(r);
		String line;
		int tests = 0;
		while ((line = is.readLine()) != null) {
			if (!looksLikeCommand(line)) {
				continue;
			}
			lines.add(line);
			++tests;
		}
		this.tests = tests;
	}
		
	/** Check if the given line looks like a command, that is, has a non-comment char in col 1.
	 * @param line
	 * @return
	 */
	private boolean looksLikeCommand(String line) {
		if (line.length() == 0 )
			return false;
		if (isComment(line))
			return false;
		
		// XXX rip out redirections until they work again, to keep the count right!
		if (line.charAt(0) == '<')
			return false;
			
		return true;
	}
	
	// Allow # or ; as comment chars (not //, because of e.g., /index.html
	private boolean isComment(String line) {
		char cmdChar = line.charAt(0);
		return (cmdChar == '#' || cmdChar == ';') ;
	}
	
//		 XXX MOVE THIS ELSEWHERE AND GET WORKING AGAIN
//		if (theFile.isAbsolute()) {
//			curDir = theFile.getParentFile();
//		} else {

//			if (curDir != null) {
//				theFile = new File(curDir, fileName);
//			} else {
//				System.err.printf("File %s has no directory parent", fileName);
//			}

	@Override
	public int countTestCases() {
		System.out.println("TestRunner.countTestCases() --> " + tests);
		return tests;
	}

	/** Run all the tests in the current file.
	 */
	@Override
	public void run(TestResult results) {
		
		variables.clear();

		variables.setVar("USER", TestUtils.getProperty("login"));
		variables.setVar("PASS", TestUtils.getProperty("password"));
		variables.setVar("HOST", TestUtils.getProperty("host"));
		variables.setVar("PORT", TestUtils.getProperty("port"));
		
		stars();
		System.out.println("PageUnit $Version$");
		System.out.println("Test run with default URL http://" + variables.getVar("HOST") + ":" + variables.getVar("PORT"));
		System.out.println("Input test file: " + fileName);
		System.out.println("Run at " + new Date());
		stars();

		session = new WebSession();

		for (int lineNumber = 0, numLines = lines.size(); lineNumber < numLines; lineNumber++) {	// MAIN LOOP PER LINE

			String line = lines.get(lineNumber);
			System.out.printf("%d: %s%n", lineNumber, line);

			if (!looksLikeCommand(line)) {
				System.out.println(line);
				continue;
			}
			
			StringTokenizer st = new StringTokenizer(line);
			if (st.countTokens() < 1) {
				throw new IllegalArgumentException("invalid line " + line);
			}
			String cmd = st.nextToken();
			if (cmd.length() != 1) {
				throw new IllegalArgumentException("invalid command in line " + line);
			}
			char c = cmd.charAt(0);
			
			Test test = new PageTest(c, fileName, lineNumber);
			results.startTest(test);
			
			String restOfLine = line.length() > 2 ? line.substring(2).trim() : "";
			if (restOfLine.length() > 0 && isComment(restOfLine)) {
				System.out.println("SKIP");
				continue;
			}
			System.out.println("NOT SKIP");
			restOfLine = variables.substVars(restOfLine);
			String page;
			
			
			try {
				switch(c) {
				// First-half testing: Exceptions thrown here are fatal to the remainder of this FILE.
				// Handle declarative (non-test) requests here.
				// Each case ends with continue, to next iteration of main loop.

				case '<':	// File Inclusion
					// run(restOfLine);
					System.err.println("< MECHANISM IS BROKEN");
					continue;
				case '=':	// Set Variable
					String[] args = getTwoArgs("variable", restOfLine, ' ');
					variables.setVar(args[0], args[1]);
					continue;
					
				case 'E':	// echo
					System.out.println(restOfLine);
					continue;
					
				case 'X':	// XTENTION or PLUG-IN
					String className = restOfLine;
					if (className == null || className.length() == 0) {
						throw new IllegalArgumentException("Plug-In Command must have class name");
					} else {
						Object o = null;
						try {
							o = Class.forName(className).newInstance();
						} catch (Throwable e) {
							e.printStackTrace();
							throw new IllegalArgumentException("class " + className + " did not load: " + e);
						}
						if (!(o instanceof TestFilter)) {
							throw new IllegalArgumentException("class " + className + " does not implement TestFilter");
						}
						filterList.add((TestFilter)o);
					}				
					continue;
					
				case 'Y':	// REMOVE XTENTION or PLUG-IN
					String clazzName = restOfLine;
					for (Object o : filterList) {
						if (clazzName.equals(o.getClass().getName())) {
							filterList.remove(o);
						}
					}
					continue;
					
				case 'D':	// debug on/off
					setDebug(getBoolean(restOfLine));
					continue; 
					
				case 'B':	// set Base URL
					URL u = null;
					try {
						u = new URL(restOfLine);
						variables.setVar("HOST", u.getHost());
						variables.setVar("PORT", Integer.toString(u.getPort()));
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
						throw new IllegalArgumentException("URL " + restOfLine + " threw " + e1);
					}				
					continue;
					
				case 'H':	// hard-code hostname
					variables.setVar("HOST", restOfLine.trim());
					continue;
					
				case 'O':	// hard-code pOrt number
					String portNumStr = restOfLine.trim();
					try {
						int i = Integer.parseInt(portNumStr);
						if (i < 0 || i > PORT_MAX_IPV4)
							System.err.println("Value not in range 0.." + PORT_MAX_IPV4 + " " + portNumStr);
						variables.setVar("PORT", portNumStr);
					} catch (NumberFormatException e) {
						System.err.printf("FAIL: %s not a valid port number", portNumStr);
					}
					continue;
					
				case 'A':	// As user [pw]
					String[] atmp = getOneOrTwoArgs("name [pass]", restOfLine, ' ');				
					variables.setVar("USER", atmp[0]);
					if (atmp[1] != null) {
						variables.setVar("PASS", atmp[1]);
					}
					continue;
					
				case 'C':	// Configuration
					System.err.println("Config management not written yet, abandoning this file");
					break;
					
				case 'N':	// start new session
					session = new WebSession();
					session.setThrowExceptionOnFailingStatusCode(false);
					theLink = null;
					continue;
			
			// Second-half testing: exceptions thrown here are converted to failures, and only fail one test.
			// Handle most "actual" tests here; each case ends with break.

				case 'P':	// get Unprotected page
					resetForPage();
					page = restOfLine;
					assertValidRURL(page);
					
					if (debug) {
						System.err.println(new URL("http", variables.getVar("HOST"),
								variables.getIntVar("PORT"), page));
					}
					thePage = TestUtils.getSimplePage(session, 
							variables.getVar("HOST"), variables.getIntVar("PORT"), page);
					theResult = session.getWebResponse();
					filterPage(thePage, theResult);
					assertEquals("unprotected page load", HttpStatus.SC_OK, theResult.getStatus());
					System.out.println("Got page " + page);
					break;
					
				case 'J':	// get J2EE protected page
					resetForPage();
					page = restOfLine;
					assertValidRURL(page);
					
					thePage = TestUtils.getProtectedPage(session, variables.getVar("HOST"),
							variables.getIntVar("PORT"), page, 
							variables.getVar("USER"), variables.getVar("PASS"));
					theResult = session.getWebResponse();
					filterPage(thePage, theResult);
					assertEquals("protected page status", HttpStatus.SC_OK, theResult.getStatus());
					assertEquals("protected page redirect", page, theResult.getUrl());
					break;
					
				case 'M':	// page contains text
					// PreCondition: theResult has been set by the U or P code above
					theLink = null;
					assertNotNull("Invalid test: requested txt before getting page", thePage);
					theResult = session.getWebResponse();
					if (theResult == null) { 
						System.err.println("M ignored because page is null");
						break;
					}
					String pattern = restOfLine;
					String contentAsString = theResult.getContentAsString();
					System.out.println(contentAsString.length());
					Matcher mMatcher = Pattern.compile(pattern).matcher(contentAsString);
					boolean mFound = mMatcher.find();
					assertTrue("page contains regex <" + pattern + ">", mFound);
					System.out.printf("mMatcher.find() => %b, groupCount() => %d%n", mFound, mMatcher.groupCount());
					int i;
					for (i = 0; i <= mMatcher.groupCount(); i++) {
						final String group = mMatcher.group(i);
						// System.out.println("Set M" + i + " to: "+ group);
						variables.setVar("M" + i, group);
					}
					for ( ; i < mHighWater; i++) {
						variables.remove("M" + i);
					}
					mHighWater = mMatcher.groupCount();
					break;
					
				case 'T':	// page contains tag with text (in bodytext or attribute value)
					// PreCondition: theResult has been set by the U or P code above
					assertNotNull("Requested txt before getting page", theResult);
					
					String[] ttmp = getTwoArgs("tag", restOfLine, ' ');
					String tagType = ttmp[0];
					String tagText =  ttmp[1];
					
					// special case for "title" tag; assume only one <title> tag per HTML page
					if ("title".equals(tagType)) {
						String titleText = thePage.getTitleText();
						System.out.println("TITLE FOUND = " + titleText);
						assertNotNull("T title", titleText);
						assertTrue("T title " + tagText, titleText.indexOf(tagText) != -1);
						break; // out of case 'T'
					}
					
					// look for tag;
					boolean found = false;
					
					for (Iterator<HTMLComponent> iter = thePage.getChildren().iterator(); iter.hasNext();) {
						HTMLComponent element = iter.next();
						String bodyText = element.getBody();
						if (bodyText != null && bodyText.indexOf(tagText) != -1) {
							found = true;
							break; // out of this for loop
						}
					}
					assertTrue("did not find tag type " + tagType + " with text: " + tagText, found);
					break;
					
				case 'L':	// page contains Link
					// PreCondition: theResult has been set by the U or P code above
					String linkURL = restOfLine;
					theLink = null;
					for (HTMLAnchor oneLink : thePage.getAnchors()) {
						
						// Check in the Name attribute, if any
						String n = oneLink.getName();
						if (n != null && n.indexOf(linkURL) != -1) {
							System.out.println("MATCH NAME");
							theLink = oneLink;
							break;
						}
						
						// Check the Href attribute too
						n = oneLink.getURL();
						if (n != null && n.indexOf(linkURL) != -1) {
							System.out.println("MATCH NAME");
							theLink = oneLink;
							break;
						}
						
						// Check in the body text, if any.
						// Note: will fail if body text is nested in e.g., font tag!
						String t = oneLink.toString();
						
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
					thePage = (HTMLPage)session.follow(theLink);
					
					assertEquals("go to link response code", HttpStatus.SC_OK, session.getWebResponse().getStatus());
					break;
					
				// FORMS
					
				case 'F':
					// Find Form By Name
					String formName = restOfLine;
					List<HTMLForm> theForms = thePage.getForms();
					for (Iterator<HTMLForm> iterator = theForms.iterator(); iterator.hasNext();) {
						HTMLForm oneForm = iterator.next();
						if (oneForm.getName().indexOf(formName) != -1) {
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
					
					if (debug) {
						System.err.println("Name=" + attrName + "; value=" + attrValue);
					}
					
					HTMLInput theButton = theForm.getInputByName(attrName);
					assertNotNull("get Button", theButton);
					theButton.setValue(attrValue);
					
					break;
					
				case 'S':	// SUBMIT
					assertNotNull("Form found before submit", theForm);
					
					String submitValue = restOfLine;
					
					if (submitValue == null || "".equals(submitValue)) {
						thePage = (HTMLPage)session.submitForm(theForm);   // SEND THE LOGIN
					} else { 
						// Use only getInputByName() here! Too confusing otherwise.
						final HTMLInput button = (HTMLInput)theForm.getInputByName(submitValue);
						thePage = (HTMLPage)session.submitForm(theForm, button);
					}
					
					// That should have taken us to a new page.
					WebResponse formResponse = session.getWebResponse();
					int statusCode = formResponse.getStatus();
					
					if (TestUtils.isRedirectCode(statusCode)) {
						String newLocation = formResponse.getHeaderValue("location");
						System.out.println(newLocation);
						assertNotNull("form submit->redirection: location header", newLocation);
						thePage = TestUtils.getSimplePage(session, new URL(newLocation));
						theResult = session.getWebResponse();
						assertEquals("form with redirect: page load", HttpStatus.SC_OK, theResult.getStatus());
					}				
					
					break;	
					
				case 'V':	// Verify (Link Checker)
					System.out.printf("LinkChecker: %s%n", restOfLine);
					LinkChecker.checkStartingAt(restOfLine);
					break;
					
				default:
					fail("Unknown request: " + line);
					break;
				}
				
			} catch (final AssertionFailedError e) {
				System.err.println("FAILURE: " + fileName + ";" + lineNumber + e);
				results.addFailure(test, e);
			} catch (final Throwable e) {
				final Throwable cause = e.getCause();
				results.addError(test, e);
				System.err.print("ERROR: " + fileName + ":" + lineNumber + " (" + e);
				if (cause != null) {
					System.err.print(":" + cause);
				}
				System.err.println(")");
			} finally {
				results.endTest(test);
			}
		}
		
		stars();
		System.out.printf("** END OF FILE %s **%n", fileName);
		stars();

		return;
	}

	private void stars() {
		System.out.println("*****************************************************************");
	}

	private void filterPage(final HTMLPage thePage, final WebResponse theResult) throws Exception {
		for (TestFilter filter : filterList) {
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
	 * Reset common fields for pages, and do some common error checking.
	 */
	private void resetForPage() {
		theLink = null;
		
		if (session == null) {
			System.err.println("Warning: no Session before Get Unprotected Page");
			session = new WebSession();
		}
	}
	
	// Various "parsing" methods - consolidated here, all made
	// public for ease of JUnit...
	
	/**
	 * Get a Boolean from an input line.
	 * @param input
	 */
	public static boolean getBoolean(final String input) {
		if ("on".equals(input) || "t".equals(input) || "true".equals(input)) {
			return true;
		} else if ("off".equals(input) || "false".equals(input)) {
			return false;
		}
		throw new IllegalArgumentException("Warning: invalid Debug setting in " + input);	
	}
	
	/**
	 * Split the rest of the line into a tag and the rest of the line, based on delim
	 * @param wordDescription a short description of what you are looking for, e.g., "tag" or "name" or ...
	 * @param lineAfterCommand The "restOfLine" variable, that is, the input line minus the one-letter command. 
	 * @param delim A character such as ' ' or '=' to split the line on.
	 * @return String[2] containing the tag and the rest of the line
	 */
	public static String[] getTwoArgs(String wordDescription, String lineAfterCommand, char delim) {
		String[] res = getOneOrTwoArgs(wordDescription, lineAfterCommand, delim);
		assertTrue(wordDescription + " in line" + lineAfterCommand, res[1] != null && res[1].length() > 0);
		return res;
	}
	
	public static String[] getOneOrTwoArgs(String wordDescription, String lineAfterCommand, char delim) {
		int i = lineAfterCommand.indexOf(delim);
		if (i >= 0) {
			String verb = lineAfterCommand.substring(0, i);
			String args = lineAfterCommand.substring(i + 1);
			return new String[] { verb, args };
		} else {
			return new String[] { lineAfterCommand, null };
		}
		
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
