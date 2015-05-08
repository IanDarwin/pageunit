package pageunit.html;

import java.util.List;

import org.w3c.dom.Document;

/** The Content of an HTML page is the raw HTML page, whilst the
 * Body of an HTMLContainer is the text content it contains.
 */
public interface HTMLPage extends HTMLContainer, Document {
	
	void setContent(String bodyContent);
	public String getContent();
	public int getContentLength();
	
	public void addAnchor(HTMLAnchor a);
	public HTMLAnchor getAnchorByURL(String regex);
	public HTMLAnchor getAnchorByText(String regex);
	public HTMLAnchor getAnchorByName(String regex);
	public List<HTMLAnchor> getAnchors();
	
	public void addForm(HTMLForm f);
	public HTMLForm getFormByName(String regex);
	public HTMLForm getFormByURL(String regex);
	public List<HTMLForm> getForms();
	
	public String getTitleText();
}
