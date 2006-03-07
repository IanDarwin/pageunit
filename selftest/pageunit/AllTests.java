package regress;

import junit.framework.Test;
import junit.framework.TestSuite;
import regress.html.HTMLComponentBaseTest;
import regress.html.HTMLComponentFactoryTest;
import regress.html.ParserTest;
import regress.html.ParserTest2;
import regress.javascript.JSTest;
import regress.linkchecker.LinkCheckerTest;

/** Runs all self-tests for PageUnit
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for regress");
		//$JUnit-BEGIN$
		suite.addTestSuite(ScriptFileTest.class);
		suite.addTestSuite(TestUtilsTest.class);
		suite.addTestSuite(HTMLComponentBaseTest.class);
		suite.addTestSuite(HTMLComponentFactoryTest.class);
		suite.addTestSuite(ParserTest.class);
		suite.addTestSuite(ParserTest2.class);
		suite.addTestSuite(JSTest.class);
		suite.addTestSuite(LinkCheckerTest.class);
		//$JUnit-END$
		return suite;
	}

}
