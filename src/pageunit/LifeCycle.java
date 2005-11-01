package pageunit;

public interface LifeCycle {
	public void init() throws java.io.IOException;
    public void destroy() throws java.io.IOException;
}
