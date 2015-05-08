package pageunit.html;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public class NodeBase implements Node {
	
	// XXX MAKE ABSTRACT, REMOVE GET/SET NODE NAME/VALUE

	public String getNodeName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNodeValue() throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setNodeValue(String nodeValue) throws DOMException {
		// TODO Auto-generated method stub

	}

	public short getNodeType() {
		// TODO Auto-generated method stub
		return 0;
	}

	Node errorImmutable() throws DOMException {
		throw new DOMException((short)0, "Immutable Tree");
	}
	
	public Node getParentNode() {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeList getChildNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node getFirstChild() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node getLastChild() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node getPreviousSibling() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node getNextSibling() {
		// TODO Auto-generated method stub
		return null;
	}

	public NamedNodeMap getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	public Document getOwnerDocument() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public Node removeChild(Node oldChild) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public Node appendChild(Node newChild) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildNodes() {
		// TODO Auto-generated method stub
		return false;
	}

	public Node cloneNode(boolean deep) {
		// TODO Auto-generated method stub
		return null;
	}

	public void normalize() {
		// TODO Auto-generated method stub

	}

	public boolean isSupported(String feature, String version) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getNamespaceURI() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPrefix() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPrefix(String prefix) throws DOMException {
		// TODO Auto-generated method stub

	}

	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasAttributes() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getBaseURI() {
		// TODO Auto-generated method stub
		return null;
	}

	public short compareDocumentPosition(Node other) throws DOMException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getTextContent() throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTextContent(String textContent) throws DOMException {
		// TODO Auto-generated method stub

	}

	public boolean isSameNode(Node other) {
		// TODO Auto-generated method stub
		return false;
	}

	public String lookupPrefix(String namespaceURI) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isDefaultNamespace(String namespaceURI) {
		// TODO Auto-generated method stub
		return false;
	}

	public String lookupNamespaceURI(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isEqualNode(Node arg) {
		// TODO Auto-generated method stub
		return false;
	}

	public Object getFeature(String feature, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object setUserData(String key, Object data, UserDataHandler handler) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getUserData(String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
