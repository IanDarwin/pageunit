package pageunit.html;

import java.util.List;

public interface HTMLContainer extends HTMLComponent {
	public void addChild(HTMLComponent child);
	public List<HTMLComponent> getChildren();
}
