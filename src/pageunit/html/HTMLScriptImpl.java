package pageunit.html;

public class HTMLScriptImpl extends HTMLContainerBase implements HTMLScript {

	private String lang;

	public HTMLScriptImpl(String name) {
		super(name);
	}

	public void setLanguage(String lang) {
		this.lang = lang;
	}

	public String getLanguage() {
		return lang;
	}
}
