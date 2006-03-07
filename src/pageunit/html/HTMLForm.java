package pageunit.html;

import java.util.List;

public interface HTMLForm extends HTMLContainer {
	public abstract String getAction();
	public abstract List<HTMLInput> getInputs();
	public HTMLInput getInputByName(String regex);
	public void setOnSubmit(String script);
	public String getOnSubmit();
}
