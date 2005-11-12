package pageunit.html;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;

/** Encapsulate all knowledge of how to create any HTMLComponent.
 * 
 * @author ian
 */
public class HTMLComponentFactory {

	public static HTMLComponent create(Tag tag, MutableAttributeSet attrs) {
		String name = (String)attrs.getAttribute(HTML.Attribute.NAME);
		// If this were a Java 5 enum we could use switch.
		if (tag == HTML.Tag.A) {
			String url = (String)attrs.getAttribute(HTML.Attribute.HREF);
			HTMLAnchor comp = new HTMLAnchorImpl(name, url);
			return comp;
		}
		return null;
	}
}
