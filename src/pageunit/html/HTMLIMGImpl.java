package pageunit.html;


public class HTMLIMGImpl extends HTMLComponentBase implements HTMLIMG {
	String src;
	
	public HTMLIMGImpl(String name, String src) {
		super(name);
		setSrc(src);
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}


}
