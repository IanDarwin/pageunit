package pageunit;

import junit.framework.Test;
import junit.framework.TestResult;

/** This class is just a wrapper to represents one actual test, 
 * which will have been read from a file...
 * @author ian
 */
public class PageTest implements Test {
	private final Command command;
	private final String args;
	private final String fileName;
	private final int lineNumber;
	/**
	 * @param commandChar 
	 * @param fileName
	 * @param lineNumber
	 */
	public PageTest(final char commandChar, final String args, final String fileName, final int lineNumber) {
		super();
		String commandString = new String(new char[] {commandChar});
		if (commandChar == '<')
			command = Command.SOURCE;
		else if (commandChar == '=')
			command = Command.SET;
		else try {
			command = Command.valueOf(commandString);
		} catch (IllegalArgumentException e) {
			System.err.printf("Could not handle command `%s'%n", commandString);
			throw e;
		}
		this.args = args;
		this.fileName = fileName;
		this.lineNumber = lineNumber;
	}

	public int countTestCases() {
		return 1;					// This does represent one test read from a file.
	}

	public void run(TestResult result) {
		throw new IllegalStateException("Called run method in PageTest line wrapper class!");
	}	
	public String toString() {
		return String.format("PageTest[%s %s:%d]", command, fileName, lineNumber);
	}

	public Command getCommand() {
		return command;
	}
	
	public String getArguments() {
		return args;
	}

	public String getFileName() {
		return fileName;
	}

	public int getLineNumber() {
		return lineNumber;
	}

}
