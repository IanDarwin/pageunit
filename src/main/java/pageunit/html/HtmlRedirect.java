package pageunit.html;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.darwinsys.util.VariableMap;

import pageunit.Utilities;

/** 
 * Encapsulate the Html <meta http-equiv="refresh"..."> style of HTML redirect.
 */
public class HtmlRedirect {
	
	public static final String META_REFRESH_CONTENT_REGEX_STRING = "(\\d+);?\\s*URL=['\"]?(.*?)['\"]?";
	public static final Pattern META_REFRESH_CONTENT_REGEX_PATTERN = 
		Pattern.compile(META_REFRESH_CONTENT_REGEX_STRING, Pattern.CASE_INSENSITIVE);

	public int seconds;
	public URL url;

	public static HtmlRedirect parse(String content, VariableMap variables) {
		HtmlRedirect ret = new HtmlRedirect();
		int semi = content.indexOf(";");
		if (semi == -1) {		// Just a number, no URL, e.g., MRTG does this
			ret.seconds = Integer.parseInt(content);
			return ret;
		}
		Matcher match = META_REFRESH_CONTENT_REGEX_PATTERN.matcher(content);
		if (match.matches()) {
			try {
				String secondsString = match.group(1);
				ret.seconds = Integer.parseInt(secondsString);
				String urlPattern = match.group(2);
				if (variables != null) {
					ret.url = Utilities.qualifyURL(variables, urlPattern);
				} else {
					ret.url = new URL(urlPattern);
				}
				return ret;
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException("Html Meta Refresh: bad URL: " + e);
			}
		} else {
			throw new IllegalArgumentException("can't parse META refresh pattern " + content);
		}
	}
}