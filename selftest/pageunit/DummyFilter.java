package regress;

import pageunit.TestFilter;
import pageunit.html.HTMLPage;
import pageunit.http.WebResponse;

/**
 * Dummy TestFilter, only for use by "TestXandY" test case
 */
public class DummyFilter implements TestFilter {
	public void filterPage(HTMLPage thePage, WebResponse theResult) throws Exception {
		System.out.println("(AnonFilter).filterPage()");
	}			
};