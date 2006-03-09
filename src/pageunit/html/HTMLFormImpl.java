package pageunit.html;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import pageunit.http.HTTPMethod;

public class HTMLFormImpl extends HTMLContainerBase implements HTMLForm {

	String action;
	HTTPMethod method;
	List<HTMLInput> inputs = new ArrayList<HTMLInput>();
	private String onSubmit;
	
	public HTMLFormImpl(String name, String action, String method) {
		super(name);
		this.action = action;
		if (method == null) {
			System.err.println("Warning: form has no method attribute");
			method = "get";
		}
		this.method = HTTPMethod.valueOf(method.toUpperCase());
	}

	public HTTPMethod getMethod() {
		return method;
	}

	public void setMethod(HTTPMethod method) {
		this.method = method;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public List<HTMLInput> getInputs() {
		return inputs;
	}
	
	public void addInput(HTMLInput i) {
		inputs.add(i);
	}

	public HTMLInput getInputByName(String regex) {
		Pattern patt = Pattern.compile(regex);
		for (HTMLInput i : inputs) {
			final String linksName = i.getName();
			if (linksName == null)
				continue;
			if (regex.equals(linksName) ||
				(patt.matcher(linksName).find())) {
				return i;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {		
		return String.format(
			"<form method=%s action=%s name=%s onsubmit=%s>", method, action, getName(), onSubmit);
	}

	public void setOnSubmit(String script) {
		this.onSubmit = script;
	}

	public String getOnSubmit() {
		return onSubmit;
	}

}
