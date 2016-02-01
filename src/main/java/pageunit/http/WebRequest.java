package pageunit.http;

import java.util.HashMap;
import java.util.Map;

/** Similar to an HttpServletRequest, but lighter */
public class WebRequest {
	
	private Map<String, String> headerMap = new HashMap<String,String>();

	public void setHeader(String name, String value) {
		headerMap.put(name, value);
	}
	public String getHeader(String name) {
		return headerMap.get(name);
	}
}
