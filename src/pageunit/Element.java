package regress.webtest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An XML element has a name (which in this version will include the ns prefix if any), 
 * and zero or more attributes.
 * @version $Id$
 */
public class Element {
	
	/** The type of element, e.g., in HTML, "a", "img", etc. */
	private final String type;	
	/** The attributes */
	private Map attributes = new HashMap();
	/** The body text, if any, or null */
	private String bodyText;
	
	/**
	 * @param type
	 */
	public Element(String type) {
		super();
		this.type = type;
	}
	/**
	 * @return Returns the attributes.
	 */
	public String getAttribute(String key) {
		return (String)attributes.get(key);
	}
	/**
	 * @param attributes The attributes to set.
	 */
	public void setAttribute(String key, String value) {
		attributes.put(key, value);
	}
	
	public Set keySet() {
		return attributes.keySet();
	}
	
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
	public String getBodyText() {
		return bodyText;
	}
	
	public void setBodyText(String bodyText) {
		this.bodyText = bodyText;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return '<' + type + '>';
	}
}
