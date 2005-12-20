package pageunit.http;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

import pageunit.html.HTMLForm;
import pageunit.html.HTMLPage;

/** represents an HTTP session
 * 
 * @version $Id$
 */
public class WebSession {
	
	private HttpClient client;
	private boolean throwExceptionOnFailingStatusCode;
	
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
	 * @throws HttpException 
	 */
	public HTMLPage getPage(URL url) throws IOException {
		
		client.getHostConfiguration().setHost(
				url.getHost(), url.getPort(), url.getProtocol());

		GetMethod initialGet = new GetMethod(url.getPath());
		initialGet.setFollowRedirects(false);
		System.out.println("Initial request: " + initialGet);

		int status = client.executeMethod(initialGet);
		if (status > 399 && throwExceptionOnFailingStatusCode) {
			throw new IOException("Status code: " + status);
		}
		// XXX
		return null;
	}
	
	public HTMLPage postForm(HTMLForm form, Map<String, String> params) {
		// TODO 
		return null;
	}
}
