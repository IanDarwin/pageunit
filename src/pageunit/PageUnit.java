package pageunit;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.framework.TestResult;

/** Main program to run test files under PageUnit.
 * @author ian
 */
public class PageUnit {
	private static Logger logger = Logger.getLogger(PageUnit.class);
	
	private static final String TEST_FILENAME_EXT = ".txt";

	public static final String TESTS_FILE = "tests" + TEST_FILENAME_EXT;
	
	private static int numFilesRun = 0;
	private static boolean debug;
	
	private static class PageUnitTestResult extends TestResult implements Cloneable {
		@Override
		public String toString() {
			return String.format("%d Errors, %d Failures", errorCount(), failureCount());
		}
		
		@Override
		public Object clone() {
			try {
			return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException("Trivial class threw CloneNotSupported exception, doh!");
			}
		}
	};
	private static PageUnitTestResult results;
	
	public static void init() {
		results = new PageUnitTestResult();
	}

	public static void main(final String[] args) {
		
		try {
			if (args.length == 0) {
				processOne(new File(TESTS_FILE));
			} else {
				for (int i = 0; i < args.length; i++) {
					final String fsObjectName = args[i];
					File f = new File(fsObjectName);
					processOne(f);
				}
			}
			System.out.printf("%d %s run, results: %s\n", numFilesRun,
					numFilesRun > 1 ? "files" : "file", results);
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
	public static void processOne(final File f) throws Exception {
		logger.info(String.format("pageunit: processOne(%s)", f));
		if (f.isFile()) {
			if (f.getName().endsWith(TEST_FILENAME_EXT)) {
				++numFilesRun;
				TestCase t = new ScriptTestCase(f.getAbsolutePath());
				t.run(results);
			} else {
				if (debug) {
					System.err.printf("%s ignored, filename doesn't end in %s\n", f.getName(), TEST_FILENAME_EXT);
				}
			}
		} else if (f.isDirectory()) {
			loopThrough(f);
		} else {
			System.err.printf("%s is neither file nor directory\n", f.getAbsolutePath());
		}
	}
	
	/** Process one directory, recursing by calling back to processOne,
	 * and processing files.
	 */
	private static void loopThrough(final File dir) throws Exception {
		assert dir.isDirectory() : "Logic error: not a directory";
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name != null || name.endsWith(".txt");
			}
			
		});
		Arrays.sort(files);
		for (File f : files) {
			processOne(f);
		}
	}

	/**
	 * Returns true if any tests have reported errors or failures.
	 * @return
	 */
	public static boolean isFailed() {
		return results.errorCount() > 0 || results.failureCount() > 0;
	}
	
	/**
	 * Get a copy of the JUnit TestResults for this run.
	 */
	public static TestResult getTestResults() {
		return (TestResult) results.clone();
	}	
}
