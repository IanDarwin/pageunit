package pageunit.html;

public class HTMLTitleImpl extends HTMLComponentBase implements HTMLTitle {

	public HTMLTitleImpl(String name) {
		super(name);
	}

	@Override
	public String toString() {		
		return String.format(
			"<title>%s</title>", getBody());
	}

}
