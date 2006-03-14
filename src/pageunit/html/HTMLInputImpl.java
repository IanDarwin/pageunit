package pageunit.html;

public class HTMLInputImpl extends HTMLContainerBase implements HTMLInput {

	private Type type;
	private String value;
	private String onClick;
	private String onChanged;
	
	public HTMLInputImpl(String name, String type) {
		super(name);
		if (type != null) {
			this.type = Type.valueOf(type.toUpperCase());
		}
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

	@Override
	public String toString() {		
		return String.format(
			"<input type=%s name=%s value=%s>", type, getName(), value);
	}

	public void setOnChanged(String script) {
		this.onChanged = script;
	}

	public void setOnClick(String script) {
		this.onClick = script;
	}

	public String getOnChanged() {
		return onChanged;
	}

	public String getOnClick() {
		return onClick;
	}

	public void setOnChange(String script) {
		// TODO Auto-generated method stub
		
	}

	public String getOnChange() {
		// TODO Auto-generated method stub
		return null;
	}
}
