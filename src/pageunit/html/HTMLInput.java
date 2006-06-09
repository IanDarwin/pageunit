package pageunit.html;

public interface HTMLInput extends HTMLContainer, OnChangeSettable, OnClickSettable {
	enum InputType {
		TEXT, PASSWORD, RADIO, HIDDEN, SUBMIT, CHECKBOX, RESET, FILE, BUTTON, SELECT
	}
	public String getValue();
	public InputType getInputType();
	public void setValue(String value);
	public void setOnChanged(String script);
	public void setOnClick(String script);
}
