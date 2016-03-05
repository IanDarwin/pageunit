package pageunit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;

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
 * Run the tests listed in the input file.
 * This file is the heart of PageUnit (but not, I hope, its "heart of darkness").
 * This is a JUnit 3.8 "test case" that makes a bunch of tests and runs them.
 * Should upgrade to JUnit 4 someday...
 */
public class ScriptTestCase extends TestCase {	
	private static Logger logger = Logger.getLogger(ScriptTestCase.class);

	private String fileName;
	private final List<TestHolder> lines = new ArrayList<TestHolder>();
	
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
	private File file;
	
	/** Construct a ScriptTestCase from the named test file.
	 * @param fileName the test script file name.
	 * @throws Exception If something blows
	 */
	public ScriptTestCase(String fileName) throws Exception {			
		this(new File(fileName), fileName);
	}

	/** Construct a ScriptTestCase from a java.io.File object.
	 * @param theFile A File object representing the file; MUST NOT be null unless there are guaranteed to be no
	 * inclusion operators ('&lt;') in the input.
	 * @param fileName The file to read
	 * @throws IOException If the file can't be read
	 */
	public ScriptTestCase(File theFile, String fileName) throws IOException {		
		this(theFile, new BufferedReader(new FileReader(theFile)), fileName);
	}
	
	/** Construct a ScriptTestCase, reading the file into a List.
	 * N.B. This constructor is needed for running from JUnit tests.
	 * @param theFile A File object representing the file; MUST NOT be null unless there are guaranteed to be no
	 * inclusion operators ('&lt;') in the input.
	 * @param reader An open Reader for the file.
	 * @param fileName The filename.
	 * @throws IOException If the file can't be read
	 */
	public ScriptTestCase(File theFile, Reader reader, String fileName) throws IOException {
		this.file = theFile;
		this.fileName = fileName;
		readTests(reader, fileName);
	}
	
	/* Set some default variables */
	private void setDefaultVariables(VariableMap variables) {
		variables.clear();
		if (variables.get(TestUtils.PROP_HOST) == null) {
			variables.put(TestUtils.PROP_HOST, "localhost");
		}
		if (variables.get(TestUtils.PROP_PORT) == null) {
			variables.put(TestUtils.PROP_PORT, "80");
		}
	}

	/** Read all the tests.
	 * @param r The reader to read from
	 * @param fileName The filename of 'r'
	 * @throws IOException If something fails
	 */
	private void readTests(Reader r, String fileName) throws IOException {
		logger.debug("readTests" + fileName);
		LineNumberReader is = new LineNumberReader(r);
		String line;
		while ((line = is.readLine()) != null) {
			line = line.trim();
			if (line.length() == 0) {
				continue;
			}
			char ch = line.charAt(0);
			String args = line.substring(1).trim();
			
			if (line.charAt(0) == '<') {
				readTests(new FileReader(new File(file.getParentFile(), args)), args);	// recurse
				continue;
			}
			if (!looksLikeCommand(line)) {
				continue;
			}
			lines.add(new TestHolder(ch, args, fileName, is.getLineNumber()));
		}
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
			
		return true;
	}
	
	// Allow # or ; as comment chars (not //, because of e.g., /index.html
	private boolean isComment(String line) {
		char cmdChar = line.charAt(0);
		return (cmdChar == '#' || cmdChar == ';') ;
	}

	@Override
	public int countTestCases() {
		int tests = lines.size();
		if (debug) {
			System.out.println("TestRunner.countTestCases() --> " + tests);
		}
		return tests;
	}

	/** Run all the tests in the current file; normally called by JUnit framework
	 */
	@Override
	public void run(TestResult results) {

		variables.setVar(TestUtils.PROP_USER, TestUtils.getProperty(TestUtils.PROP_USER));
		variables.setVar(TestUtils.PROP_PASS, TestUtils.getProperty(TestUtils.PROP_PASS));
		variables.setVar(TestUtils.PROP_HOST, TestUtils.getProperty(TestUtils.PROP_HOST));
		variables.setVar(TestUtils.PROP_PORT, TestUtils.getProperty(TestUtils.PROP_PORT));
		
		setDefaultVariables(variables);
		
		stars();
		System.out.println("PageUnit Running");
		System.out.println("Test run with default URL http://" + variables.getVar(TestUtils.PROP_HOST) + ":" + 
				variables.getVar(TestUtils.PROP_PORT));
		System.out.println("Input test file: " + fileName);
		System.out.println("Run at " + new Date());
		stars();

		session = new WebSession(variables);

		for (int lineNumber = 0, numLines = lines.size(); lineNumber < numLines; lineNumber++) {	// MAIN LOOP PER LINE

			TestHolder test = lines.get(lineNumber);
			String line = test.getArguments();
			System.out.printf("%d: %s%n", lineNumber, test);
			
			Command c = test.getCommand();
			String restOfLine =  variables.substVars(test.getArguments());
			
			results.startTest(test);			

			String page;		
			
			try {
				switch(c) {
				// First-half testing: Exceptions thrown here are fatal to the remainder of this FILE.
				// Handle declarative (non-test) requests here.
				// Each case ends with continue, to next iteration of main loop.

				case SOURCE:	// File Inclusion is now handled entirely in reading phase...
					throw new IllegalStateException("'<' got into list of tests, should have been removed by readTests");

				case SET:	// Set Variable
					String[] args = getTwoArgs("variable", restOfLine, ' ');
					variables.setVar(args[0], args[1]);
					continue;
					
				case E:	// echo
					System.out.println(restOfLine);
					continue;
					
				case X:	// XTENTION or PLUG-IN
					String className = restOfLine;
					if (className == null || className.length() == 0) {
						throw new IllegalArgumentException("Plug-In Command must have class name");
					}
					Object xo = null;
					try {
						xo = Class.forName(className).newInstance();
					} catch (Throwable e) {
						e.printStackTrace();
						throw new IllegalArgumentException("class " + className + " did not load: " + e);
					}
					if (!(xo instanceof TestFilter)) {
						throw new IllegalArgumentException("class " + className + " does not implement TestFilter");
					}
					System.out.println("Installing filter " + className);
					filterList.add((TestFilter)xo);			
					continue;
					
				case Y:	// REMOVE XTENTION or PLUG-IN
					String clazzName = restOfLine;
					if (clazzName == null || clazzName.length() == 0) {
						throw new IllegalArgumentException("Plug-In Command must have class name");
					}
					for (int i = 0; i < filterList.size(); i++) {
						TestFilter yo = filterList.get(i);
						if (yo.getClass().getName().equals(clazzName)) {
							System.out.println("Removing filter " + clazzName);
							filterList.remove(yo);
						}
					}
					continue;
					
				case D:	// debug on/off
					setDebug(getBoolean(restOfLine));
					continue; 
					
				case B:	// set Base URL
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
					
				case H:	// hard-code hostname
					variables.setVar("HOST", restOfLine.trim());
					continue;
					
				case O:	// hard-code pOrt number
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
					
				case A:	// As user [pw]
					String[] atmp = getOneOrTwoArgs("name [pass]", restOfLine, ' ');				
					variables.setVar("USER", atmp[0]);
					if (atmp[1] != null) {
						variables.setVar("PASS", atmp[1]);
					}
					continue;
					
				case C:	// Configuration
					System.err.println("Config management not written yet, abandoning this file");
					break;
					
				case N:	// start new session
					newSession();
					continue;
			
			// Second-half testing: exceptions thrown here are converted to failures, and only fail one test.
			// Handle most "actual" tests here; each case ends with break.

				case P:	// get Unprotected page
					resetForPage();
					page = restOfLine;
					assertValidRURL(page);
					
					if (debug) {
						System.err.println("G " + new URL("http", variables.getVar("HOST"),
								variables.getIntVar("PORT"), page));
					}
					thePage = session.getPage(
							variables.getVar("HOST"), variables.getIntVar("PORT"), page);
					theResult = session.getWebResponse();
					filterPage(thePage, theResult);
					assertEquals("unprotected page load", HttpStatus.SC_OK, theResult.getStatus());
					System.out.println("Got page " + page);
					break;
					
				case J:	// get Java EE protected page
					newSession();
					resetForPage();
					page = restOfLine;
					assertValidRURL(page);
					
					logger.debug("J " + new URL("http", variables.getVar("HOST"),
								variables.getIntVar("PORT"), page));
					
					assertNotNull("username", variables.getVar(TestUtils.PROP_USER));
					assertNotNull("password", variables.getVar(TestUtils.PROP_PASS));
					thePage = session.getPage(variables.getVar(TestUtils.PROP_HOST),
							variables.getIntVar(TestUtils.PROP_PORT), page, 
							variables.getVar("USER"), variables.getVar("PASS"));
					theResult = session.getWebResponse();
					filterPage(thePage, theResult);
					assertEquals("protected page status", HttpStatus.SC_OK, theResult.getStatus());
					// Can't use assertEquals here, as page may be "/admin/foo" but
					// the URL will be http://host/admin/foo". Just check endswith.
					assertTrue("protected page redirect", theResult.getUrl().endsWith(page));
					System.out.println("Got page " + page);
					break;
					
				case M:	// page contains text
					// PreCondition: theResult has been set by the U or P code above
					theLink = null;
					assertNotNull("Invalid test: requested txt before getting page", thePage);
					theResult = session.getWebResponse();
					assertNotNull("M ignored because page is null", theResult);
					String pattern = restOfLine;
					String contentAsString = theResult.getContentAsString();
					System.out.println(contentAsString.length());
					Matcher mMatcher = Pattern.compile(pattern).matcher(contentAsString);
					boolean mFound = mMatcher.find();
					System.out.printf("mMatcher.find() => %b, groupCount() => %d%n", mFound, mMatcher.groupCount());
					assertTrue("page contains regex <" + pattern + ">", mFound);
					int i;
					for (i = 0; i <= mMatcher.groupCount(); i++) {
						final String group = mMatcher.group(i);
						// System.out.println("Set M" + i + " to: "+ group);
						variables.setVar("M" + i, group);
					}
					for ( ; i < mHighWater; i++) {	// remove any left from previous run with more groups.
						variables.remove("M" + i);
					}
					mHighWater = mMatcher.groupCount();
					break;
					
				case T:	// page contains tag with text (in bodytext or attribute value)
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
					
				case L:	// page contains Link
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
					
				case G:	// Go to link
					// PreCondition: theLink has been set by the 'L' case above.
					assertNotNull("found link before gotoLink", theLink);
					thePage = (HTMLPage)session.follow(theLink);
					
					assertEquals("go to link response code", HttpStatus.SC_OK, session.getWebResponse().getStatus());
					break;
					
				// FORMS
					
				case F: // Find Form By Name
					theForm = null;
					String formName = restOfLine;
					List<HTMLForm> theForms = thePage.getForms();
					for (Iterator<HTMLForm> iterator = theForms.iterator(); iterator.hasNext();) {
						HTMLForm oneForm = iterator.next();
						if (oneForm.getName().indexOf(formName) != -1) {
							theForm = oneForm;	// Found it
							break;
						}
					}
					assertNotNull("Find form named " + formName, theForm);
					break;
					
				case R:	// set HTML Form parameter to value
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
					
				case S:	// SUBMIT
					assertNotNull("Form found before submit", theForm);
					
					String submitValue = restOfLine;
					
					if (submitValue == null || "".equals(submitValue)) {
						thePage = (HTMLPage)session.submitForm(theForm);   // SEND THE LOGIN
					} else { 
						// Use only getInputByName() here! Too confusing otherwise.
						final HTMLInput button = (HTMLInput)theForm.getInputByName(submitValue);
						thePage = (HTMLPage)session.submitForm(theForm, false, button);
					}
					
					// That should have taken us to a new page.
					WebResponse formResponse = session.getWebResponse();
					int statusCode = formResponse.getStatus();
					
					if (TestUtils.isRedirectCode(statusCode)) {
						String newLocation = formResponse.getHeader("location");
						System.out.println(newLocation);
						assertNotNull("form submit->redirection: location header", newLocation);
						thePage = session.getPage(new URL(newLocation), true);
						theResult = session.getWebResponse();
						assertEquals("form with redirect: page load", HttpStatus.SC_OK, theResult.getStatus());
					}				
					
					break;	
					
				case V:	// Verify (Link Checker) - may take a long time!
					System.out.printf("LinkChecker: %s%n", restOfLine);
					LinkChecker.checkStartingAt(restOfLine);
					break;
					
				default:
					fail("Unknown request: " + line);
					break;
				}
				
			} catch (final AssertionFailedError e) {
				fixupStackTrace(e, test);
				results.addFailure(test, e);
				printThrowable("FAILURE", e, test);
			} catch (final Throwable e) {
				fixupStackTrace(e, test);
				results.addError(test, e);
				printThrowable("ERROR", e, test);
			} finally {
				results.endTest(test);
			}
		}
		
		stars();
		System.out.printf("** END OF FILE %s **%n", fileName);
		stars();

		return;
	}

	/**
	 * 
	 */
	private void newSession() {
		session = new WebSession(variables);
		session.setThrowExceptionOnFailingStatusCode(false);
		theLink = null;
	}

	/** print the throwable along with the filename and line number
	 * @param e
	 * @param lineNumber
	 */
	private void printThrowable(final String type, final Throwable e, TestHolder pt) {
		System.err.print(type + ": " + pt.getFileName() + ";" + pt.getLineNumber() + " (" + e);
		final Throwable cause = e.getCause();
		if (cause != null) {
			System.err.print(":" + cause);
		}
		System.err.println(")");
		e.printStackTrace(System.err);
	}

	/** Modify the stack trace by adding a fake element that contains the filename and line number of the actual test.
	 * @param e
	 * @param test
	 */
	private void fixupStackTrace(Throwable e, Test test) {
		StackTraceElement[] oldStack = e.getStackTrace();
		StackTraceElement newTop = new StackTraceElement(getClass().getName(), "Test Runner", 
				((TestHolder)test).getFileName(),
				((TestHolder)test).getLineNumber());
		int oldSize = oldStack.length;
		StackTraceElement[] newStack = new StackTraceElement[oldSize + 1];	
		System.arraycopy(oldStack, 0, newStack, 1, oldSize);
		newStack[0] = newTop;
		e.setStackTrace(newStack);
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
			session = new WebSession(variables);
		}
	}
	
	// Various "parsing" methods - consolidated here, all made
	// public for ease of JUnit...
	
	/**
	 * Get a Boolean from an input line.
	 * @param input The input line
	 * @return The boolean value of the input
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
	 * Is Debug?
	 * @return true if debug is enabled.
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
