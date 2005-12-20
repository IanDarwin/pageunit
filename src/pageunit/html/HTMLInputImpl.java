package pageunit.html;

public class HTMLInputImpl extends HTMLContainerBase implements HTMLInput {

	private Type type;
	private String value;
	
	public HTMLInputImpl(String name, String type) {
		super(name);
		this.type = Type.valueOf(type.toUpperCase());
	}

	public Type getType() {
		return type;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
