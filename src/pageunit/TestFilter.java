package regress.webtest;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Site-specific extension mechanism for WebTest package
 * @version $Id$
 */
public interface TestFilter {

		public void filterPage(HtmlPage thePage, WebResponse theResult) throws Exception;
}
