package pageunit.html;

public class HTMLParseException extends Exception {

	private static final long serialVersionUID = 5313124205302445808L;

	public HTMLParseException() {
		super();
	}

	public HTMLParseException(String message) {
		super(message);
			}

	public HTMLParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public HTMLParseException(Throwable cause) {
		super(cause);
	}
}
