package pageunit;

import pageunit.Utilities;
import junit.framework.TestCase;

public class TestUtilsTest extends TestCase {

	/*
	 * Test method for 'pageunit.TestUtils.isRedirectCode(int)'
	 */
	public void testIsRedirectCode() {
		assertTrue("301", Utilities.isRedirectCode(301));
	}

	/*
	 * Test method for 'pageunit.TestUtils.isErrorCode(int)'
	 */
	public void testIsErrorCode() {
		assertTrue("404", Utilities.isErrorCode(404));
	}

	/*
	 * Test method for 'pageunit.TestUtils.{set,is}Debug()'
	 */
	public void testDebug() {
		for (boolean b : new boolean[]{true, false} ) {
			Utilities.setDebug(b);
			assertEquals(b, Utilities.isDebug());
		}
	}

}
