package pageunit.html;

import java.util.List;

public interface HTMLPage extends HTMLContainer {
	public void addAnchor(HTMLAnchor a);
	public HTMLAnchor getAnchorByURL(String regex);
	public HTMLAnchor getAnchorByText(String regex);
	public HTMLAnchor getAnchorByName(String regex);
	public List<HTMLAnchor> getAnchors();
	
	public void addForm(HTMLForm f);
	public HTMLForm getFormByName(String regex);
	public HTMLForm getFormByURL(String regex);
	public List<HTMLForm> getForms();
}
