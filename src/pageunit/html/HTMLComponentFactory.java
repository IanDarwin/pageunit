package pageunit.html;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;

/** Encapsulate all knowledge of how to create any HTMLComponent.
 */
public class HTMLComponentFactory {

	/** Factory pattern: Create a new tag. 
	 * DO NOT CHANGE without also changing classForTagType() accordingly!
	 * @param tag
	 * @param attrs
	 * @return The construct HTMLComponent.
	 */
	public static HTMLComponent create(Tag tag, MutableAttributeSet attrs) {
		String name = getAttribute(HTML.Attribute.NAME, attrs);
		// If HTML.Tag were a Java 5 enum we could use switch.
		if (tag == HTML.Tag.HTML) {
			return new HTMLHTMLImpl("top");
		}
		if (tag == HTML.Tag.A) {
			String url = getAttribute(HTML.Attribute.HREF, attrs);
			return new HTMLAnchorImpl(name, url);
		}
		if (tag == HTML.Tag.FORM) {
			System.out.printf("FORM, attribtes types is %s%n", attrs.getClass());
			String action = getAttribute(HTML.Attribute.ACTION, attrs);
			String method = getAttribute(HTML.Attribute.METHOD, attrs);
			String onSubmit = getAttribute("onsubmit", attrs);			
			HTMLFormImpl form = new HTMLFormImpl(name, action, method);
			form.setOnSubmit(onSubmit);
			return form;
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
			// String onChange = getAttribute(HTML.Attribute.)
			// String onClick = getAttribute(HTML.Attribute.)
			// input.setEnabled(true);
			return input;
		}
		if (tag == HTML.Tag.SELECT) {
			HTMLSelect input = new HTMLSelectImpl(name);
			// String onChange = getAttribute(HTML.Attribute.)
			return input;
		}
		if (tag == HTML.Tag.OPTION) {
			HTMLOption input = new HTMLOptionImpl(name);
			return input;
		}
		if (tag == HTML.Tag.TITLE) {
			return new HTMLTitleImpl(null);
		}
		if (tag == HTML.Tag.SCRIPT) {
			return new HTMLScriptImpl(name);
		}
		System.err.printf("HTMLComponentFactory(%s): unknown", tag);
		return null;
	}

	/** Return the Class type in my hierarchy that corresponds to the HTML.Tag type in Swing's HTML;
	 * used in HTMLParser to know when to pop a Container.
	 * @param tag
	 * @return
	 */
	public static Class<?> classForTagType(HTML.Tag tag) {
		if (tag == HTML.Tag.HTML) {
			return HTMLHTMLImpl.class;
		}
		if (tag == HTML.Tag.A) {
			return HTMLAnchorImpl.class;
		}
		if (tag == HTML.Tag.FORM) {
			return HTMLFormImpl.class;
		}
		if (tag == HTML.Tag.IMG) {
			return HTMLIMGImpl.class;
		}
		if (tag == HTML.Tag.INPUT) {
			return HTMLInputImpl.class;
		}
		if (tag == HTML.Tag.SELECT) {
			return HTMLSelectImpl.class;
		}
		if (tag == HTML.Tag.OPTION) {
			return HTMLOptionImpl.class;
		}
		if (tag == HTML.Tag.TITLE) {
			return HTMLTitleImpl.class;
		}
		if (tag == HTML.Tag.SCRIPT) {
			return HTMLScriptImpl.class;
		}
		return HTMLComponentBase.class;
	}
	
	private static String getAttribute(Object attr_name, MutableAttributeSet attrs) {
		if (attrs == null)
			return null;
		return (String)attrs.getAttribute(attr_name);
	}

}
