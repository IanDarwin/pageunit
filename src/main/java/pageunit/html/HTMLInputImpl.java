package pageunit.html;

public class HTMLInputImpl extends HTMLContainerBase implements HTMLInput {

	private HTMLInputType type;
	private String value;
	private String onClick;
	private String onChange;
	
	public HTMLInputImpl(String name, String type) {
		super(name);
		if (type != null) {
			this.type = HTMLInputType.valueOf(type.toUpperCase());
		}
	}

	public HTMLInputType getInputType() {
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
			"<input type='%s' name='%s' value='%s'/>", type, getName(), value);
	}

	public void setOnChanged(String script) {
		this.onChange = script;
	}

	public void setOnClick(String script) {
		this.onClick = script;
	}

	public String getOnClick() {
		return onClick;
	}

	public void setOnChange(String script) {
		this.onChange = script;
	}

	public String getOnChange() {
		return onChange;
	}
}
