package net.sf.junite2.runner;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * This is the JUnitEE test runner.
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @author <a href="http://www.ziesemer.com">Mark A. Ziesemer</a>
 */
public class TestRunner extends RunListener{

	private TestSuiteLoader loader;
	private TestRunnerResults listener;
	private boolean forkThread;
	private JUnitCore juc;

	/**
	 * Create a new instance and set the classloader to be used to load test classes.
	 * @param loader classloader to load test classes
	 * @param listener test listener to be notified
	 */
	public TestRunner(ClassLoader loader, TestRunnerResults listener, boolean forkThread){
		this.listener = listener;
		this.loader = new TestSuiteLoader(loader);
		this.forkThread = forkThread;
		juc = new JUnitCore();
		juc.addListener(new LoggerListener());
		juc.addListener(this);
	}

	@Override
	public void testFailure(Failure failure) throws Exception{
		listener.addFailure(failure.getDescription(), failure);
	}

	@Override
	public void testFinished(Description description) throws Exception{
		listener.endTest(description);
	}

	@Override
	public void testRunFinished(Result result) throws Exception{
		listener.finish();
	}

	@Override
	public void testRunStarted(Description description) throws Exception{
		listener.start(false);
		if(description.testCount() == 1){
			listener.start(true);
		}
		if(description.isSuite()){
			listener.testRunStarted(description);
		}
	}

	@Override
	public void testStarted(Description description) throws Exception{
		listener.startTest(description);
	}

	public void stop(){
		// notify the listener immediately so we can display this information
		listener.setStopped();
		System.out.println("sto");
	}

	/**
	 * Run all tests in the given test classes.
	 * @param testClassNames names of the test classes
	 */
	public void run(final String[] testClassNames){
		Runnable runnable = new Runnable(){
			public void run(){
				listener.start(false);
				for(String testClassName : testClassNames){
					try{
						Class<?> clazz = this.getClass().getClassLoader().loadClass(testClassName);
						Request req = Request.aClass(clazz);
						juc.run(req);
					}catch(ClassNotFoundException e1){
						e1.printStackTrace();
					}
				}
				try{
					Thread.sleep(10);
				}catch(InterruptedException e){
					// back to work
				}
				listener.finish();
			}
		};
		if(forkThread){
			Thread thread = new Thread(runnable, this.toString());
			thread.start();
		}else{
			runnable.run();
		}
	}

	public void run(final String testClassName, final String testName){
		final Filter filter = new Filter(){
			@Override
			public String describe(){
				return "Only Run " + testClassName + " test " + testName;
			}

			@Override
			public boolean shouldRun(Description description){
				if(description.getDisplayName().equals(testName))
					return true;
				return false;
			}
		};

		Runnable runnable = new Runnable(){

			public void run(){
				Request runRequest = null;

				try{
					Class<?> clazz = this.getClass().getClassLoader().loadClass(testClassName);
					Request req = Request.aClass(clazz);
					runRequest = req.filterWith(filter);
				}catch(ClassNotFoundException e1){
					e1.printStackTrace();
				}

				listener.start(false);

				if(runRequest != null){
					juc.run(runRequest);
				}
				try{
					Thread.sleep(10);
				}catch(InterruptedException e){
					// back to work
				}

				listener.finish();
			}
		};
		if(forkThread){
			Thread thread = new Thread(runnable, this.toString());
			thread.start();
		}else{
			runnable.run();
		}

	}

	public TestSuiteLoader getLoader(){
		return loader;
	}

}
