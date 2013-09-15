package net.sf.junite2.runner;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

/**
 * Depending upon
 * <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=2033150&group_id=15278&atid=365278"
 * >JUnit bug 2033150</a>, this class should be considered for implementing {@link Serializable}.
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class TestRunnerResults{

	private ThreadLocal<TestInfo> currentInfo = new ThreadLocal<TestInfo>();
	private long timestamp;
	private List<TestSuiteInfo> suiteInfo = new LinkedList<TestSuiteInfo>();
	private Map<String, TestSuiteInfo> suites = new HashMap<String, TestSuiteInfo>();
	private boolean failure = false;
	private boolean singleTest = false;
	private List<String> errorMessages = new LinkedList<String>();
	private boolean filterTrace = true;
	private boolean finished = false;
	private boolean stopped = false;
	private Description testSuite = null;

	public void start(boolean singleTest){
		setSingleTest(singleTest);
	}

	public synchronized void setStopped(){
		stopped = true;
	}

	public synchronized boolean isStopped(){
		return stopped;
	}

	public synchronized void finish(){
		finished = true;
	}

	public synchronized boolean isFinished(){
		return finished;
	}

	public synchronized void addError(Description test, Throwable t){
		boolean preRunError = !existsCurrentInfo(test);

		getCurrentInfo().setError(t);
		setFailure(true);

		if(preRunError){
			endTest(test);
		}
	}

	public synchronized void addFailure(Description test, Failure t){
		boolean preRunError = !existsCurrentInfo(test);

		getCurrentInfo().setFailure(t);
		setFailure(true);

		if(preRunError){
			endTest(test);
		}
	}

	public void testRunStarted(Description description) throws Exception{
		testSuite = description;
	}

	public synchronized void endTest(Description test){
		long elapsedTime = System.currentTimeMillis() - getTimestamp();

		getCurrentInfo().setElapsedTime(elapsedTime);
		addToSuite(getCurrentInfo());
		setCurrentInfo(null);
	}

	public synchronized void startTest(Description test){
		setCurrentInfo(new TestInfo(testSuite, test));
		setTimestamp(System.currentTimeMillis());
	}

	public synchronized void runFailed(String message){
		errorMessages.add(message);
	}

	public synchronized long getTimestamp(){
		return timestamp;
	}

	public synchronized void setTimestamp(long timestamp){
		this.timestamp = timestamp;
	}

	public synchronized List<TestSuiteInfo> getSuiteInfo(){
		return new LinkedList<TestSuiteInfo>(suiteInfo);
	}

	public synchronized void setSuiteInfo(List<TestSuiteInfo> suiteInfo){
		this.suiteInfo = new LinkedList<TestSuiteInfo>(suiteInfo);
	}

	public synchronized TestInfo getCurrentInfo(){
		return currentInfo.get();
	}

	public synchronized void setCurrentInfo(TestInfo currentInfo){
		this.currentInfo.set(currentInfo);
	}

	public synchronized boolean isFailure(){
		return failure;
	}

	public synchronized void setFailure(boolean failure){
		this.failure = failure;
	}

	public synchronized boolean isFilterTrace(){
		return filterTrace;
	}

	public synchronized void setFilterTrace(boolean filterTrace){
		this.filterTrace = filterTrace;
	}

	public synchronized boolean isSingleTest(){
		return singleTest;
	}

	public synchronized void setSingleTest(boolean singleTest){
		this.singleTest = singleTest;
	}

	public synchronized List<String> getErrorMessages(){
		return errorMessages;
	}

	private boolean existsCurrentInfo(Description test){
		if(getCurrentInfo() == null){
			setTimestamp(System.currentTimeMillis());
			setCurrentInfo(new TestInfo(testSuite, test));
			return false;
		}
		return true;
	}

	protected synchronized void addToSuite(TestInfo info){
		String className = info.getTestClassName();
		TestSuiteInfo suite = suites.get(className);

		if(suite == null){
			suite = new TestSuiteInfo(className);
			suites.put(className, suite);
			suiteInfo.add(suite);
		}
		suite.add(info);
	}
}
