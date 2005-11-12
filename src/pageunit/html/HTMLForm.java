package pageunit.html;

import java.util.List;

public interface HtmlForm extends HTMLComponent {
	public abstract String getURL();
	public abstract List getInputs();
}
