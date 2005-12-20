package pageunit.html;

public interface HTMLInput extends HTMLContainer {
	enum Type {
		TEXT, PASSWORD, RADIO, HIDDEN, SUBMIT
	}
	public String getValue();
	public void setValue(String value);
}
