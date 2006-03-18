package pageunit.html;

public class GenericHTMLContainer extends HTMLContainerBase {
	private String type;

	public GenericHTMLContainer(String name, String type) {
		super(name);
		this.type = type;
	}

	@Override
	public String toString() {
		return String.format("<%s>", type);
	}
}
