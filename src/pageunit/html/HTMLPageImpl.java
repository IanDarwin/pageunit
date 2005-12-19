package pageunit.html;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class HTMLPageImpl extends HTMLContainerBase implements HTMLPage {

	public HTMLPageImpl(String name) {
		super(name);
	}
	
	private List<HTMLAnchor> anchors = new ArrayList<HTMLAnchor>();
	
	public void addAnchor(HTMLAnchor a) {
		anchors.add(a);
	}

	public HTMLAnchor getAnchorByURL(String regex) {
		Pattern p = Pattern.compile(regex);
		for (HTMLAnchor a : anchors) {
			if (p.matcher(a.getURL()).find())
				return a;
		}
		return null;
	}

	public HTMLAnchor getAnchorByText(String regex) {
		Pattern p = Pattern.compile(regex);
		for (HTMLAnchor a : anchors) {
			final String bodyText = a.getBody();
			if (bodyText != null && p.matcher(bodyText).find())
				return a;
		}
		return null;
	}

	public HTMLAnchor getAnchorByName(String regex) {
		Pattern p = Pattern.compile(regex);
		for (HTMLAnchor a : anchors) {
			if (p.matcher(a.getName()).find())
				return a;
		}
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
		Pattern p = Pattern.compile(regex);
		for (HTMLForm f : forms) {
			if (p.matcher(f.getName()).find())
				return f;
		}
		return null;
	}

	public HTMLForm getFormByURL(String regex) {
		Pattern p = Pattern.compile(regex);
		for (HTMLForm f : forms) {
			if (p.matcher(f.getAction()).find())
				return f;
		}
		return null;
	}

	public List<HTMLForm> getForms() {
		return forms;
	}
}
