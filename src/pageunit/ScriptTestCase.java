package pageunit;

import java.io.IOException;
import java.io.LineNumberReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpStatus;

import pageunit.html.HTMLAnchor;
import pageunit.html.HTMLComponent;
import pageunit.html.HTMLForm;
import pageunit.html.HTMLInput;
import pageunit.html.HTMLPage;
import pageunit.http.WebResponse;
import pageunit.http.WebSession;
import pageunit.io.FileStack;
import pageunit.io.InputFile;

import com.darwinsys.util.VariableMap;

/**
 * Run the tests listed in the input files. Set up as a JUnit "test case" not as a JUnit Test Runner,
 * until we find a way to do the latter, so (a) it all appears as one ginormous test, and 
 * (b) it also has a Main method so you can run it standalone.
 * <br>
 * To Do items are listed in the file under docs/.
 * @version $Id$
 */
public class TestRunner extends TestCase {	
	
	private int nTests;
	private int nSucceeded;
	private int nFailures;
	
	public ResultStat testAllTests() throws Exception {
		return run(PageUnit.TESTS_FILE);
	}

	private WebSession session;
	private WebResponse theResult = null;
	private HTMLPage thePage = null;
	private HTMLAnchor theLink = null;
	private HTMLForm theForm = null;
	private boolean debug;
	private List<TestFilter> filterList = new ArrayList<TestFilter>();
	VariableMap variables = new VariableMap();

	FileStack files = new FileStack();
	
	/** Run ALL the tests in the named test file.
	 * @param fileName the test script file name.
	 * @throws Exception
	 */
	public ResultStat run(final String thisFileName) throws Exception {			
	
		variables.clear();

		variables.setVar("USER", TestUtils.getProperty("login"));
		variables.setVar("PASS", TestUtils.getProperty("password"));
		variables.setVar("HOST", TestUtils.getProperty("host"));
		variables.setVar("PORT", TestUtils.getProperty("port"));
		
		files.reset();
		files.pushInputFile(thisFileName);
		
		System.out.println("*****************************************************************");
		System.out.println("PageUnit $Version$");
		System.out.println("Test run with default URL http://" + variables.getVar("HOST") + ":" + variables.getVar("PORT"));
		System.out.println("Input test file: " + files.getFileName());
		System.out.println("Run at " + new Date());
		System.out.println("*****************************************************************");

		session = new WebSession();

		InputFile inf = files.peekInputFile();

		do {		
			// This will happen once per file in the input stack plus once
			// per "<" file inclusion.
			LineNumberReader r = inf.getReader();
			
			String line;
			
			mainLoop:
			while ((line = r.readLine()) != null) {	// MAIN LOOP PER LINE
				if (line.length() == 0) {
					System.out.println();
					continue;
				}
				if (line.charAt(0) == '#') {
					System.out.println(line);
					continue;
				}
				System.out.println("CMD: " + line);
				
				StringTokenizer st = new StringTokenizer(line);
				if (st.countTokens() < 1) {
					throw new IOException("invalid line " + line);
				}
				String cmd = st.nextToken();
				if (cmd.length() != 1) {
					throw new IOException("invalid command in line " + line);
				}
				char c = cmd.charAt(0);
				String restOfLine = line.length() > 2 ? line.substring(2).trim() : "";
				if (restOfLine.length() < 1 || restOfLine.charAt(0) == '#') {
					continue;
				}
				System.out.println("--> " + restOfLine) ;
				String page;
				
				// Handle declarative (non-test) requests here.
				// Each case ends with continue, to next iteration of main loop.
				switch(c) {
				case '<':	// File Inclusion
					inf = files.pushInputFile(restOfLine);
					r = inf.getReader(); // will loop through input file
					continue mainLoop;
				case '=':	// Set Variable
					String[] args = getTwoArgs("variable", restOfLine, ' ');
					variables.setVar(args[0], args[1]);
					continue;
					
				case 'E':	// echo
					System.out.println(variables.substVars(restOfLine));
					continue;
					
				case 'X':	// XTENTION or PLUG-IN
					String className = restOfLine;
					if (className == null || className.length() == 0) {
						throw new IllegalArgumentException("Plug-In Command must have class name");
					} else {
						Object o = Class.forName(className).newInstance();
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
					URL u = new URL(restOfLine);
					variables.setVar("HOST", u.getHost());
					variables.setVar("PORT", Integer.toString(u.getPort()));
					continue;
					
				case 'H':	// hard-code hostname
					variables.setVar("HOST", restOfLine.trim());
					continue;
					
				case 'O':	// hard-code pOrt number
					variables.setVar("PORT", restOfLine.trim());
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
					
				case 'Q':
					System.out.println("*****************************************************************");
					System.out.println("*   Test Run Terminated by 'Q' command, others may be skipped   *");
					System.out.println("*****************************************************************");
					report();
					continue;				
				}
				
				// Handle actual tests here; each case ends with break.
				try {
					this.testStarted(line);
					switch (c) {
					
					case 'U':	// get Unprotected page
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
						break;
						
					case 'P':	// get protected page
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
						String contentAsString = theResult.getContentAsString();
						assertTrue("page contains text <" + restOfLine + ">", 
								TestUtils.checkResultForPattern(contentAsString, variables.substVars(restOfLine)));
						break;
						
					case 'T':	// page contains tag with text (in bodytext or attribute value)
						// PreCondition: theResult has been set by the U or P code above
						assertNotNull("Requested txt before getting page", theResult);
						
						String[] ttmp = getTwoArgs("tag", restOfLine, ' ');
						String tagType = ttmp[0];
						String tagText = variables.substVars(ttmp[1]);
						
						// special case for "title" tag; assume only one <title> tag per HTML page
						if ("title".equals(tagType)) {
							String titleText = thePage.getTitleText();
							System.out.println("TITLE FOUND = " + titleText);
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
						// Find Form By Name - don't use getFormByName as SOFIA puts junk at start of form name.
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

						attrValue = variables.substVars(attrValue);
						
						if (debug) {
							System.err.println("Name=" + attrName + "; value=" + attrValue);
						}
						
						HTMLInput theButton = theForm.getInputByName(attrName);
						assertNotNull("get Button", theButton);
						theButton.setValue(attrValue);
						
						break;
						
					case 'S':
						assertNotNull("Form found before submit", theForm);
						
						String submitValue = restOfLine;
						
						if (submitValue == null || "".equals(submitValue)) {
							thePage = (HTMLPage)session.submitForm(theForm);   // SEND THE LOGIN
						} else {
							final HTMLInput button = (HTMLInput)theForm.getInputByName(submitValue);
							thePage = (HTMLPage)session.submitForm(theForm, button);
						}
						
						// Should take us to a new page; HtmlUnit handles redirections automatically on regular
						// page gets, but for some reason not on form submits. I dunno, ask Brian.
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
						
					default:
						fail("Unknown request: " + line);
					}
					this.testPassed(line);
					
				} catch (final NullPointerException e) {
					System.err.println("Error in PageUnit: " + e);
					e.printStackTrace(System.err);
				} catch (final Throwable e) {
					final Throwable cause = e.getCause();
					this.testFailed(line);
					System.err.println(
							"FAILURE: " + files.getFileName() + ":" + r.getLineNumber() + 
							" (" + e + ':' + cause + ")");
				} // end of try
				System.out.println("1");
			} // end of while readLine loop
			System.out.println("EOF on " +  inf);
			// inf.close();
		} while ((inf = files.popInputFile()) != null); // end of "do while" loop.
		
		// More All Done
		files.reset();	// will print on stderr if any leftovers

		return new ResultStat(nTests, nSucceeded, nFailures);
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
			session = new WebSession();
		}
	}
	
	private void report() {
		System.out.println("RUNS " + nTests + "; FAILURES " + nFailures + " so far.");
		// assertTrue(nFailures + " script test failures", nFailures == 0);
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
