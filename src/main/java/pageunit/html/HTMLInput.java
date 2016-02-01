package pageunit.html;

public interface HTMLInput extends HTMLContainer, OnChangeSettable, OnClickSettable {
	public String getValue();
	public HTMLInputType getInputType();
	public void setValue(String value);
	public void setOnChanged(String script);
	public void setOnClick(String script);
}
