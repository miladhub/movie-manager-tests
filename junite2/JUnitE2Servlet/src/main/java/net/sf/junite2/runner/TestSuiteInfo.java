package net.sf.junite2.runner;

import java.util.LinkedList;
import java.util.List;

/**
 * This class holds information about on test.
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class TestSuiteInfo{
	private String testClassName;
	private List<TestInfo> tests = new LinkedList<TestInfo>();
	private List<TestInfo> errors = new LinkedList<TestInfo>();
	private List<TestInfo> failures = new LinkedList<TestInfo>();
	private long elapsedTime = 0L;

	public TestSuiteInfo(String className){
		testClassName = className;
	}

	public synchronized void add(TestInfo info){
		tests.add(info);
		if(info.hasError()){
			errors.add(info);
		}else if(info.hasFailure()){
			failures.add(info);
		}
		elapsedTime = elapsedTime + info.getElapsedTime();
	}

	public synchronized List<TestInfo> getTests(){
		return new LinkedList<TestInfo>(tests);
	}

	public synchronized boolean hasFailure(){
		return !failures.isEmpty();
	}

	public synchronized boolean hasError(){
		return !errors.isEmpty();
	}

	public synchronized boolean successful(){
		return !(hasError() || hasFailure());
	}

	public String getTestClassName(){
		return testClassName;
	}

	public long getElapsedTime(){
		return elapsedTime;
	}

	public synchronized List<TestInfo> getFailures(){
		return new LinkedList<TestInfo>(failures);
	}

	public synchronized List<TestInfo> getErrors(){
		return new LinkedList<TestInfo>(errors);
	}

}
