package pageunit.html;

import junit.framework.TestCase;
import pageunit.html.HTMLAnchorImpl;
import pageunit.html.HTMLComponent;

public class HTMLComponentBaseTest extends TestCase {
	
	HTMLComponent target = new HTMLAnchorImpl("foo", "http://www.bar");
	
	public void testBody() {
		target.setBody("Hello");
		assertEquals("Hello", target.getBody());
		target.appendBody(" World");
		assertEquals("Hello World", target.getBody());
	}
}
