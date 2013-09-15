package net.sf.junite2.runner;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link RunListener} that logs to an SLF4J {@link Logger}.
 * <ul>
 * 	<li>{@link #testRunStarted(Description)} and {@link #testRunFinished(Result)}
 * 		call {@link Logger#info(String)}.</li>
 * 	<li>{@link #testStarted(Description)} and {@link #testFinished(Description)}
 * 		call {@link Logger#debug(String)}.</li>
 * 	<li>{@link #testIgnored(Description)} calls {@link Logger#warn(String)}.</li>
 * 	<li>{@link #testFailure(Failure)} calls {@link Logger#error(String, Throwable)}.</li>
 * </ul>
 * <a href="http://www.ziesemer.com">Mark A. Ziesemer</a>
 */
public class LoggerListener extends RunListener{
	
	protected final Logger logger;
	
	/**
	 * Logs to a default {@link Logger} named the same as this class.
	 */
	public LoggerListener(){
		this(LoggerFactory.getLogger(LoggerListener.class));
	}
	
	/**
	 * Logs to the specified {@link Logger}.
	 */
	public LoggerListener(Logger logger){
		if(logger == null){
			throw new IllegalArgumentException("Logger must not be null.");
		}
		this.logger = logger;
	}
	
	@Override
	public void testRunStarted(Description description) throws Exception{
		logger.info("testRunStarted: " + description.getDisplayName());
	}
	
	@Override
	public void testRunFinished(Result result) throws Exception{
		if(logger.isInfoEnabled()){
			logger.info("testRunFinished: ["
				+ "runCount: " + result.getRunCount() + ", "
				+ "ignoreCount: " + result.getIgnoreCount() + ", "
				+ "failureCount: " + result.getFailureCount() + ", "
				+ "runTime: " + result.getRunTime() + "]");
		}
	}
	
	@Override
	public void testStarted(Description description) throws Exception{
		logger.debug("testStarted: " + description);
	}
	
	@Override
	public void testFinished(Description description) throws Exception{
		logger.debug("testFinished: " + description);
	}
	
	@Override
	public void testIgnored(Description description) throws Exception{
		logger.warn("testIgnored: " + description.getDisplayName());
	}
	
	@Override
	public void testFailure(Failure failure) throws Exception{
		if(logger.isErrorEnabled()){
			logger.error("testFailure: " + failure.getTestHeader() + ", " + failure.getDescription(),
				failure.getException());
		}
	}
}
