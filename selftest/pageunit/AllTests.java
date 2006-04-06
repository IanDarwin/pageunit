package pageunit;

import junit.framework.Test;
import junit.framework.TestSuite;
import pageunit.html.HTMLComponentBaseTest;
import pageunit.html.HTMLComponentFactoryTest;
import pageunit.html.HTMLMetaImplTest;
import pageunit.html.HTMLSelectTest;
import pageunit.html.ParserTest;
import pageunit.html.ParserTest2;
import pageunit.html.ParserTest3;
import pageunit.html.ParserTest4;
import pageunit.javascript.JSTest;
import pageunit.linkchecker.LinkCheckerTest;

/** Runs all self-tests for PageUnit
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for regress");
		//$JUnit-BEGIN$
		suite.addTestSuite(ScriptFileTest.class);
		suite.addTestSuite(TestUtilsTest.class);
		suite.addTestSuite(TestXandY.class);
		suite.addTestSuite(HTMLComponentBaseTest.class);
		suite.addTestSuite(HTMLComponentFactoryTest.class);
		suite.addTestSuite(HTMLSelectTest.class);
		suite.addTestSuite(HTMLMetaImplTest.class);
		suite.addTestSuite(ParserTest.class);
		suite.addTestSuite(ParserTest2.class);
		suite.addTestSuite(ParserTest3.class);
		suite.addTestSuite(ParserTest4.class);
		suite.addTestSuite(JSTest.class);
		suite.addTestSuite(LinkCheckerTest.class);
		//$JUnit-END$
		return suite;
	}

}
