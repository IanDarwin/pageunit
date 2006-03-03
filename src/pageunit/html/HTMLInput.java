package pageunit.html;

public interface HTMLInput extends HTMLContainer {
	enum Type {
		TEXT, PASSWORD, RADIO, HIDDEN, SUBMIT, CHECKBOX, RESET, FILE, BUTTON
	}
	public String getValue();
	public void setValue(String value);
	public void setOnChanged(String script);
	public void setOnClick(String script);
}
