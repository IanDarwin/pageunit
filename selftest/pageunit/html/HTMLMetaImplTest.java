package pageunit.html;

import pageunit.html.HTMLMeta;
import pageunit.html.HTMLMetaImpl;
import junit.framework.TestCase;

public class HTMLMetaImplTest extends TestCase {

	/*
	 * Test method for 'pageunit.html.HTMLMetaImpl.HTMLMetaImpl(String, String, String)' and getters
	 */
	public void testHTMLMetaImpl() {
		HTMLMeta t = new HTMLMetaImpl("name", "equiv", "content");
		assertEquals("name", t.getName());
		assertEquals("equiv", t.getMetaEquiv());
		assertEquals("content", t.getMetaContent());
	}

}
