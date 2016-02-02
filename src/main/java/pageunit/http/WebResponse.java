package pageunit.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.Header;

/**
 * Similar to an HttpServletResponse but lighter.
 * @author ian
 */
public class WebResponse {
	private String bodyContent;
	private String url;
	private int status;
	private Map<String,String> headerMap = new HashMap<String,String>();
	
	public WebResponse(String bodyContent, String url, int status) {
		this.bodyContent = bodyContent;
		this.url = url;
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	public String getContentAsString() {
		return bodyContent;
	}
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	/** 
	 * Save the given headers in a Map for simpler access 
	 * (don't export Commons HTTPClient Header[] type to client!)
	 * Names are converted to lowercase, 
	 * @param headers The Http Headers
	 */
	public void setHeaders(Header[] headers) {
		for (Header h : headers) {
			headerMap.put(h.getName().toLowerCase(), h.getValue());
		}
	}
	
	public Map<String,String> getHeaders() {
		return headerMap;
	}
	
	/** Get the given response header.
	 * @param headerName The (case-insensitive) name to look for.
	 * @return The Value for the given Name, or null if not found.
	 */
	public String getHeader(String headerName) {
		if (headerName == null) {
			throw new IllegalArgumentException("headerName may not be null");
		}
		return headerMap.get(headerName.toLowerCase());
	}
}
