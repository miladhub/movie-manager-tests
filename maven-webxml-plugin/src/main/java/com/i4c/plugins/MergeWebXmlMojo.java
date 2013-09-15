package com.i4c.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.FileScanner;
import org.jdom.JDOMException;

/**
 * Merges the provided web.xml files into a single one.
 *
 * @goal merge
 * @phase process-resources
 */
public class MergeWebXmlMojo extends AbstractMojo {

    /**
     * Base directory where to find web.xml files.
     * @parameter expression="${webxml.merge.sourceDir}" default-value="${project.build.directory}/dependency"
     */
    public String sourceDir;

    /**
     * Target directory where to write the merged web.xml.
     * @parameter expression="${webxml.merge.buildDir}" default-value="${project.build.directory}/mergedWebXml"
     */
    public String buildDir;

	/**
	 * The Maven Project Object.
	 * @parameter expression="${project}"
	 */
	protected MavenProject project;

    /**
     * Scanner to find web.xml files.
     */
    protected FileScanner fileScanner = new DirectoryScanner();

    /**
     * List containing the found web.xml files.
     */
    protected List<String> webXmlFiles = new ArrayList<String>();

    private static final String WEB_XML = "web.xml";

    public static final String INCLUDE_PATTERN = "**/" + WEB_XML;

    public MergeWebXmlMojo() {}

    public void execute() throws MojoExecutionException {
    	if (!"war".equals(project.getPackaging())) {
    		getLog().info("Packaging is not 'war', skipping.");
    		return;
    	}

        File buildDirFile = new File(buildDir);

        if (!buildDirFile.exists()) {
        	if (!buildDirFile.mkdirs()) {
        		throw new MojoExecutionException("Cannot create directory " + buildDir);
        	}
        }

        configureScanner();
        configureWebXmlFiles();
        if (webXmlFiles.size() == 0) {
        	throw new MojoExecutionException("No test-cases found (include pattern = " + INCLUDE_PATTERN + ").");
        }

        FileOutputStream webXmlOs;
        File webXml = new File(buildDirFile, WEB_XML);
		try {
			webXmlOs = new FileOutputStream(webXml);
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException("Can't write web.xml", e);
		}

		try {
			createWebXml(webXmlOs);
			getLog().info("Merged files into " + webXml.getAbsolutePath());
		} catch (IOException e) {
			throw new MojoExecutionException("I/O error while merging web.xml", e);
		} catch (JDOMException e) {
			throw new MojoExecutionException("Can't merge web.xml", e);
		}
    }

    /**
     * Configures the scanner.
     */
    protected void configureScanner() {
        getLog().info("Setting web.xml scanner source dir: " + sourceDir);
        fileScanner.setBasedir(sourceDir);
        fileScanner.setIncludes(new String[]{INCLUDE_PATTERN});
    }

    /**
     * Creates file web.xml.
     * @throws IOException in case of I/O errors
     * @throws JDOMException in case of merge errors
     */
    private void createWebXml(FileOutputStream webXmlOs) throws IOException, JDOMException {
    	PrintWriter pw = new PrintWriter(webXmlOs);
    	try {
            String first = webXmlFiles.remove(0);
            WebXml target = WebXmlParser.parse(new FileInputStream(new File(first)));
            for (String webXml : webXmlFiles) {
            	target = WebXmlParser.merge(target, WebXmlParser.parse(new FileInputStream(new File(webXml))));
            }
            pw.append(target.toString());
            pw.flush();
        } finally {
        	webXmlOs.close();
        	pw.close();
        }
	}

    /**
     * Configures the web.xml file list.
     */
    protected void configureWebXmlFiles() {
        fileScanner.scan();
        getLog().info("Adding web.xml files ...");
        for (String webXml : fileScanner.getIncludedFiles()) {
            getLog().info("... adding file " + webXml);
            webXmlFiles.add(sourceDir + File.separator + webXml);
        }
        getLog().info("Added " + webXmlFiles.size() + " web.xml files.");
    }

//    /**
//     * Creates file web.xml.
//     * @throws JDOMException in case of parsing errors
//     * @throws IOException in case of I/O errors
//     */
//    protected void createWebXmlWithCargo(OutputStream webXmlOs) throws IOException, JDOMException {
//        try {
//            String first = webXmlFiles.remove(0);
//            WebXml targetWebXml = WebXmlIo.parseWebXmlFromFile(new File(first), null);
//            getLog().info("First: " + targetWebXml);
//            WebXmlMerger merger = new WebXmlMerger(targetWebXml);
//            merger.setLogger(new MavenLogger(getLog()));
//            for (String webXml : webXmlFiles) {
//				merger.merge(WebXmlIo.parseWebXmlFromFile(new File(webXml), null));
//            }
//            WebXmlIo.writeDescriptor(targetWebXml, webXmlOs, null, false);
//        } finally {
//        	webXmlOs.close();
//        }
//    }
//    public final class MavenLogger extends AbstractLogger {
//    	private Log log;
//    	public MavenLogger(Log log) {this.log = log;}
//		@Override
//		protected void doLog(LogLevel level, String message, String category) {
//			String content = category + ": " + message;
//			if (LogLevel.INFO.equals(level)) {
//				log.info(content);
//			} else if (LogLevel.DEBUG.equals(level)) {
//				log.debug(content);
//			} if (LogLevel.WARN.equals(level)) {
//				log.warn(content);
//			}
//		}
//    }

	@Override
	public String toString() {
		return "MergeWebXmlMojo [buildDir=" + buildDir + ", sourceDir="
				+ sourceDir + ", webXmlFiles=" + webXmlFiles + "]";
	}

}
