package pageunit;

/** Simple value holder for number run, succeeded, failed.
 * @author ian
 */
/**
 * @author ian
 */
public class Results {

	private int nTests;
	private int nSucceeded;
	private int nFailures;
	
	/**
	 * @param failures
	 * @param succeeded
	 * @param tests
	 */
	public Results(int tests, int failures, int succeeded) {
		super();
		nTests = tests;
		nFailures = failures;
		nSucceeded = succeeded;
	}

	public int getNFailures() {
		return nFailures;
	}

	public int getNSucceeded() {
		return nSucceeded;
	}

	public int getNTests() {
		return nTests;
	}
	
	/**
	 * Add this Results to another, return a new Results that is the sum
	 * @param r2 The Results to be added with this one.
	 * @return
	 */
	public Results add(Results r2) {
		return new Results(
			nTests + r2.nTests,
			nFailures + r2.nFailures,
			nSucceeded + r2.nSucceeded);
	}
}
