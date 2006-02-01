package pageunit;

import java.io.File;
import java.util.Arrays;

/** Main program to run test files under PageUnit. NOT WORKING.
 * @author ian
 */
public class PageUnit {
	
	private static final String TEST_FILENAME_EXT = ".txt";

	public static final String TESTS_FILE = "tests" + TEST_FILENAME_EXT;
	
	private static ScriptTestCase t;// = new TestRunner();
	
	private static int numFilesRun = 0;
		
	public static void main(String[] args) {
		
		try {
			if (args.length == 0) {
				t = new ScriptTestCase(TESTS_FILE);
			} else {
				for (int i = 0; i < args.length; i++) {
					final String fsObjectName = args[i];
					File f = new File(fsObjectName);
					processOne(f);
				}
			}
			System.out.printf("%d files run\n", numFilesRun);
		} catch (Exception e) {
			System.out.printf("FAILED: caught Exception %s after %d runs\n", e, numFilesRun);
			e.printStackTrace();
		}
	}

	/** Process one File; if it's a directory, recurse,
	 * if it's a file, ending in .txt, run it as a test file.
	 * @param f The File object to process.
	 * @throws Exception
	 */
	private static void processOne(final File f) throws Exception {
		if (f.isFile()) {
			if (f.getName().endsWith(TEST_FILENAME_EXT)) {
				++numFilesRun;
				t = new ScriptTestCase(f.getAbsolutePath());
				// r = t.run(f.getAbsolutePath()).add(r);
			} else {
				//System.err.printf("%s ignored, filename doesn't end in %s\n", f.getName(), TEST_FILENAME_EXT);
			}
		} else if (f.isDirectory()) {
			loopThrough(f);
		} else {
			System.err.printf("%s is neither file nor directory\n", f.getName());
		}
	}
	
	/** Process one directory, recursing by calling back to processOne,
	 * and processing files.
	 */
	private static void loopThrough(final File dir) throws Exception {
		assert dir.isDirectory() : "Logic error: not a directory";
		File[] files = dir.listFiles();
		Arrays.sort(files);
		for (File f : files) {
			processOne(f);
		}
	}	
}
