package pageunit.html;

public class HTMLAnchorImpl extends HTMLContainerBase implements HTMLAnchor {

	private String href;
	private String body;
	
	public HTMLAnchorImpl(String name, String href) {
		super(name);
		this.href = href;
	}

	public String getURL() {
		return href;
	}

	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return String.format(
		"Anchor href='%s' name='%s' body='%s'", href, getName(), body);
	}

}
