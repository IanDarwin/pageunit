package pageunit.http;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import pageunit.TestUtils;
import pageunit.html.HTMLAnchor;
import pageunit.html.HTMLForm;
import pageunit.html.HTMLInput;
import pageunit.html.HTMLPage;
import pageunit.html.HTMLParseException;
import pageunit.html.HTMLParser;

/** represents an HTTP session
 * 
 * @version $Id$
 */
public class WebSession {
	
	private HttpClient client;
	private boolean throwExceptionOnFailingStatusCode;
	private String responseText;
	private WebResponse response;
	private boolean debug = true;
	
	public WebSession() {
		super();
		client = new HttpClient();
	}

	
	/** If this is set to true, 400/500 errors will throw an exception;
	 * if false (the default), errors simply return and the user must 
	 * check with getStatus().
	 * @param b
	 */
	public void setThrowExceptionOnFailingStatusCode(final boolean b) {
		throwExceptionOnFailingStatusCode = b;
	}

	/** load the given page, parse the HTML response
	 * @param url The page to get
	 * @return the parsed HTML page
	 * @throws IOException 
	 * @throws HTMLParseException 
	 * @throws HttpException 
	 */
	public HTMLPage getPage(final URL url, final boolean followRedirects) throws IOException, HTMLParseException {
		
		client.getHostConfiguration().setHost(
				url.getHost(), url.getPort(), url.getProtocol());

		GetMethod getter = new GetMethod(url.getPath());
		
		getter.setFollowRedirects(followRedirects);
		
		System.out.printf("Initial GET request: %s (followRedirects %b)%n", url, followRedirects);

		int status = client.executeMethod(getter);
		if (status >= 400 && throwExceptionOnFailingStatusCode) {
			throw new IOException("Status code: " + status);
		}
		
		byte[] responseBody = getter.getResponseBody();
		System.out.println("Read body length was " + responseBody.length);
		System.out.println("Got to simple page: " + url);
		getter.releaseConnection();	
		
		responseText = new String(responseBody);

		response = new WebResponse(responseText, url.toString(), status);
		
		return new HTMLParser().parse(responseText);
	}
	
	/**
	 * Get an unprotected page given its URL
	 * @param webclient
	 * @param newLocation
	 * @return
	 * @throws HTMLParseException 
	 */
	public HTMLPage getPage(final URL url) throws IOException, HTMLParseException {
		return getPage(url, true);
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
	 * @throws IOException
	 * @throws HTMLParseException 
	 */
	public HTMLPage getPage(
			String targetHost, int targetPort, String targetPage)
			throws IOException, HTMLParseException {

		final URL url = TestUtils.qualifyURL(targetHost, targetPort, targetPage);
		
		return getPage(url);

	}

	/**
	 * Get an HTML page that is protected by J2EE Container-based Forms
	 * Authentication.
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
	 * @throws IOException
	 * @throws HTMLParseException 
	 */
	public HTMLPage getPage(final String targetHost, final int targetPort,
			final String targetPage, final String login,
			final String pass) throws IOException, HTMLParseException {
		
		final URL url = TestUtils.qualifyURL(targetHost, targetPort, targetPage);
		
		// request protected page, and let WebSession handle redirection here.
		final HTMLPage page1 = (HTMLPage) getPage(url, true);	// Ask for one page, really get login page
		
		WebResponse interaction = getWebResponse();
		int statusCode = interaction.getStatus();
        if (debug) {     	
			System.out.println("Protected Page get: " + page1.getTitleText() + ", status: " + statusCode);
        }

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
		if (debug) {
			System.out.println("Login return " + formResultsPage.getTitleText());
		}

		// Should be yet another redirect, back to original request page
		WebResponse res2 = getWebResponse();
		statusCode = res2.getStatus();
		System.out.printf("After submit login, statusCode = %d%n", statusCode);
		
		if (!TestUtils.isRedirectCode((statusCode))) {
			throw new IllegalStateException("expected redirect status but got " + statusCode);
		}
		
		return getPage(TestUtils.qualifyURL(targetHost, targetPort, "/admin/index.jsp"));
	}
	
	/**
	 * A thin wrapper around getPage(): GET the page linked
	 * to an anchor imbedded in a page.
	 * @param theLink
	 * @return
	 * @throws HTMLParseException 
	 * @throws IOException 
	 */
	public HTMLPage follow(final HTMLAnchor theLink) throws IOException, HTMLParseException {
		URL u = TestUtils.completeURL(theLink.getURL());
		return getPage(u);
	}
	
	/** Post an HTML Form
	 * @param form
	 * @return
	 * @throws HTMLParseException
	 * @throws IOException
	 */
	public HTMLPage submitForm(final HTMLForm form, final boolean followRedirects, final HTMLInput button) throws HTMLParseException, IOException {

		String action = form.getAction();
		
		if (!action.contains("/")) {
			action = "/" + action;	// Handle e.g., J2EE form "action='j_security_check'" from Tomcat
		}
		PostMethod handler = new PostMethod(action);
		handler.setFollowRedirects(followRedirects);
		System.out.println("Initial POST request: " + action);

		if (button != null) {
			System.err.println("Warning: ignoring button " + button);	// need to do something for this case...
		}
		// propagate the inputs().getValues()...
		List<HTMLInput> inputs = form.getInputs();
		final int numInputs = inputs.size();
		NameValuePair[] data = new NameValuePair[numInputs];
		for (int i = 0; i < numInputs; i++) {
			HTMLInput input = inputs.get(i);
			data[i] = new NameValuePair(input.getName(), input.getValue());
		}
        handler.setRequestBody(data);
		
		int status = client.executeMethod(handler);
		if (TestUtils.isErrorCode(status) && throwExceptionOnFailingStatusCode) {
			throw new IOException("Status code: " + status);
		}
		if (debug) {
			System.out.println("WebSession.submitForm(): status code after post was: " + status);
		}
		
		byte[] responseBody = handler.getResponseBody();
		System.out.println("Read body length was " + responseBody.length);
		handler.releaseConnection();	
		responseText = new String(responseBody);

		response = new WebResponse(responseText, action, status);
		
		
		return new HTMLParser().parse(responseText);
	}
	
	/** Submit a Form with defaults
	 * @param form
	 * @return
	 * @throws HTMLParseException
	 * @throws IOException
	 */
	public HTMLPage submitForm(final HTMLForm form) throws HTMLParseException, IOException {
		return submitForm(form, false, null);
	}

	public WebResponse getWebResponse() {
		return response;
	}


}
