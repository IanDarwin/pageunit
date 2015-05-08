package pageunit.html;

import java.util.ArrayList;
import java.util.List;

public class HTMLContainerBase extends HTMLComponentBase implements HTMLContainer {
	
	private List<HTMLComponent> children = new ArrayList<HTMLComponent>();

	public HTMLContainerBase(String name) {
		super(name);
	}

	public void addChild(HTMLComponent child) {
		children.add(child);
	}

	public List<HTMLComponent> getChildren() {
		return children;
	}
}
