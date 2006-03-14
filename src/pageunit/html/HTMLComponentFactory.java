package pageunit.html;

import java.util.Enumeration;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;

/** Encapsulate all knowledge of how to create any HTMLComponent.
 */
public class HTMLComponentFactory {

	private static boolean debug = false;

	/** Factory pattern: Create a new tag. 
	 * DO NOT CHANGE without also changing classForTagType() accordingly!
	 * @param tag
	 * @param attrs The Set of attributes (note that the names are coerced to lower case for us, so look em up in lower case!).
	 * @return The construct HTMLComponent.
	 */
	public static HTMLComponent create(Tag tag, MutableAttributeSet attrs) {
		if (debug ) {
			System.err.println(String.format("create(%s)", tag));
			dumpAttrs(attrs);
		}
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
			String action = getAttribute(HTML.Attribute.ACTION, attrs);
			String method = getAttribute(HTML.Attribute.METHOD, attrs);
						
			HTMLFormImpl form = new HTMLFormImpl(name, action, method);
			processOnSubmit(form, attrs);
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
			processOnChange(input, attrs);
			processOnClick(input, attrs);
			// input.setEnabled(true);
			return input;
		}
		if (tag == HTML.Tag.SELECT) {
			HTMLSelect input = new HTMLSelectImpl(name);
			processOnChange(input, attrs);
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
		if (tag == HTML.Tag.META) {
			String equiv = getAttribute(HTML.Attribute.HTTPEQUIV, attrs);
			String content = getAttribute(HTML.Attribute.CONTENT, attrs);
			return new HTMLMetaImpl(name, equiv, content);
		}
		throw new IllegalStateException(String.format("HTMLComponentFactory(%s): requested build of unknown tag", tag));
	}

	private static void processOnChange(OnChangeSettable c, MutableAttributeSet attrs) {
		String script = getAttribute("onchange", attrs);
		if (script != null) {
			c.setOnChange(script);
		}		
	}
	private static void processOnClick(OnClickSettable c, MutableAttributeSet attrs) {
		String script = getAttribute("onclick", attrs);
		if (script != null) {
			c.setOnClick(script);
		}		
	}
	private static void processOnSubmit(OnSubmitSettable c, MutableAttributeSet attrs) {
		String script = getAttribute("onsubmit", attrs);
		if (script != null) {
			c.setOnSubmit(script);
		}		
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
		if (tag == HTML.Tag.META) {
			return HTMLMetaImpl.class;
		}
		return HTMLComponentBase.class;
	}
	
	private static String getAttribute(Object attr_name, MutableAttributeSet attrs) {
		if (attrs == null)
			return null;
		return (String)attrs.getAttribute(attr_name);
	}

	private static void dumpAttrs(MutableAttributeSet attrs) {
		Enumeration e = attrs.getAttributeNames();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			System.err.print(key);
			System.err.print(" --> ");
			System.err.println(attrs.getAttribute(key));
		}
	}
}
