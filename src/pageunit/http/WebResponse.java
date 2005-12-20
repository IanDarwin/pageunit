package pageunit.http;

import javax.servlet.http.Cookie;

/**
 * Like an HttpServletResponse but lighter.
 * @author ian
 */
public class WebResponse {
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
		return null;
	}
	public String getUrl() {
		// TODO Auto-generated method stub
		return null;
	}
}
