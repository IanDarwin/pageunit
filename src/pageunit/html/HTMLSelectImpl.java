package pageunit.html;


public class HTMLSelectImpl extends HTMLInputImpl implements HTMLSelect {

	public HTMLSelectImpl(String name) {
		super(name, "Select");
	}
	
	/** set the value, which is equivalent to selecting a given included Option.
	 * At present, we don't have JavaScript working, so we can't be sure that all
	 * the correct values will have been loaded, so if you set a value that is not
	 * the name of an included Option, you get a warning.
	 * Once JavaScript support is working, we reserve the right :-) to change this
	 * to throw an IAE, causing the tests to fail, since then it really will be invalid
	 * (as it would represent something the user would not be able to do).
	 * @see pageunit.html.HTMLInput#setValue(java.lang.String)
	 * @throws IllegalArgumentException if the value isn't one of the nested OPTION elements,
	 * 	but only after we get JavaScript support working in the main part of the framework.
	 */
	@Override
	public void setValue(String value) {
		for (HTMLComponent c : getChildren()) {
			if (c instanceof HTMLOption) {
				HTMLOption op = (HTMLOption)c;
				// System.out.println("option: " + op);
				if (op.getName().equals(value)) {
					super.setValue(value);
					return;
				}
			}
		}
		System.err.printf("Warning: <select(%s)>.setValue: no <option> for %s%n", getName(), value);
		super.setValue(value);
	}
}
