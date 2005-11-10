package regress;

import pageunit.ResultStat;
import junit.framework.TestCase;

public class ResultStatTest extends TestCase {
	final ResultStat r = new ResultStat(123,456,789);
	final ResultStat r0 = new ResultStat(0,0,0);
	final ResultStat r2 = new ResultStat(2,4,6);
	
	public void testAccessors() {
		assertEquals("getNtest", 123, r.getTests());
		assertEquals("getNSucceeded", 456, r.getSuccesses());
		assertEquals("getNFailures", 789, r.getFailures());
	}
	public void testAdd() throws Exception {
		
		assertEquals(r2, r0.add(r2));
		assertNotSame(r2, r2.add(r0));
	}
	
	public void testHashCode() throws Exception {
		assertEquals(r0.hashCode(), r0.hashCode());
		assertFalse(r0.hashCode() == r2.hashCode());
	}
	
	/** This test is pretty gruess, but it does show that
	 * the fields are output in the correct order.
	 * If you change toString()'s output you'll know soon enough.
	 */
	public void testToString() {
		assertEquals("Tests Run: 123, Successful: 456, Failed: 789", 
			r.toString());
	}
}
