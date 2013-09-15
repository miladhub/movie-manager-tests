/*
 * $Id: JUnitEEResultFormatter.java,v 1.1.1.1 2007/07/13 23:45:13 martinfr62 Exp $ 2002 Oliver
 * Rossmueller
 */
package net.sf.junite2.anttask;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.w3c.dom.Node;

/**
 * This interface is the equivalent to <code>JUnitResultFormatter</code> provided by the JUnit task.
 * @version $Revision: 1.1.1.1 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public interface JUnitEEResultFormatter{

	/**
	 * Format the JUnitEE result
	 * @param testSuiteNode
	 */
	public void format(Node testSuiteNode) throws IOException;

	public void setOut(OutputStream out);

	public void setOutfile(File file);

	public void setExtension(String extension);

	public void setFilterTrace(boolean filter);

	void flush() throws IOException;
}
