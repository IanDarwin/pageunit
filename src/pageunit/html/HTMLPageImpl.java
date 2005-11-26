package pageunit.html;

import java.util.ArrayList;
import java.util.List;

public class HTMLPageImpl extends HTMLContainerBase implements HTMLPage {

	public HTMLPageImpl(String name) {
		super(name);
	}
	
	private List<HTMLAnchor> anchors = new ArrayList<HTMLAnchor>();
	
	public void addAnchor(HTMLAnchor a) {
		anchors.add(a);
	}

	public HTMLAnchor getAnchorByURL(String regex) {
		// TODO Auto-generated method stub
		return null;
	}

	public HTMLAnchor getAnchorByText(String regex) {
		// TODO Auto-generated method stub
		return null;
	}

	public HTMLAnchor getAnchorByName(String regex) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<HTMLAnchor> getAnchors() {
		return anchors;
	}

	private List<HTMLForm> forms = new ArrayList<HTMLForm>();

	public void addForm(HTMLForm f) {
		forms.add(f);
	}

	public HTMLForm getFormByName(String regex) {
		// TODO Auto-generated method stub
		return null;
	}

	public HTMLForm getFormByURL(String regex) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<HTMLForm> getForms() {
		return forms;
	}
}
