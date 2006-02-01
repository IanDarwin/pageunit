package pageunit;

import junit.framework.Test;
import junit.framework.TestResult;

/** This class represents one actual test, which will have
 * been read from a file...
 * @author ian
 */
public class PageTest implements Test {
	private final char command;
	private final String fileName;
	private final int lineNumber;
	/**
	 * @param commandChar 
	 * @param fileName
	 * @param lineNumber
	 */
	public PageTest(final char commandChar, final String fileName, final int lineNumber) {
		super();
		this.command = commandChar;
		this.fileName = fileName;
		this.lineNumber = lineNumber;
	}

	public int countTestCases() {
		return 1;
	}

	public void run(TestResult result) {
		throw new IllegalStateException("Called run method in PageTest line wrapper class!");
	}	
	public String toString() {
		return String.format("PageTest: %c %d %s", command, lineNumber, fileName);
	}

}
