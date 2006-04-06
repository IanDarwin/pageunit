package pageunit.javascript;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import junit.framework.TestCase;

/**
 * Simple test to ensure that JavaScript engine is working
 * 
 * @author ian
 */
public class JSTest extends TestCase {
	public static void testJavaScript() throws Exception {
		try {
			ScriptEngine engine = new ScriptEngineManager()
					.getEngineByName("js");
			System.out.println(engine.getClass());
			Object ret;
			ret = engine.eval("print('Hello World!')");
			
			System.out.println("Results: " + ret);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}