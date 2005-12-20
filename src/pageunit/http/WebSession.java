package pageunit.http;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

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
	private WebRequest request;
	private WebResponse response;
	
	public WebSession() {
		super();
		client = new HttpClient();
	}

	public void setThrowExceptionOnFailingStatusCode(boolean b) {
		throwExceptionOnFailingStatusCode = b;
	}

	/** load the given page, parse the HTML response
	 * @param url The page to get
	 * @return the parsed HTML page
	 * @throws IOException 
	 * @throws HTMLParseException 
	 * @throws HttpException 
	 */
	public HTMLPage getPage(URL url) throws IOException, HTMLParseException {
		
		client.getHostConfiguration().setHost(
				url.getHost(), url.getPort(), url.getProtocol());

		GetMethod getter = new GetMethod(url.getPath());
		getter.setFollowRedirects(false);
		System.out.println("Initial request: " + getter);

		int status = client.executeMethod(getter);
		if (status >= 400 && throwExceptionOnFailingStatusCode) {
			throw new IOException("Status code: " + status);
		}

		request = new WebRequest();
		
		byte[] responseBody = getter.getResponseBody();
		System.out.println("Read body length was " + responseBody.length);
		getter.releaseConnection();	

		response = new WebResponse();
		
		responseText = new String(responseBody);
		return new HTMLParser().parse(responseText);
	}
	
	/** XXX implement as
	 * A thin wrapper around getPage().
	 * @param theLink
	 * @return
	 */
	public HTMLPage follow(HTMLAnchor theLink) {
		throw new RuntimeException("follow(anchor) not written");
	}
	
	/** Post an HTML Form
	 * @param form
	 * @return
	 * @throws HTMLParseException
	 * @throws IOException
	 */
	public HTMLPage submitForm(HTMLForm form) throws HTMLParseException, IOException {
		String action = form.getAction();
		
		//client.getHostConfiguration().setHost(
		//		url.getHost(), url.getPort(), url.getProtocol());

		PostMethod poster = new PostMethod(action);
		poster.setFollowRedirects(false);
		System.out.println("Initial request: " + poster);

		// XXX propagate the inputs().getValues()...
		
		int status = client.executeMethod(poster);
		if (status >= 400 && throwExceptionOnFailingStatusCode) {
			throw new IOException("Status code: " + status);
		}

		request = new WebRequest();
		
		byte[] responseBody = poster.getResponseBody();
		System.out.println("Read body length was " + responseBody.length);
		poster.releaseConnection();	

		response = new WebResponse();
		
		responseText = new String(responseBody);
		return new HTMLParser().parse(responseText);
	}
	
	public HTMLPage submitForm(HTMLForm form, HTMLInput button) throws HTMLParseException, IOException {
		throw new RuntimeException("submitForm(form, input) nryet");
	}

	public WebRequest getWebRequest() {
		return request;
	}

	public WebResponse getWebResponse() {
		return response;
	}


}
