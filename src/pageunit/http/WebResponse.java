package pageunit.http;

import javax.servlet.http.Cookie;

/**
 * Like an HttpServletResponse but lighter.
 * @author ian
 */
public class WebResponse {
	String bodyContent;
	String url;

	
	public WebResponse(String bodyContent, String url) {
		this.bodyContent = bodyContent;
		this.url = url;
	}
	public String getHeaderValue(String key) {
		return null;
	}
	public int getStatus() {
		return 200;
	}
	public Cookie[] getCookies() {
		return null;
	}
	public Cookie getCookie(String name) {
		return null;
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
}
