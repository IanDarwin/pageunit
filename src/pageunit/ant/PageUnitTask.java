package pageunit.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
 * Simple Ant Task for running PageUnit tests.
 * Usage Prerequisite:
 * junit and pageunit (and their depends) on classpath.
 * &lt;taskdef name="pageunit" classname="pageunit.ant.AntTask"&gt;
 * Usage examples: 
 * &lt;pageunit file="/foo.bar/test.txt"/&gt;
 * &lt;pageunit testPath="dir1:dir2" test="test1.txt"/&gt;
 * &lt;pageunit dir="/foo.bar"/&gt;
 */
public class AntTask extends Task {

	private File theFile;
	private String theTest;
	private Path thePath;
	
	@Override
	public void execute() throws BuildException {
		if (theFile == null && theTest == null) {
			throw new BuildException("Either file, or test (and optionally path) must be specified");
		}
		if (theFile != null) {
			// XXX process file
			return;
		}
		if (theTest != null) {
			if (thePath == null) {
				// create File(theTest), try to process
				return;
			} else {
				// walk thePath, looking for test, first found, process 
				return;
			}
		}
		throw new BuildException("Value of fileName must be set");		
	}


	public void addText(String unwantedText) {
		throw new BuildException("The PageUnit task does not support nested text");
	}
	
	// --------------- Simple Accessors -------------------

	public void setFile(File fileName) {
		this.theFile = fileName;
	}
	
	public void setTest(String testName) {
		this.theTest = testName;
	}
	
	public void setTestPath(Path p) {
		this.thePath = p;
	}
}
