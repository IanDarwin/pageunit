package pageunit.html;

import java.util.List;

import pageunit.http.HTTPMethod;

public class HTMLFormImpl extends HTMLComponentBase implements HTMLForm {

	String action;
	HTTPMethod method;
	List inputs;
	
	public HTMLFormImpl(String name, String action, String method) {
		super(name);
		this.action = action;
		this.method = HTTPMethod.valueOf(method);
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

	public List getInputs() {
		return inputs;
	}
	
	public void addInput(HTMLInput i) {
		inputs.add(i);
	}
}
