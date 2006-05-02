package pageunit.http;

import java.util.regex.Matcher;

import junit.framework.TestCase;

public class WebSessionTest extends TestCase {

	public void testMetaRefreshRegex() throws Exception {
		String refreshTests[] = { 
			"42;URL='/voo/gar.pig'",
			"0; url=/",
			"0; URL=\"/xxx\"",
			"12345;Url='/abc.html",	// test if typo, missing end quote, still passes, lax is good.
		};
		for (String refresh : refreshTests) {
			System.out.println("Trying " + refresh);
			Matcher matcher = WebSession.META_REFRESH_CONTENT_REGEX_PATTERN.matcher(refresh);
			assertTrue(matcher.matches());
			assertEquals(1, matcher.groupCount());
			assertTrue(refresh.endsWith(matcher.group(1)));
		}
	}
}
