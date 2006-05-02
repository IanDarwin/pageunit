package pageunit;

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
import javax.swing.JTextField;

import junit.framework.TestCase;
import junit.framework.TestResult;

import com.darwinsys.swingui.UtilGUI;

/**
 * A static GUI to run one test file.
 */
public class PageUnitGUI extends PageUnit {

	final static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
	
	final static Preferences p = Preferences.userNodeForPackage(PageUnitGUI.class);
	
	/**
	 * Main method; ignores arguments.
	 */
	public static void main(String[] args) {
		final JFrame jf = new JFrame("PageUnit Runner");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final Container cp = jf.getContentPane();
		cp.setLayout(new FlowLayout());
		cp.add(new JLabel("PageUnit Test File:"));
		final JTextField tf = new JTextField(20);
		cp.add(tf);

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
				tf.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}		
		});

		final JButton testButton = new JButton("Test");
		cp.add(testButton);
		testButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				String fileName = tf.getText();
				if (fileName == null || fileName.length() == 0) {
					return;
				}
				File f = new File(fileName);
				if (!f.canRead()) {
					error(jf, "Can't read file " + f);
				}
				try {
					TestResult results = new TestResult();
					TestCase t = new ScriptTestCase(f.getAbsolutePath());
					int max = results.runCount();
					System.out.printf("Out of %d runs%n", max);
					
					t.run(results);
				} catch (Exception e) {
					error(jf, e.toString());
					e.printStackTrace();
				}
			}			
		});		
		
		jf.pack();
		UtilGUI.monitorWindowPosition(jf, p);
		jf.setVisible(true);
	}
	
	static void error(final JFrame tf, String mesg) {
		JOptionPane.showMessageDialog(tf, mesg, "Oops", JOptionPane.ERROR_MESSAGE);
	}

}
