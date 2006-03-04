package pageunit.html;

public abstract class HTMLComponentBase implements HTMLComponent {

	private String name;
	private String body;
	private boolean debug = false;

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
		if (debug) {
			System.out.printf("Set body of %s to %s%n", getClass(), getBody());
		}
	}
}
