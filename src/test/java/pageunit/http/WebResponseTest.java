package pageunit.http;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import pageunit.Utilities;

import java.net.http.HttpHeaders;

public class WebResponseTest {
	
	private static final String HTML_PAGE_AS_STRING = "<html></html>";
	WebResponse response;
	
	@Before
	public void setUp() throws Exception {
		response = new WebResponse(HTML_PAGE_AS_STRING, "http://www.foofoo.com", 200);
	}

	/*
	 * Test method for 'pageunit.http.WebResponse.WebResponse(String, String, int)'
	 */
	@Test
	public void testWebResponse() {
		assertEquals("status OK", 200, response.getStatus());
		response.setStatus(302);
		assertEquals("get status", 302, response.getStatus());
		assertTrue("set redirect", Utilities.isRedirectCode(response.getStatus()));
		System.out.println("WebResponseTest.testWebResponse()");
	}

	/*
	 * Test method for 'pageunit.http.WebResponse.getContentAsString()'
	 */
	@Test
	public void testGetContentAsString() {
		assertSame(response.getContentAsString(), HTML_PAGE_AS_STRING);
	}

	/*
	 * Test method for 'pageunit.http.WebResponse.setHeaders(Header[])'
	 * * Test method for 'pageunit.http.WebResponse.getHeader(String)'
	 */
//	How to get a HttpHeaders for testing this?
//	public void testHeaders() {
//		final String location = "http://www.foofoo.leSnoo";
//		HttpHeaders headers = HttpHeaders();
//		Header loc = new Header("Location", location);
//		Header cook = new Header("set-Cookie", "//////.com-jjjj-29898109801810");
//		Header[] headers = { loc, cook };
//		response.setHeaders(headers);
//		assertEquals("get location header", location, response.getHeader("location"));
//		System.out.println("WebResponseTest.testHeaders()");
//	}
}
