package regress.html;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;

import junit.framework.TestCase;
import pageunit.html.HTMLComponent;
import pageunit.html.HTMLComponentFactory;
import pageunit.html.HTMLInputImpl;
import pageunit.html.HTMLScriptImpl;


public class HTMLComponentFactoryTest extends TestCase {

	Object[][] data = {
			{HTML.Tag.INPUT, HTMLInputImpl.class},
			{HTML.Tag.SCRIPT, HTMLScriptImpl.class},
			
	};
	
	/*
	 * Test method for 'pageunit.html.HTMLComponentFactory.create(Tag, MutableAttributeSet)'
	 */
	public void testCreateAll() {
		for (Object[] d : data) {
			HTMLComponent c = HTMLComponentFactory.create((HTML.Tag)d[0], null);
			assertSame(c.getClass(), d[1]);
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

}
