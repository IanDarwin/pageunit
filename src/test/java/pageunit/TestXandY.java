package pageunit;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

public class TestXandY extends TestCase {
	
	public void test() throws Exception {
		System.out.println("TestXandY.test()");
		String script = "D on\nX pageunit.DummyFilter\nY pageunit.DummyFilter\n";
		Test t = new ScriptTestCase(null, new StringReader(script), "Imbedded XY test data");
		TestResult tr;
		t.run(tr = new TestResult());
		System.out.printf("%d runs, %d errors, %d failures%n", tr.runCount(), tr.errorCount(), tr.failureCount());
	}
}
