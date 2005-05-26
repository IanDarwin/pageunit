package pageunit;

import java.io.File;

public class PageUnit {
	
	private static final String TEST_FILENAME_EXT = ".txt";

	public static final String TESTS_FILE = "tests" + TEST_FILENAME_EXT;
	
	private static TestRunner t = new TestRunner();
	
	private static int numberRun = 0;
	
	public static void main(String[] args) {
		
		try {
			if (args.length == 0) {
				t.run(TESTS_FILE);
			} else {
				for (int i = 0; i < args.length; i++) {
					final String fsObjectName = args[i];
					File f = new File(fsObjectName);
					processOne(f);
				}
			}
			System.out.printf("SUCCESS; %d run\n", numberRun);
		} catch (Exception e) {
			System.out.println("FAILED: caught Exception " + e);
		}
	}

	private static void processOne(File f) throws Exception {
		if (f.isFile()) {
			if (f.getName().endsWith(TEST_FILENAME_EXT)) {
				++numberRun;
				t.run(f.getAbsolutePath());
			} else
				System.err.printf("%s ignored, filename doesn't end in %s\n", f.getName(), TEST_FILENAME_EXT);
		} else if (f.isDirectory()) {
			loopThrough(f);
		} else {
			System.err.printf("%s is neither file nor directory\n", f.getName());
		}
	}
	
	private static void loopThrough(File dir) throws Exception {
		for (File f : dir.listFiles()) {
			processOne(f);
		}
	}	
}
