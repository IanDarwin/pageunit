package pageunit.html;

import java.util.List;
import java.util.NoSuchElementException;

import pageunit.http.ScriptResult;
import pageunit.http.WebResponse;

public interface WebPage extends HtmlElement {

    public HtmlElement getDocumentElement();
    public String getPageEncoding();
    public HtmlElement createElement(String t);
    public HtmlAnchor getAnchorByName(String t)       throws NoSuchElementException;
    public HtmlAnchor getAnchorByHref(String t)       throws NoSuchElementException;
    public java.util.List getAnchors();
    public HtmlAnchor getFirstAnchorByText(String t)       throws NoSuchElementException;
    public HtmlForm getFormByName(String t)       throws NoSuchElementException;
    public java.util.List getForms();
    public java.net.URL getFullyQualifiedUrl(String t) throws java.net.MalformedURLException;
    public String getResolvedTarget(String t);
    public WebResponse getWebResponse();
    public java.util.List getTabbableElementIds();
    public java.util.List getTabbableElements();
    public HtmlElement getHtmlElementByAccessKey(char c);
    public java.util.List getHtmlElementsByAccessKey(char c);
    public void assertAllTabIndexAttributesSet();
    public void assertAllAccessKeyAttributesUnique();
    public void assertAllIdAttributesUnique();
    public ScriptResult executeJavaScriptIfPossible(String  s, String t, boolean b, HtmlElement e);
    public ScriptResult executeJavaScriptFunctionIfPossible(org.mozilla.javascript.Function f, org.mozilla.javascript.Scriptable scr, java.lang.Object[] args, HtmlElement he);
    public void loadExternalJavaScriptFile(String s);
    public void loadExternalJavaScriptFile(String s, String t);
    public boolean isJavaScript(String s, String t);
    public String getTitleText();
    public void setTitleText(String t);
    public List getFrames();
    public HtmlElement getFrameByName(String t) throws NoSuchElementException;
    public HtmlElement pressAccessKey(char c) throws java.io.IOException;
    public HtmlElement tabToNextElement();
    public HtmlElement tabToPreviousElement();
    public HtmlElement getHtmlElementById(String t) throws NoSuchElementException;
    void addIdElement(HtmlElement e);
    void removeIdElement(HtmlElement e);
    public WebPage executeOnChangeHandlerIfAppropriate(HtmlElement e);
}
