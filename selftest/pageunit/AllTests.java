package regress;

import junit.framework.Test;
import junit.framework.TestSuite;
import regress.html.HTMLComponentFactoryTest;
import regress.html.ParserTest;
import regress.html.ParserTest2;
import regress.linkchecker.LinkCheckerTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for regress");
		//$JUnit-BEGIN$
		suite.addTestSuite(ResultStatTest.class);
		suite.addTestSuite(TestRunnerTest.class);
		suite.addTestSuite(TestUtilsTest.class);
		suite.addTestSuite(HTMLComponentFactoryTest.class);
		suite.addTestSuite(ParserTest.class);
		suite.addTestSuite(ParserTest2.class);
		suite.addTestSuite(LinkCheckerTest.class);
		//$JUnit-END$
		return suite;
	}

}
