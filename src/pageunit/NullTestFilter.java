package pageunit;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Null TestFilter implementation
 * @version $Id$
 */
public class NullTestFilter implements TestFilter {

	/**
	 * Null test -- always passes.
	 */
	public void filterPage(HtmlPage thePage,  WebResponse theResult) throws Exception {
		return; // pass
	}
}
