package pageunit.javascript;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import junit.framework.TestCase;

/**
 * Simple test to ensure that JavaScript engine is working.
 * Or at least isn't failing, if present.
 * "Engine not found" should not be counted as a failure.
 * @author ian
 */
public class JSTest extends TestCase {
	public static void testJavaScript() throws Exception {
		try {
			final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
			String js = "js";
			ScriptEngine engine = scriptEngineManager
					.getEngineByName(js);
			if (engine == null) {
				System.out.println("Cannot load JavaScript ScriptEngine " + js);
				System.out.println("These engines found:");
				System.out.println("----------");
				scriptEngineManager.getEngineFactories().forEach(System.out::println);
				System.out.println("----------");
				return;
			}
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