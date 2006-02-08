package pageunit;

import junit.framework.Test;
import junit.framework.TestSuite;

/** This is NOT part of the release, just what its name implies.
 * @author ian
 */
public class QuickDemo {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for regress");
		//$JUnit-BEGIN$
		try {
			suite.addTest(new ScriptTestCase("demos/testphpproject.txt"));
			suite.addTest(new ScriptTestCase("demos/j2ee.txt"));
			suite.addTest(new ScriptTestCase("demos/tiny.txt"));			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//$JUnit.END$
		return suite;
	}
}
