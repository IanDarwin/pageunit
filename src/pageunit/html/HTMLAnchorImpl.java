package pageunit.html;

public class HTMLAnchorImpl implements HTMLAnchor {

	private String name;
	private String href;
	private String body;
	
	public HTMLAnchorImpl(String name, String href) {
		this.name = name;
		this.href = href;
	}

	public String getURL() {
		return href;
	}

	public String getName() {
		return name;
	}

	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}

}
