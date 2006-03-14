package pageunit.html;


public class HTMLSelectImpl extends HTMLInputImpl implements HTMLSelect {

	public HTMLSelectImpl(String name) {
		super(name, "Select");
	}
	
	@Override
	public void setValue(String value) {
		for (HTMLComponent c : getChildren()) {
			if (c instanceof HTMLOption) {
				HTMLOption op = (HTMLOption)c;
				System.out.println("option: " + op);
				if (op.getName().equals(value)) {
					super.setValue(value);
					return;
				}
			}
		}
		System.err.println("Warn: no <option> for " + value);
		super.setValue(value);
	}
}
