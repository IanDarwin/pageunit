package regress.webtest;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Null TestFilter implementation
 * @version $Id$
 */
public class NullTestFilter implements TestFilter {
	private static TestFilter instance = new NullTestFilter();
	private NullTestFilter() {}
	/**
	 * @return
	 */
	public static TestFilter getInstance() {		
		return instance;
	}

	/**
	 * Null test -- always passes.
	 */
	public void filterPage(HtmlPage thePage,  WebResponse theResult) throws Exception {
		return; // pass
	}
}
