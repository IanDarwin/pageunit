package pageunit.html;

/**
 * Represents a Script tag; normally used to define functions that get invoked by
 * one-line scripts written as onChange, onClick or onSubmit attributes to other elements.
 */
public interface HTMLScript extends HTMLContainer {
	public void setLanguage(String lang);
	public String getLanguage();
}
