package pageunit.http;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public interface WebRequest extends HttpServletRequest {
	public Cookie getCookie(String name);
	public void setHeader(String name, String value);
}
