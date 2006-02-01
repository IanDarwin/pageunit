package regress;

import java.io.StringReader;

import pageunit.TestRunner;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

/** Horrible name, sorry: test some of the self-contained methods in TestRunner
 * @author ian
 */
public class TestRunnerTest extends TestCase {
	
	public void testGet1or2CanGetOne() {
		String[] r;
		r = TestRunner.getOneOrTwoArgs("no descr", "one", ' ');
		assertEquals(r.length, 2);
		assertNotNull(r[0]);
		assertNull(r[1]);
	}
	
	public void testGet1or2CanGetTwo() {
		String[] r;
		r = TestRunner.getOneOrTwoArgs("no descr", "one two", ' ');
		assertEquals(r.length, 2);
		assertNotNull(r[0]);
		assertNotNull(r[1]);
	}
	
	public void testGet2Succeeds() {
		String[] r;
		r = TestRunner.getTwoArgs("no descr", "one two", ' ');
		assertEquals(r.length, 2);
		assertNotNull(r[0]);
		assertNotNull(r[1]);
	}
	
	public void testGet2Short() {
		String[] r;
		r = TestRunner.getTwoArgs("no descr", " two", ' ');
		assertEquals(r.length, 2);
		assertNotNull(r[0]);
		assertNotNull(r[1]);
	}
	
	public void testGet2CatchesFailure() {
		try {
			TestRunner.getTwoArgs("test for failure", "one two", '/');
		} catch (Throwable e) {
			System.out.println("Caught expected exception");
			return;
		}
		throw new AssertionError("Did not catch expected Exception");
	}
	
	public void testGetBooleanSuccess() {
		assertTrue(TestRunner.getBoolean("t"));
		assertFalse(TestRunner.getBoolean("off"));
	}
	
	public void testGetBooleanFailure() {
		try {
			TestRunner.getBoolean("fuddle duddle");
		} catch (Throwable e) {
			System.out.println("Caught expected exception from getBoolean");
			return;
		}
		throw new AssertionError("Did not catch expected Exception from getBoolean");
	}
	
	public void testAnotherGetBooleanFailure() {
		try {
			TestRunner.getBoolean("on and on");
		} catch (Exception e) {
			System.out.println("Caught expected exception from getBoolean");
			return;
		}
		throw new AssertionError("Did not catch expected Exception from getBoolean");
	}
	
	public void testCommandM() throws Exception {
		String script = "P http://www.phenogenomics.ca/\n" +
			"M Toronto (Centre) for Phenogenomics\n" +
			"E M0 is ${M0}\n" +
			"E M1 is ${M1}\n";
		Test t = new TestRunner(new StringReader(script), "Imbedded test data");
		t.run(new TestResult());
	}
}
