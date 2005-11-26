package pageunit.html;

import java.util.ArrayList;
import java.util.List;

public class HTMLContainerBase extends HTMLComponentBase implements
		HTMLContainer {
	
	private List<HTMLComponent> list = new ArrayList<HTMLComponent>();

	public HTMLContainerBase(String name) {
		super(name);
	}

	public void addChild(HTMLComponent child) {
		list.add(child);
	}

	public List<HTMLComponent> getChildren() {
		return list;
	}

}
