package pageunit;

import pageunit.http.*;
import pageunit.html.*;

/**
 * Null TestFilter implementation
 * @version $Id$
 */
public class NullTestFilter implements TestFilter {

	/**
	 * Null test -- always passes.
	 */
	public void filterPage(HTMLPage thePage,  WebResponse theResult) throws Exception {
		return; // pass
	}
}
