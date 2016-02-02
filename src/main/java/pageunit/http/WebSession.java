package pageunit.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import com.darwinsys.util.VariableMap;

import pageunit.TestUtils;
import pageunit.html.HTMLAnchor;
import pageunit.html.HTMLComponent;
import pageunit.html.HTMLForm;
import pageunit.html.HTMLInput;
import pageunit.html.HTMLInputImpl;
import pageunit.html.HTMLInputType;
import pageunit.html.HTMLMeta;
import pageunit.html.HTMLPage;
import pageunit.html.HTMLParseException;
import pageunit.html.HTMLParser;

/** 
 * Represents an HTTP session
 */
public class WebSession {
	
	static final String META_REFRESH_CONTENT_REGEX_STRING = "\\d+;\\s*URL=['\"]?(.*?)['\"]?";
	static final Pattern META_REFRESH_CONTENT_REGEX_PATTERN = 
		Pattern.compile(META_REFRESH_CONTENT_REGEX_STRING, Pattern.CASE_INSENSITIVE);
	private HttpClient client;
	private boolean throwExceptionOnFailingStatusCode;
	private String responseText;
	private WebResponse response;
	private VariableMap variables;
	
	private static Logger logger = Logger.getLogger(WebSession.class);

	public WebSession() {
		this(new VariableMap());
	}
	
	public WebSession(VariableMap vars) {
		super();
		client = new HttpClient();
		this.variables = vars;
	}

	
	/** 
	 * If this is set to true, 400/500 errors will throw an exception;
	 * if false (the default), errors simply return and the user must 
	 * check with getStatus().
	 * @param b Whether to throw on 400/500 staus codes
	 */
	public void setThrowExceptionOnFailingStatusCode(final boolean b) {
		throwExceptionOnFailingStatusCode = b;
	}

	/**
	 * Load the given page, parse the HTML response
	 * @param url The page to get
	 * @param followRedirects True to follow redirects, false to return the 3XX page
	 * @return the parsed HTML page
	 * @throws IOException If the page can't be read
	 * @throws HTMLParseException If the page fails to parse
	 */
	public HTMLPage getPage(URL url, final boolean followRedirects) throws IOException, HTMLParseException {
		HTMLPage page;
		do {
			client.getHostConfiguration().setHost(
					url.getHost(), url.getPort(), url.getProtocol());
			
			GetMethod getter = new GetMethod(url.toString());		
			getter.setFollowRedirects(followRedirects);
			
			logger.info(String.format("Initial GET request: %s (followRedirects %b)%n", url, followRedirects));
			
			int status = client.executeMethod(getter);
			if (status >= 400 && throwExceptionOnFailingStatusCode) {
				throw new IOException("Status code: " + status);
			}
			
			byte[] responseBody = getter.getResponseBody();
			System.out.println("Read body length was " + responseBody.length);
			responseText = new String(responseBody);
			response = new WebResponse(responseText, url.toString(), status);
			
			System.out.println("Got to simple page: " + url);
			response.setHeaders(getter.getResponseHeaders());	// gets converted to Map<String,String>
			getter.releaseConnection();	
			
			responseText = new String(responseBody);			// must save in field.
			
			page = new HTMLParser().parse(responseText);
			
		} while ((url = isRedirectpage(page)) != null);
		return page;
	}
	
	private URL isRedirectpage(HTMLPage page) {
		// XXX Do we need to check HTTP status for !isRedirectURL?
		
		// check for META tag with Refresh
		for (HTMLComponent c : page.getChildren()) {
			if (c instanceof HTMLMeta) {
				HTMLMeta m = (HTMLMeta)c;
				logger.info(String.format("WebSession.isRedirectURLPage(%s) found META tag %s%n", page, m));
				if (!"refresh".equalsIgnoreCase(m.getMetaEquiv())) {
					return null;
				}
				String content = m.getMetaContent();
				Pattern patt = Pattern.compile(META_REFRESH_CONTENT_REGEX_STRING);
				Matcher mat = patt.matcher(content);
				if (mat.find()) {
					try {
						String urlPattern = mat.group(1);
						URL url = null;
						if (variables != null) {
							url = TestUtils.qualifyURL(variables, urlPattern);
						} else {
							url = new URL(urlPattern);
						}
						logger.info(String.format("isRedirectURLPage(%s) Returning URL %s%n", page, url));
						return url;
					} catch (MalformedURLException e) {
						logger.error("HTTP META REFRESH BOMBED: " + e);
						return null;
					}
				} else {
					System.err.printf("can't parse META refresh pattern '%s'%n", content);
					return null;
				}
			}
		}
		logger.info(String.format("isRedirectpage(%s) returning null.%n", page));
		return null;
	}


	/**
	 * Get an unprotected page
	 * 
	 * @param session
	 *            The HTTP Session
	 * @param targetHost
	 *            The name (or maybe IP as a String) for the host
	 * @param targetPort
	 *            The port number, 80 for default
	 * @param targetPage
	 *            The pathname part of the URL
	 * @return An HttpMethod object containing the response.
	 * @throws IOException If the reading fails
	 * @throws HTMLParseException If the page fails to parse
	 */
	public HTMLPage getPage(
			String targetHost, int targetPort, String targetPage)
			throws IOException, HTMLParseException {

		final URL url = TestUtils.qualifyURL(targetHost, targetPort, targetPage);
		
		return getPage(url, true);

	}

	/**
	 * Get an HTML page that is protected by J2EE Container-based Forms
	 * Authentication.
	 * 
	 * @param session The HTTP Session
	 * @param targetHost The name (or maybe IP as a String) for the host
	 * @param targetPort The port number, 80 for default
	 * @param targetPage The pathname part of the URL
	 * @return An HttpMethod object containing the response.
	 * @throws IOException If the reading fails
	 * @throws HTMLParseException If the page fails to parse
	 */
	public HTMLPage getPage(final String targetHost, final int targetPort,
			final String targetPage, final String login,
			final String pass) throws IOException, HTMLParseException {
		
		final URL url = TestUtils.qualifyURL(targetHost, targetPort, targetPage);
		
		// request protected page, and let WebSession handle redirection here.
		final HTMLPage page1 = (HTMLPage) getPage(url, true);	// Ask for one page, really get login page
		
		WebResponse interaction = getWebResponse();
		int statusCode = interaction.getStatus();
	
		logger.info(String.format("Protected Page get: " + page1.getTitleText() + ", status: " + statusCode));

        // Find J2EE login form using regex: must begin with j_security_check, may have jsessionid...
        HTMLForm form = page1.getFormByURL("^j_security_check");
		if (form == null) {
			throw new IllegalStateException("Not a valid J2EE page, can't find form with action of j_security_check");
		}

		HTMLInput userNameFormField = form.getInputByName("j_username");
		if (userNameFormField == null) {
			throw new IllegalStateException("Not a valid J2EE login form - no j_username field");
		}
		userNameFormField.setValue(login);
		
		HTMLInput userPassFormField = form.getInputByName("j_password");
		if (userPassFormField == null) {
			throw new IllegalStateException("Not a valid J2EE login form - no j_password field");
		}
		userPassFormField.setValue(pass);
		
		// SEND THE LOGIN; disable redirects, HttpClient can't redirect "entity enclosing request" e.g., POST, how helpful.
		HTMLPage formResultsPage = submitForm(form);   

		logger.info("Login return " + formResultsPage.getTitleText());


		// Should be yet another redirect, back to original request page
		WebResponse finalResponse = getWebResponse();
		statusCode = finalResponse.getStatus();
		logger.info(String.format("After submit login, statusCode = %d%n", statusCode));
		
		if (!TestUtils.isRedirectCode((statusCode))) {
			throw new IllegalStateException("expected redirect status but got " + statusCode);
		}
		
		String redirectLocation = finalResponse.getHeader("location");
		logger.info("WebSession.getPage(): redirect location = " + redirectLocation);
		
		// "To reach, at the end, the goal with which one started..."
		return getPage(TestUtils.qualifyURL(targetHost, targetPort, redirectLocation), true);
	}
	
	/**
	 * A thin wrapper around getPage(): GET the page linked
	 * to an anchor imbedded in a page.
	 * @param theLink The page to load
	 * @return The resulting page
	 * @throws HTMLParseException If the page fails to parse
	 * @throws IOException If the reading fails
	 */
	public HTMLPage follow(final HTMLAnchor theLink) throws IOException, HTMLParseException {
		URL u = TestUtils.completeURL(theLink.getURL());
		return getPage(u, true);
	}
	
	/** 
	 * Post an HTML Form
	 * @param form The form to submit
	 * @return The resulting page
	 * @throws HTMLParseException If the page fails to parse
	 * @throws IOExceptionIf the reading fails
	 */
	public HTMLPage submitForm(final HTMLForm form, final boolean followRedirects, final HTMLInput button) throws HTMLParseException, IOException {

		String action = form.getAction();
		
		if (!action.contains("/")) {
			action = "/" + action;	// Handle e.g., J2EE form "action='j_security_check'" from Tomcat
		}
		PostMethod handler = new PostMethod(action);
		handler.setFollowRedirects(followRedirects);
		System.out.println("Initial POST request: " + action);

		// propagate the inputs().getValues()...
		List<HTMLInput> inputs = form.getInputs();
		
		// If a button was named, remove non-specified submit buttons from the list of inputs
		if (button != null) {
			Iterator<HTMLInput> inputsIterator = inputs.iterator();
			while (inputsIterator.hasNext()) {
	
				HTMLInputImpl input = (HTMLInputImpl) inputsIterator.next();
		
				if (input.getInputType().equals(HTMLInputType.SUBMIT)
						&& !input.getName().equals(button.getName()))
					inputsIterator.remove();
			}
		}
		final int numInputs = inputs.size();
		NameValuePair[] data = new NameValuePair[numInputs];
		int i = 0;
		for (HTMLInput input : inputs) {
			data[i++] = new NameValuePair(input.getName(), input.getValue());
		}
        handler.setRequestBody(data);
		
		int status = client.executeMethod(handler);
		if (TestUtils.isErrorCode(status) && throwExceptionOnFailingStatusCode) {
			throw new IOException("Status code: " + status);
		}
		logger.info("WebSession.submitForm(): status code after post was: " + status);
		
		byte[] responseBody = handler.getResponseBody();
		System.out.println("Read body length was " + responseBody.length);
		responseText = new String(responseBody);
		handler.releaseConnection();

		response = new WebResponse(responseText, action, status);
		response.setHeaders(handler.getResponseHeaders());
		
		return new HTMLParser().parse(responseText);
	}
	
	/** 
	 * Submit a Form with defaults
	 * @param form The form to be submitted
	 * @return The resulting HTMLPage
	 * @throws HTMLParseException If the parse fails
	 * @throws IOException If the reading fails
	 */
	public HTMLPage submitForm(final HTMLForm form) throws HTMLParseException, IOException {
		return submitForm(form, false, null);
	}

	public WebResponse getWebResponse() {
		return response;
	}

	VariableMap getVariablesMap() {
		return variables;
	}
}
