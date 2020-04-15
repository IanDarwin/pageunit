package pageunit.html;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;

import junit.framework.TestCase;
import pageunit.html.HTMLComponent;
import pageunit.html.HTMLComponentFactory;
import pageunit.html.HTMLInputImpl;
import pageunit.html.HTMLParser;
import pageunit.html.HTMLScriptImpl;


public class HTMLComponentFactoryTest extends TestCase {

	final Object[][] data = {
			{HTML.Tag.INPUT, HTMLInputImpl.class},
			{HTML.Tag.SCRIPT, HTMLScriptImpl.class},
	};
	
	/*
	 * Test method for 'pageunit.html.HTMLComponentFactory.create(Tag, MutableAttributeSet)'
	 */
	public void testCreateFew() {
		for (Object[] d : data) {
			final HTMLComponent c = HTMLComponentFactory.create((HTML.Tag)d[0], null);
			assertSame(c.getClass(), d[1]);
		}
	}
	
	public void testForBadTag() {
		try {
			HTMLComponentFactory.create(HTML.Tag.BR, null);
			fail("Did not throw exception creating BR");
		} catch (IllegalStateException e) {
			System.out.println("Caught expected exception");
		}
	}
	
	public void testForBadClass() {
		final Tag tag = HTML.Tag.BR;
		final Class<?> cl = HTMLComponentFactory.classForTagType(tag);
		System.out.printf("HTML.Tag %s created class %s%n", tag, cl);
	}
	
	/** Confirm that at least for each tag in WantedTags lists, 
	 * create() and classForTag() give same type.
	 */
	public void testCreateWanted() {
		for (HTML.Tag t : HTMLParser.getWantedSimpleTags()) {
			HTMLComponent comp = HTMLComponentFactory.create(t, null);
			Class<?> cl = HTMLComponentFactory.classForTagType(t);
			assertSame(comp.getClass(), cl);
		}
		for (HTML.Tag t : HTMLParser.getWantedComplexTags()) {
			HTMLComponent comp = HTMLComponentFactory.create(t, null);
			Class<?> cl = HTMLComponentFactory.classForTagType(t);
			assertSame(comp.getClass(), cl);
		}
	}
	
	/*
	 * Test method for 'pageunit.html.HTMLComponentFactory.create(Tag, MutableAttributeSet)'
	 */
	public void testCreateScript() {
		HTML.Tag tag = HTML.Tag.SCRIPT;
		MutableAttributeSet attrs = null;
		HTMLComponent myTag = HTMLComponentFactory.create(tag, attrs);
		assertNotNull(myTag);
		assertSame(myTag.getClass(), HTMLScriptImpl.class);
		myTag.setBody("if a < b print(c);");
	}

	public void testCreateMeta() {
		HTML.Tag tag = HTML.Tag.META;
		MutableAttributeSet attrs = null;
		HTMLComponent myTag = HTMLComponentFactory.create(tag, attrs);
		assertSame(myTag.getClass(), HTMLMetaImpl.class);
	}
}
