package pageunit.html;

public class GenericHTMLComponent extends HTMLComponentBase {
	private String type;

	public GenericHTMLComponent(String name, String type) {
		super(name);
		this.type = type;
	}

	@Override
	public String toString() {
		return String.format("<%s>", type);
	}

	protected String getType() {
		return type;
	}

	protected void setType(String type) {
		this.type = type;
	}
}
