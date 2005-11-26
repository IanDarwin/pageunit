package pageunit.html;

import java.util.List;

public interface HTMLForm extends HTMLContainer {
	public abstract String getAction();
	public abstract List getInputs();
}
