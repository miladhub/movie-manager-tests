package net.sf.junite2.runner;

import java.io.Serializable;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

/**
 * This class holds information about on test.
 * Depending upon
 * <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=2033150&group_id=15278&atid=365278"
 * >JUnit bug 2033150</a>, this class should be considered for implementing {@link Serializable}.
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class TestInfo{

	private String testClassName;
	private String testName;
	private long elapsedTime;
	private Throwable error;
	private Failure failure;

	/**
	 * public TestInfo(Test test) { if (test instanceof TestCase) { testClassName =
	 * test.getClass().getName(); testName = ((TestCase)test).getName(); } }
	 **/

	public TestInfo(Description testSuite, Description desc){
		if(desc.isTest()){
			if(testSuite == null){
				String testdisplayname = desc.getDisplayName();
				int pos = testdisplayname.indexOf("(");
				testName = testdisplayname.substring(0, pos);
				testClassName = testdisplayname.substring(pos + 1, testdisplayname.length() - pos - 1);
			}else{
				testName = desc.getDisplayName();
				testClassName = testSuite.getDisplayName();
			}

		}else{
			testName = desc.getDisplayName();
			testClassName = desc.getDisplayName();
		}
	}

	public long getElapsedTime(){
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime){
		this.elapsedTime = elapsedTime;
	}

	public String getTestClassName(){
		return testClassName;
	}

	public String getTestName(){
		return testName;
	}

	public boolean isTestCase(){
		return (testClassName != null) && (testName != null);
	}

	public void setError(Throwable t){
		error = t;
	}

	public void setFailure(Failure t){
		failure = t;
	}

	public boolean hasFailure(){
		return failure != null;
	}

	public boolean hasError(){
		return error != null;
	}

	public boolean successful(){
		return !(hasError() || hasFailure());
	}

	/**
	 * Answer the failures
	 * @return Throwable instances
	 */
	public Failure getFailure(){
		return failure;
	}

	/**
	 * Answer the errors
	 * @return Throwable instances
	 */
	public Throwable getError(){
		return error;
	}

	@Override
	public String toString(){
		return this.getTestName();
	}

}
