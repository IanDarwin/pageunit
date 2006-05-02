package pageunit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

import com.darwinsys.swingui.UtilGUI;

/**
 * A static GUI to run one test file.
 */
public class PageUnitGUI extends PageUnit {

	final JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
	
	final Preferences p = Preferences.userNodeForPackage(PageUnitGUI.class);
	
	final JProgressBar bar = new JProgressBar();
	
	/**
	 * Main method; ignores arguments.
	 */
	public static void main(String[] args) {
		new PageUnitGUI();
	}
	
	PageUnitGUI() {
		final JFrame jf = new JFrame("PageUnit Runner");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final Container cp = new JPanel();
		jf.add(cp, BorderLayout.CENTER);
		
		cp.setLayout(new FlowLayout());
		cp.add(new JLabel("PageUnit Test File:"));
		final JTextField tf = new JTextField(20);
		cp.add(tf);
		
		String rememberedCurrentDirectory = p.get("directory", null);
		if (rememberedCurrentDirectory != null) {
			fileChooser.setCurrentDirectory(new File(rememberedCurrentDirectory));
		}

		final JButton browseButton = new JButton("...");
		cp.add(browseButton);
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ret = fileChooser.showOpenDialog(tf);
				// blocking wait
				// XXX should use invokeLater()?
				if (ret != JFileChooser.APPROVE_OPTION) {
					return;
				}
				p.put("directory", fileChooser.getCurrentDirectory().getAbsolutePath());
				tf.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}		
		});

		final JButton testButton = new JButton("Test");
		cp.add(testButton);
		testButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				final String fileName = tf.getText();
				if (fileName == null || fileName.length() == 0) {
					return;
				}
				final File f = new File(fileName);
				if (!f.canRead()) {
					error(jf, "Can't read file " + f);
				}
				
				// Run rest of this under Swing's control, so we don't block the EventDispatch thread...
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
				
						try {
							TestResult results = new TestResult() {
								@Override
								public void startTest(Test test) {
									crank();
								}
								@Override
								public synchronized void addError(Test test, Throwable t) {
									seeRed();
								}
								@Override
								public synchronized void addFailure(Test test, AssertionFailedError t) {
									seeRed();
								}
							};
							TestCase t = new ScriptTestCase(f.getAbsolutePath());
							int max = t.countTestCases();
							System.out.printf("Starting %d tests%n", max);
							bar.setMaximum(max);
							t.run(results);
						} catch (Exception e) {
							error(jf, e.toString());
						}
					}}
				);
			}			
		});	
		
		jf.add(bar, BorderLayout.SOUTH);
		seeGreen();
		
		jf.pack();
		UtilGUI.monitorWindowPosition(jf, p);
		jf.setVisible(true);
	}
	
	/**
	 * Set the bar to green, used only at the beginning
	 */
	void seeGreen() {
		bar.setForeground(Color.GREEN);
	}
	/**
	 * Set the bar to red, used when a test fails or errors.
	 */
	void seeRed() {
		bar.setForeground(Color.RED);
	}
	
	void crank() {
		int x = bar.getValue();
		bar.setValue(++x);
		bar.repaint();
		boolean debug = false;
		if (debug) {
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			// canthappen
		}
		}
	}
	
	static void error(final JFrame tf, String mesg) {
		JOptionPane.showMessageDialog(tf, mesg, "Oops", JOptionPane.ERROR_MESSAGE);
	}

}
