package regress.html;

import pageunit.html.*;

import junit.framework.TestCase;

public class HTMLSelectTest extends TestCase {
	public void testOne() throws Exception {
		HTMLSelect sel = new HTMLSelectImpl("province");
		HTMLOption opt;
		String[] provinceNames = new String[] { "Ontario", "Quebec" };
		for (String s : provinceNames) {
			opt = new HTMLOptionImpl(s);
			sel.addChild(opt);
		}
		assertEquals(provinceNames[0], sel.getChildren().get(0).getName());
		sel.setValue(provinceNames[1]);
		assertEquals(provinceNames[1], sel.getValue());
	}
}
