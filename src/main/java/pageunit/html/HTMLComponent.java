package pageunit.html;

import org.w3c.dom.Node;

public interface HTMLComponent extends Node {
    public String getName();
    /* no setName, always passed in constructor, immutable */
    public String getBody();
    public void setBody(String body);
    public void appendBody(String body);
}
