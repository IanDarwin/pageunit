package pageunit;

/** Simple value holder for number run, succeeded, failed.
 * @author ian
 */
/**
 * @author ian
 */
public class ResultStat {

	private int numTests;
	private int numSucceeded;
	private int numFailures;
	
	/**
	 * Construct a ResultStat given the number of tests, successes and failures
	 * @param tests
	 * @param succeeded
	 * @param failures
	 */
	public ResultStat(int tests, int succeeded, int failures) {
		super();
		numTests = tests;
		numSucceeded = succeeded;
		numFailures = failures;
	}

	public int getFailures() {
		return numFailures;
	}

	public int getSuccesses() {
		return numSucceeded;
	}

	public int getTests() {
		return numTests;
	}
	
	/**
	 * Add this Results to another, return a new Results that is the sum
	 * @param r2 The Results to be added with this one.
	 * @return
	 */
	public ResultStat add(ResultStat r2) {
		return new ResultStat(
			numTests + r2.numTests,
			numSucceeded + r2.numSucceeded, 
			numFailures + r2.numFailures);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ResultStat)) {
			return false;
		}
		ResultStat r2 = (ResultStat)obj;
		return numTests == r2.numTests &&
		numSucceeded == r2.numSucceeded &&
		numFailures == r2.numFailures;
	}

	@Override
	public int hashCode() {
		return numTests<<8 | numSucceeded<<4 | numFailures;
	}

	@Override
	public String toString() {
		return "Tests Run: " + numTests + 
			", Successful: " + 
			(numSucceeded==0 ? "NONE" : Integer.toString(numSucceeded)) +
			", Failed: " +
			(numFailures==0 ? "NONE" : Integer.toString(numFailures));
	}
	
}
