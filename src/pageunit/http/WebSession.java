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
		getter.releaseConnection();	
		
		responseText = new String(responseBody);

		response = new WebResponse(responseText, url.toString());
		return new HTMLParser().parse(responseText);
	}
	
	public HTMLPage getPage(final URL url) throws IOException, HTMLParseException {
		return getPage(url, true);
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
			action = "/" + action;	// XXX lame
		}
		PostMethod poster = new PostMethod(action);
		poster.setFollowRedirects(followRedirects);
		System.out.println("Initial POST request: " + action);

		if (button != null) {
			// XXX need to do something for this case...
		}
		// propagate the inputs().getValues()...
		List<HTMLInput> inputs = form.getInputs();
		final int numInputs = inputs.size();
		NameValuePair[] data = new NameValuePair[numInputs];
		for (int i = 0; i < numInputs; i++) {
			HTMLInput input = inputs.get(i);
			data[i] = new NameValuePair(input.getName(), input.getValue());
		}
        poster.setRequestBody(data);
		
		int status = client.executeMethod(poster);
		if (TestUtils.isErrorCode(status) && throwExceptionOnFailingStatusCode) {
			throw new IOException("Status code: " + status);
		}
		
		byte[] responseBody = poster.getResponseBody();
		System.out.println("Read body length was " + responseBody.length);
		poster.releaseConnection();	
		responseText = new String(responseBody);

		response = new WebResponse(responseText, action);
		
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
