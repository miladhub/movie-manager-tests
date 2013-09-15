package net.sf.junite2.runner;

/**
 * The classloader used to load test classes is set in the constructor.
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class TestSuiteLoader{

	private ClassLoader loader;

	/**
	 * Create a new instance and set the classloader to be used to load test classes.
	 * @param loader classloader to load test classes
	 */
	public TestSuiteLoader(ClassLoader loader){
		this.loader = loader;
	}

	public Class<?> load(String className) throws ClassNotFoundException{
		return loader.loadClass(className);
	}

	public <T> Class<T> reload(Class<T> aClass) throws ClassNotFoundException{
		return aClass;
	}
}
