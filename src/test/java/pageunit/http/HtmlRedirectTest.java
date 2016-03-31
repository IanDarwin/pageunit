package pageunit.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.darwinsys.util.VariableMap;

import pageunit.html.HtmlRedirect;

@RunWith(Parameterized.class)
public class HtmlRedirectTest {

	private VariableMap variables = new VariableMap(); // empty
	
	String refresh;
	int expectSeconds;
	String expectUrl;
	
	final static Object[][] TESTDATA = { 
			{"42;URL='/voo/gar.pig'", 42, "/voo/gar.pig"},
			{"0; url=/",0, "/"},
			{"0; URL=\"/xxx\"",0, "/xxx"},
			{"12345;Url='/abc.html", 12345, "/abc.html"},	// test if typo, missing end quote, still passes, lax is good.
			{"300", 300, null},
	};
	
    @Parameters
    public static List<Object[]> getAsList() {
        return Arrays.asList(TESTDATA);
    }
	
	public HtmlRedirectTest(String refresh, int seconds, String urlPart) {
		this.refresh = refresh;
		this.expectSeconds = seconds;
		this.expectUrl = urlPart;
	}
	
	@Test
	public void testMetaRefreshRegex() throws Exception {
		HtmlRedirect red = HtmlRedirect.parse(refresh, variables);
		assertEquals(expectSeconds, red.seconds);
		if (expectUrl == null) {
			assertNull(red.url);
		} else {
			assertEquals(expectUrl, red.url.getPath());
		}
	}
}
