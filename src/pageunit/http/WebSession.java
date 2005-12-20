package pageunit.http;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

import pageunit.html.HTMLForm;
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

		GetMethod initialGet = new GetMethod(url.getPath());
		initialGet.setFollowRedirects(false);
		System.out.println("Initial request: " + initialGet);

		int status = client.executeMethod(initialGet);
		if (status >= 400 && throwExceptionOnFailingStatusCode) {
			throw new IOException("Status code: " + status);
		}

		request = new WebRequest();
		
		byte[] responseBody = initialGet.getResponseBody();
		System.out.println("Read body length was " + responseBody.length);
		initialGet.releaseConnection();	

		response = new WebResponse();
		
		responseText = new String(responseBody);
		return new HTMLParser().parse(responseText);
	}
	
	public HTMLPage submitForm(HTMLForm form) {
		// TODO 
		return null;
	}

	public WebRequest getWebRequest() {
		return request;
	}

	public WebResponse getWebResponse() {
		return response;
	}
}
