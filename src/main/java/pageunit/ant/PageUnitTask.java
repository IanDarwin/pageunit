package pageunit.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import pageunit.PageUnit;

/**
 * Simple Ant Task for running PageUnit tests.
 * Usage Prerequisite:
 * junit and pageunit (and their depends) on classpath.
 * &lt;taskdef name="pageunit" classname="pageunit.ant.PageUnitTask"&gt;
 * Usage examples: 
 * &lt;pageunit file="/foo.bar/test.txt"/&gt;
 * &lt;pageunit testPath="dir1:dir2" test="test1.txt"/&gt;
 * &lt;pageunit dir="/foo.bar"/&gt;
 */
public class PageUnitTask extends Task {

	private File theFile;
	private File theDir;
	private String theTest;
	private Path thePath;
	
	@Override
	public void execute() throws BuildException {
		PageUnit.init();
		try {
			if (theFile != null) {
				PageUnit.processFile(theFile);
			} else if (theDir != null) {
				PageUnit.processFile(theDir);
			} else if (theTest != null) {
				if (thePath == null) {
					PageUnit.processFile(new File(theTest));
				} else {
					// walk thePath, looking for test, first found, process 
					throw new BuildException("code not written yet: walk thePath, looking for test, first found, process");
				}
			} else {
				throw new BuildException("Either file, or dir, or test (and optionally path) must be specified");			
			}
		} catch (Exception e) {
			throw new BuildException(e.toString());
		}
		if (PageUnit.isFailed()) {
			throw new BuildException(PageUnit.getTestResults().toString());
		}
	}


	public void addText(String unwantedText) {
		if (unwantedText != null && unwantedText.trim().length() > 0) {
			throw new BuildException(String.format(
				"The PageUnit task does not support nested text: `%s'", unwantedText));
		}
	}
	
	// --------------- Simple Accessors -------------------

	public void setDir(File theDir) {
		this.theDir = theDir;
	}

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
