/*
 * $Id: AbstractResultFormatter.java,v 1.1.1.1 2007/07/13 23:45:13 martinfr62 Exp $ 2002 Oliver
 * Rossmueller
 */
package net.sf.junite2.anttask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @version $Revision: 1.1.1.1 $
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 */
public abstract class AbstractResultFormatter implements JUnitEEResultFormatter{

	private OutputStream out;
	private File outfile;
	private boolean filterTrace;
	private String extension;

	public boolean isFilterTrace(){
		return filterTrace;
	}

	public void setFilterTrace(boolean filterTrace){
		this.filterTrace = filterTrace;
	}

	public void setOut(OutputStream out){
		this.out = out;
	}

	public void setOutfile(File file){
		outfile = file;
	}

	public void setExtension(String extension){
		this.extension = extension;
	}

	public OutputStream getOutput(String testName) throws FileNotFoundException{
		if(out != null){
			return out;
		}
		String fileName = outfile.getAbsolutePath() + testName + extension;
		out = new FileOutputStream(fileName);
		return out;
	}

	public void flush() throws IOException{
		out.flush();
		if(out != null && out != System.out && out != System.err){
			try{
				out.close();
			}catch(IOException e){ /* ignore */
			}
			out = null;
		}
	}

	protected String getTestName(Node testSuiteNode){
		NamedNodeMap attributes = testSuiteNode.getAttributes();
		String name = attributes.getNamedItem("name").getNodeValue();
		String pkg = attributes.getNamedItem("package").getNodeValue();

		if(pkg != null && pkg.length() != 0){
			return pkg + "." + name;
		}
		return name;
	}

}
