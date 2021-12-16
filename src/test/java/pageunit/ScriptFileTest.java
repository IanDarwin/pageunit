package pageunit;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

/** Test some of the self-contained methods in ScriptTestCase
 * @author ian
 */
public class ScriptFileTest extends TestCase {
	
	public void testGet1or2CanGetOne() {
		String[] r;
		r = ScriptTestCase.getOneOrTwoArgs("no descr", "one", ' ');
		assertEquals(r.length, 2);
		assertNotNull(r[0]);
		assertNull(r[1]);
	}
	
	public void testGet1or2CanGetTwo() {
		String[] r;
		r = ScriptTestCase.getOneOrTwoArgs("no descr", "one two", ' ');
		assertEquals(r.length, 2);
		assertNotNull(r[0]);
		assertNotNull(r[1]);
	}
	
	public void testGet2Succeeds() {
		String[] r;
		r = ScriptTestCase.getTwoArgs("no descr", "one two", ' ');
		assertEquals(r.length, 2);
		assertNotNull(r[0]);
		assertNotNull(r[1]);
	}
	
	public void testGet2Short() {
		String[] r;
		r = ScriptTestCase.getTwoArgs("no descr", " two", ' ');
		assertEquals(r.length, 2);
		assertNotNull(r[0]);
		assertNotNull(r[1]);
	}
	
	public void testGet2CatchesFailure() {
		try {
			ScriptTestCase.getTwoArgs("test for failure", "one two", '/');
		} catch (Throwable e) {
			System.out.println("Caught expected exception");
			return;
		}
		throw new AssertionError("Did not catch expected Exception");
	}
	
	public void testGetBooleanSuccess() {
		assertTrue(ScriptTestCase.getBoolean("t"));
		assertFalse(ScriptTestCase.getBoolean("off"));
	}
	
	public void testGetBooleanFailure() {
		try {
			ScriptTestCase.getBoolean("fuddle duddle");
		} catch (Throwable e) {
			System.out.println("Caught expected exception from getBoolean");
			return;
		}
		throw new AssertionError("Did not catch expected Exception from getBoolean");
	}
	
	public void testAnotherGetBooleanFailure() {
		try {
			ScriptTestCase.getBoolean("on and on");
		} catch (Exception e) {
			System.out.println("Caught expected exception from getBoolean");
			return;
		}
		throw new AssertionError("Did not catch expected Exception from getBoolean");
	}
	
	public void testCommandM() throws Exception {
		String script = "P http://www.phenogenomics.ca/\n" +
			"M The (Centre) for Phenogenomics\n" +
			"E M0 is ${M0}\n" +
			"E M1 is ${M1}\n";
		Test t = new ScriptTestCase(null, new StringReader(script), "Imbedded M test data");
		t.run(new TestResult());
	}
	
	public void testBadCommand() throws Exception {
		String script = "* This should fail";
		try {
			new ScriptTestCase(null, new StringReader(script), "Imbedded M-bad test data");
			fail("did not throw expected IAE on bad command");
		} catch (IllegalArgumentException e) {
			System.out.println("Caught expected IAE on invalid command");
		}
	}
}
