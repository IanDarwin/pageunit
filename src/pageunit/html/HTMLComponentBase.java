package pageunit.html;

public abstract class HTMLComponentBase implements HTMLComponent {

	private String name;
	private String body;

	public HTMLComponentBase(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
