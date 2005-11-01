package pageunit.html;

import java.util.List;

public interface HtmlForm extends HtmlElement {
	public abstract String getURL();
	public abstract List getInputs();
}
