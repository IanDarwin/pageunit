package pageunit.http;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public interface WebResponse extends HttpServletResponse {
	public String getHeader(String key);
	public int getStatus();
	public Cookie[] getCookies();
	public Cookie getCookie(String name);
}
