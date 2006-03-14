package regress.html;

import pageunit.html.*;

import junit.framework.TestCase;

public class HTMLSelectTest extends TestCase {
	protected HTMLSelect sel;
	protected HTMLOption opt;
	protected String[] provinceNames = new String[] { "Ontario", "Quebec" };
	
	@Override
	protected void setUp() throws Exception {
		sel = new HTMLSelectImpl("province");
		for (String s : provinceNames) {
			opt = new HTMLOptionImpl(s);
			sel.addChild(opt);
		}
	}
	
	public void testOne() throws Exception {
		
		assertEquals(provinceNames[0], sel.getChildren().get(0).getName());
		sel.setValue(provinceNames[0]);
		sel.setValue(provinceNames[1]);
		assertEquals(provinceNames[1], sel.getValue());		
	}
	
	public void testBadValue() throws Exception {
		String badValue = "Ohio";
		
		// This currently should generate a warning.
		// Once we get javascript working well, it will throw an IAE.
		sel.setValue(badValue);
		assertEquals(badValue, sel.getValue());
	}
}
