package pageunit.html;

public class HTMLAnchorImpl extends HTMLComponentBase implements HTMLAnchor {

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

}
