package regress.webtest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Suite to run all the HTTP tests; run the few self-contained tests that
 * really need Java code before you run the TestRunner itself.
 * @version $Id$
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("regress.webtest tests the TCP Site");
		//$JUnit-BEGIN$
		suite.addTestSuite(BasicServerTest.class);
		suite.addTestSuite(LoginTest.class);
		suite.addTestSuite(TestRunner.class);
		//$JUnit-END$
		return suite;
	}
}
