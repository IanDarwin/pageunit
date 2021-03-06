package pageunit.html;

import org.w3c.dom.DOMException;

public abstract class HTMLComponentBase extends NodeBase implements HTMLComponent {

	private String name;
	private StringBuffer body = new StringBuffer();
	private boolean debug = false;
	private static int unnamedComponentNumber = 0;

	public HTMLComponentBase(String name) {
		if (name == null || name.isEmpty()) {
			name = generateMissingName();
		}
		this.name = name;
	}

	public String generateMissingName() {
		return getClass().getSimpleName() + Integer.toString(unnamedComponentNumber++);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getBody() {
		return body.toString();
	}

	public void setBody(String body) {
		this.body.replace(0, this.body.length(), body);
		if (debug) {
			System.out.printf("Set body of %s to this value: %s (%s)%n", this, body, getBody());
		}
	}
	
	public void appendBody(String body) {
		this.body.append(body);
		if (debug) {
			System.out.printf("Added to body of %s this value: %s (%s)%n", this, body, getBody());
		}
	}
	
	@Override
	public String toString() {
		return String.format("<%s: Name %s>", getClass().getSimpleName(), getName());
	}

	public String getNodeName() {
		return name;
	}

	public String getNodeValue() throws DOMException {
		return getTextContent();
	}

	public void setNodeValue(String nodeValue) throws DOMException {
		setTextContent(nodeValue);
	}

	public short getNodeType() {
		// TODO Auto-generated method stub
		return 0;
	}
}
