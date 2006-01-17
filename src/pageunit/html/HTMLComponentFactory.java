package pageunit.html;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

/** Encapsulate all knowledge of how to create any HTMLComponent.
 * @author ian
 */
public class HTMLComponentFactory {

	public static HTMLComponent create(Tag tag, MutableAttributeSet attrs) {
		String name = getAttribute(HTML.Attribute.NAME, attrs);
		// If this were a Java 5 enum we could use switch.
		if (tag == HTML.Tag.HTML) {
			return new HTMLHTMLImpl("top");
		}
		if (tag == HTML.Tag.A) {
			String url = getAttribute(HTML.Attribute.HREF, attrs);
			return new HTMLAnchorImpl(name, url);
		}
		if (tag == HTML.Tag.FORM) {
			String action = getAttribute(HTML.Attribute.ACTION, attrs);
			String method = getAttribute(HTML.Attribute.METHOD, attrs);
			return new HTMLFormImpl(name, action, method);
		}
		if (tag == HTML.Tag.IMG) {
			String src = getAttribute(HTML.Attribute.SRC, attrs);
			return new HTMLIMGImpl(name, src);
		}
		if (tag == HTML.Tag.INPUT) {
			String type = getAttribute(HTML.Attribute.TYPE, attrs);
			String value = getAttribute(HTML.Attribute.VALUE, attrs);
			HTMLInput input = new HTMLInputImpl(name, type);
			input.setValue(value);
			return input;
		}
		if (tag == HTML.Tag.TITLE) {
			return new HTMLTitleImpl(null);
		}
		System.err.printf("HTMLComponentFactory(%s): unknown", tag);
		return null;
	}

	private static String getAttribute(Attribute attr_name, MutableAttributeSet attrs) {
		if (attrs == null)
			return null;
		return (String)attrs.getAttribute(attr_name);
	}

}
