package pageunit.html;

public class HTMLTitleImpl extends HTMLContainerBase implements HTMLTitle {

	public HTMLTitleImpl(String name) {
		super(name);
	}

	@Override
	public String toString() {		
		return String.format(
			"<title>%s</title>", getBody());
	}

}
