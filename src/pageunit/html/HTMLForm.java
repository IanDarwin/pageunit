package pageunit.html;

import java.util.List;

public interface HTMLForm extends HTMLComponent {
	public abstract String getURL();
	public abstract List getInputs();
}
