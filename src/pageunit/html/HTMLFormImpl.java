package pageunit.html;

import java.util.ArrayList;
import java.util.List;

import pageunit.http.HTTPMethod;

public class HTMLFormImpl extends HTMLContainerBase implements HTMLForm {

	String action;
	HTTPMethod method;
	List<HTMLInput> inputs = new ArrayList<HTMLInput>();
	
	public HTMLFormImpl(String name, String action, String method) {
		super(name);
		this.action = action;
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

	public List getInputs() {
		return inputs;
	}
	
	public void addInput(HTMLInput i) {
		inputs.add(i);
	}
}
