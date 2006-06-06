package pageunit.html;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class HTMLPageImpl extends HTMLContainerBase implements HTMLPage {

	private HTMLTitle title;
	
	private List<HTMLAnchor> anchors = new ArrayList<HTMLAnchor>();

	private List<HTMLForm> forms = new ArrayList<HTMLForm>();

	private String content;
	
	public HTMLPageImpl(String name) {
		super(name);
	}
	
	public void setTitle(HTMLTitle t) {
		this.title = t;
	}
	
	public void addAnchor(HTMLAnchor a) {
		anchors.add(a);
	}

	public HTMLAnchor getAnchorByURL(String regex) {
		Pattern p = Pattern.compile(regex);
		for (HTMLAnchor a : anchors) {
			if (p.matcher(a.getURL()).find())
				return a;
		}
		return null;
	}

	public HTMLAnchor getAnchorByText(String regex) {
		Pattern p = Pattern.compile(regex);
		for (HTMLAnchor a : anchors) {
			final String bodyText = a.getBody();
			if (bodyText != null && p.matcher(bodyText).find())
				return a;
		}
		return null;
	}

	public HTMLAnchor getAnchorByName(String regex) {
		Pattern p = Pattern.compile(regex);
		for (HTMLAnchor a : anchors) {
			if (p.matcher(a.getName()).find())
				return a;
		}
		return null;
	}

	public List<HTMLAnchor> getAnchors() {
		return anchors;
	}


	public void addForm(HTMLForm f) {
		forms.add(f);
	}

	public HTMLForm getFormByName(String regex) {
		Pattern p = Pattern.compile(regex);
		for (HTMLForm f : forms) {
			if (p.matcher(f.getName()).find())
				return f;
		}
		return null;
	}

	public HTMLForm getFormByURL(String regex) {
		Pattern p = Pattern.compile(regex);
		for (HTMLForm f : forms) {
			if (p.matcher(f.getAction()).find())
				return f;
		}
		return null;
	}

	public List<HTMLForm> getForms() {
		return forms;
	}

	public String getTitleText() {
		if (title == null)
			return null;
		return title.getBody();
	}
	
	public String getTitle() {		
		return title == null ? null : title.getBody();
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public int getContentLength() {
		return content.length();
	}

	public DocumentType getDoctype() {
		// TODO Auto-generated method stub
		return null;
	}

	public DOMImplementation getImplementation() {
		// TODO Auto-generated method stub
		return null;
	}

	public Element getDocumentElement() {
		// TODO Auto-generated method stub
		return null;
	}

	public Element createElement(String tagName) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public DocumentFragment createDocumentFragment() {
		// TODO Auto-generated method stub
		return null;
	}

	public Text createTextNode(String data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Comment createComment(String data) {
		// TODO Auto-generated method stub
		return null;
	}

	public CDATASection createCDATASection(String data) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public Attr createAttribute(String name) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public EntityReference createEntityReference(String name) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeList getElementsByTagName(String tagname) {
		// TODO Auto-generated method stub
		return null;
	}

	public Node importNode(Node importedNode, boolean deep) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Element getElementById(String elementId) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInputEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getXmlEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getXmlStandalone() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
		// TODO Auto-generated method stub
		
	}

	public String getXmlVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setXmlVersion(String xmlVersion) throws DOMException {
		// TODO Auto-generated method stub
		
	}

	public boolean getStrictErrorChecking() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setStrictErrorChecking(boolean strictErrorChecking) {
		// TODO Auto-generated method stub
		
	}

	public String getDocumentURI() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDocumentURI(String documentURI) {
		// TODO Auto-generated method stub
		
	}

	public Node adoptNode(Node source) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public DOMConfiguration getDomConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	public void normalizeDocument() {
		// TODO Auto-generated method stub
		
	}

	public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}
}
