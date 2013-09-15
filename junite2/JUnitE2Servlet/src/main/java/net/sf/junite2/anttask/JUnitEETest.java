package net.sf.junite2.anttask;

import java.io.File;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import net.sf.junite2.util.IteratorToEnumeration;

import org.apache.tools.ant.Project;

/**
 * This is a data type used by the JUnitEE task and represents a call to the JUnitEE servlet to run
 * one specific test suite or all available tests.
 * @author <a href="mailto:oliver@oross.net">Oliver Rossmueller</a>
 * @version $Revision: 1.1.1.1 $
 */
public class JUnitEETest{

	private String name;
	public String getName(){
		return name;
	}
	public void setName(String value){
		name = value;
	}
	
	private String resource;
	public String getResource(){
		return resource;
	}
	public void setResource(String value){
		resource = value;
	}
	
	private String errorProperty;
	public String getErrorproperty(){
		return errorProperty;
	}
	public void setErrorproperty(String value){
		errorProperty = value;
	}
	
	private String failureProperty;
	public String getFailureproperty(){
		return failureProperty;
	}
	public void setFailureproperty(String value){
		failureProperty = value;
	}
	
	private boolean haltOnError;
	public boolean getHaltonerror(){
		return haltOnError;
	}
	public void setHaltonerror(boolean value){
		haltOnError = value;
	}
	
	private boolean haltOnFailure;
	public boolean getHaltonfailure(){
		return haltOnFailure;
	}
	public void setHaltonfailure(boolean value){
		haltOnFailure = value;
	}
	
	private boolean runAll;
	public boolean getRunall(){
		return runAll;
	}
	public void setRunall(boolean value){
		runAll = value;
	}
	
	private String outfile;
	public File getOutfile(){
		if(outfile == null){
			if(toDir == null){
				return new File(getFileName());
			}
			return new File(toDir, getFileName());
		}
		if(toDir == null){
			return new File(outfile);
		}
		return new File(toDir, outfile);
	}
	public void setOutfile(String file){
		outfile = file;
	}
	
	private File toDir;
	public File getTodir(){
		return toDir;
	}
	public void setTodir(File toDir){
		this.toDir = toDir;
	}
	
	private List<FormatterElement> formatters = new LinkedList<FormatterElement>();
	public Enumeration<FormatterElement> getFormatters(){
		return new IteratorToEnumeration<FormatterElement>(formatters.iterator());
	}
	public void addFormatter(FormatterElement formatter){
		formatters.add(formatter);
	}
	
	private boolean filterTrace = true;
	public boolean getFiltertrace(){
		return filterTrace;
	}
	public void setFiltertrace(boolean filterTrace){
		this.filterTrace = filterTrace;
	}
	
	private String ifProperty;
	public String getIf(){
		return ifProperty;
	}
	public void setIf(String ifProperty){
		this.ifProperty = ifProperty;
	}

	private String unlessProperty;
	public String getUnless(){
		return unlessProperty;
	}
	public void setUnless(String unlessProperty){
		this.unlessProperty = unlessProperty;
	}

	private String getFileName(){
		if(runAll){
			return "TEST-ALL-";
		}
		return "TEST-";
	}
	
	public boolean shouldExecute(Project project){
		if(ifProperty != null){
			return project.getProperty(ifProperty) != null;
		}
		if(unlessProperty != null){
			return project.getProperty(unlessProperty) == null;
		}
		return true;
	}
}
