package pageunit.html;

import java.util.List;
import java.util.NoSuchElementException;

import pageunit.http.ScriptResult;
import pageunit.http.WebResponse;

public interface WebPage extends HTMLComponent {

    public HTMLComponent getDocumentElement();
    public String getPageEncoding();
    public HTMLComponent createElement(String t);
    public HTMLAnchor getAnchorByName(String t)       throws NoSuchElementException;
    public HTMLAnchor getAnchorByHref(String t)       throws NoSuchElementException;
    public java.util.List getAnchors();
    public HTMLAnchor getFirstAnchorByText(String t)       throws NoSuchElementException;
    public HTMLForm getFormByName(String t)       throws NoSuchElementException;
    public java.util.List getForms();
    public java.net.URL getFullyQualifiedUrl(String t) throws java.net.MalformedURLException;
    public String getResolvedTarget(String t);
    public WebResponse getWebResponse();
    public java.util.List getTabbableElementIds();
    public java.util.List getTabbableElements();
    public HTMLComponent getHTMLComponentByAccessKey(char c);
    public java.util.List getHTMLComponentsByAccessKey(char c);
    public void assertAllTabIndexAttributesSet();
    public void assertAllAccessKeyAttributesUnique();
    public void assertAllIdAttributesUnique();
    public ScriptResult executeJavaScriptIfPossible(String  s, String t, boolean b, HTMLComponent e);
    public ScriptResult executeJavaScriptFunctionIfPossible(org.mozilla.javascript.Function f, org.mozilla.javascript.Scriptable scr, java.lang.Object[] args, HTMLComponent he);
    public void loadExternalJavaScriptFile(String s);
    public void loadExternalJavaScriptFile(String s, String t);
    public boolean isJavaScript(String s, String t);
    public String getTitleText();
    public void setTitleText(String t);
    public List getFrames();
    public HTMLComponent getFrameByName(String t) throws NoSuchElementException;
    public HTMLComponent pressAccessKey(char c) throws java.io.IOException;
    public HTMLComponent tabToNextElement();
    public HTMLComponent tabToPreviousElement();
    public HTMLComponent getHTMLComponentById(String t) throws NoSuchElementException;
    void addIdElement(HTMLComponent e);
    void removeIdElement(HTMLComponent e);
    public WebPage executeOnChangeHandlerIfAppropriate(HTMLComponent e);
}
