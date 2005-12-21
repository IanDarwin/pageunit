package regress;

import junit.framework.Test;
import regress.html.*;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for regress");
		//$JUnit-BEGIN$
		suite.addTestSuite(ResultStatTest.class);
		suite.addTestSuite(TestUtilsTest.class);
		suite.addTestSuite(HTMLComponentFactoryTest.class);
		suite.addTestSuite(ParserTest.class);
		suite.addTestSuite(ParserTest2.class);
		//$JUnit-END$
		return suite;
	}

}
