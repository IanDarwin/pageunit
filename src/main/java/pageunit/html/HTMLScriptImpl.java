package pageunit.html;

public class HTMLScriptImpl extends HTMLComponentBase implements HTMLScript {

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
	
	@Override
	public String toString() {
		return String.format("<Script name='%s' language='%s'>", getName(), getLanguage());
	}
}
