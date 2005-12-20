package regress;

import pageunit.TestUtils;
import junit.framework.TestCase;

public class TestUtilsTest extends TestCase {

	/*
	 * Test method for 'pageunit.TestUtils.isRedirectCode(int)'
	 */
	public void testIsRedirectCode() {
		assertTrue("301", TestUtils.isRedirectCode(301));
	}

	/*
	 * Test method for 'pageunit.TestUtils.isErrorCode(int)'
	 */
	public void testIsErrorCode() {
		assertTrue("404", TestUtils.isErrorCode(404));
	}

	/*
	 * Test method for 'pageunit.TestUtils.checkResultForPattern(CharSequence, String)'
	 */
	public void testCheckResultForPattern() {
		assertTrue(TestUtils.checkResultForPattern("a b c", "b"));
		assertFalse(TestUtils.checkResultForPattern("a b c", "d"));
	}

	/*
	 * Test method for 'pageunit.TestUtils.{set,is}Debug()'
	 */
	public void testDebug() {
		for (boolean b : new boolean[]{true, false} ) {
			TestUtils.setDebug(b);
			assertEquals(b, TestUtils.isDebug());
		}
	}

}
