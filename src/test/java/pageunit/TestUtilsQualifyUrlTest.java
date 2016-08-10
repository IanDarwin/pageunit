package pageunit;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestUtilsQualifyUrlTest {

	private static final String DEFAULT_PROTOCOL = "https";

	private static final int DEFAULT_PORT = 443;

	private static final String DEFAULT_HOST = "localhost";

	final static Object[][] TESTDATA = {
			{"http://foo.com/x.jsp", "http://foo.com/x.jsp"},
			{"/barcode_list.jsp", DEFAULT_PROTOCOL + "://" + DEFAULT_HOST + ":" + DEFAULT_PORT + "/barcode_list.jsp"},
			{"barcode_list.jsp", DEFAULT_PROTOCOL + "://" + DEFAULT_HOST + ":" + DEFAULT_PORT + "/barcode_list.jsp"}
		};
	
	String stringPart;
	URL expected;
	
	public TestUtilsQualifyUrlTest(String stringPart, String expected) throws Exception {
		this.stringPart = stringPart;
		this.expected = new URL(expected);
	}
	
	@Parameters
	public static List<Object[]> getAsList() {
		return Arrays.asList(TESTDATA);
	}

	@Test
	public void testQualifyUrl() throws MalformedURLException {
		final URL actual = 
					Utilities.qualifyURL(DEFAULT_PROTOCOL, DEFAULT_HOST, DEFAULT_PORT, stringPart);
		assertEquals(expected, actual);
	}

}
