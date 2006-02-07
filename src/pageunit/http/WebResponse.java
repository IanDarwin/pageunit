package pageunit.http;

import javax.servlet.http.Cookie;

/**
 * Like an HttpServletResponse but lighter.
 * @author ian
 */
public class WebResponse {
	private String bodyContent;
	private String url;
	private int status;

	public WebResponse(String bodyContent, String url, int status) {
		this.bodyContent = bodyContent;
		this.url = url;
		this.status = status;
	}
	public String getHeaderValue(String key) {
		return null;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
