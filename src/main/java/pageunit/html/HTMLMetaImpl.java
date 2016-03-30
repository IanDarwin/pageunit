package pageunit.html;

public class HTMLMetaImpl extends HTMLComponentBase implements HTMLMeta {

	private final String metaEquiv;
	private final String metaContent;
	
	public String getMetaEquiv() {
		return metaEquiv;
	}

	public String getMetaContent() {
		return metaContent;
	}

	/**
	 * @param name The tag name
	 * @param equiv The equiv element
	 * @param content The tag content
	 */
	public HTMLMetaImpl(String name, String equiv, String content) {
		super(name);
		metaEquiv = equiv;
		metaContent = content;
	}

	@Override
	public String toString() {
		// <meta equiv 'Refresh' Content '0; URL=home.seam'>
		return String.format("<meta equiv '%s' Content '%s'>", metaEquiv, metaContent);
	}
}
