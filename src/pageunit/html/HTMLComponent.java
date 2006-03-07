package pageunit.html;

public interface HTMLComponent {
    public String getName();
    /* no setName, always passed in constructor, immutable */
    public String getBody();
    public void setBody(String body);
    public void appendBody(String body);
}
