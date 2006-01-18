package pageunit.html;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class HTMLPageImpl extends HTMLContainerBase implements HTMLPage {

	private HTMLTitle title;
	
	private List<HTMLAnchor> anchors = new ArrayList<HTMLAnchor>();

	private List<HTMLForm> forms = new ArrayList<HTMLForm>();

	private String content;
	
	public HTMLPageImpl(String name) {
		super(name);
	}
	
	public void setTitle(HTMLTitle t) {
		this.title = t;
	}
	
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

	public String getTitleText() {
		if (title == null)
			return null;
		return title.getBody();
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public int getContentLength() {
		return content.length();
	}
}
