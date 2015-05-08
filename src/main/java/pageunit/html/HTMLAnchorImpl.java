package pageunit.html;

public class HTMLAnchorImpl extends HTMLContainerBase implements HTMLAnchor {

	private String href;
	
	public HTMLAnchorImpl(String name, String href) {
		super(name);
		this.href = href;
	}

	public String getURL() {
		return href;
	}

	@Override
	public String toString() {
		return String.format(
		"Anchor href='%s' name='%s' body='%s'", href, getName(), getBody());
	}

}
