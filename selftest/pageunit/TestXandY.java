package pageunit;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import pageunit.ScriptTestCase;

public class TestXandY extends TestCase {
	
	public void test() throws Exception {
		System.out.println("TestXandY.test()");
		String script = "D on\nX regress.DummyFilter\nY regress.DummyFilter\n";
		Test t = new ScriptTestCase(null, new StringReader(script), "Imbedded test data");
		TestResult tr;
		t.run(tr = new TestResult());
		System.out.printf("%d runs, %d errors, %d failures%n", tr.runCount(), tr.errorCount(), tr.failureCount());
	}
}
