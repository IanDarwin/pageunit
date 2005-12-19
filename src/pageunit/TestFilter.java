package pageunit;

import pageunit.http.WebResponse;
import pageunit.html.HTMLPage;

/**
 * Site-specific extension mechanism for WebTest package
 * @version $Id$
 */
public interface TestFilter {

		public void filterPage(HTMLPage thePage, WebResponse theResult) throws Exception;
}
