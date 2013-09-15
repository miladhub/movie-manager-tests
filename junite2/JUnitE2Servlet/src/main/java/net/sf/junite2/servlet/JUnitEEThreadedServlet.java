/*
 * $Id: JUnitEEThreadedServlet.java,v 1.1.1.1 2007/07/13 23:45:17 martinfr62 Exp $ 2002 Oliver
 * Rossmueller
 */
package net.sf.junite2.servlet;

/**
 * This servlet changes the behaviour of {@link JUnitEEServlet} in the way that by default a thread
 * is forked if more than one test suite is to be exectued.
 * @version $Revision: 1.1.1.1 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public class JUnitEEThreadedServlet extends JUnitEEServlet{

	private static final long serialVersionUID = -5569574940877282710L;

	@Override
	protected boolean getDefaultThreadMode(){
		return true;
	}
}
