package pageunit.html;

public abstract class HTMLComponentBase implements HTMLComponent {

	private String name;
	private StringBuffer body = new StringBuffer();
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
		return body.toString();
	}

	public void setBody(String body) {
		this.body.replace(0, this.body.length(), body);
		if (debug) {
			System.out.printf("Set body of %s to this value: %s (%s)%n", this, body, getBody());
		}
	}
	
	public void appendBody(String body) {
		this.body.append(body);
		if (debug) {
			System.out.printf("Added to body of %s this value: %s (%s)%n", this, body, getBody());
		}
	}
	
	@Override
	public String toString() {
		return String.format("<%s: Name %s>", getClass(), getName());
	}
}
