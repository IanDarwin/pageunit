package pageunit.html;

import java.util.List;

public interface HTMLForm extends HTMLComponent {
	public abstract String getAction();
	public abstract List getInputs();
}
