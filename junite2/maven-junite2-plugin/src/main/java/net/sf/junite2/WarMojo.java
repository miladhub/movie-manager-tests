package net.sf.junite2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.sf.junite2.anttask.JUnitEEWarTask;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.FileScanner;

/**
 * Decorates the current project with a basic webapp structure
 * providing the necessary elements for the building of a webapp
 * usable with JUnitEE 2.0.
 * 
 * @goal war
 * @phase test
 */
public class WarMojo extends AbstractMojo {
    
    /**
     * Base directory where to find test-cases.
     * @parameter expression="${project.build.sourceDirectory}"
     */
    public String sourceDir;
    
    /**
     * Target build directory.
     * @parameter expression="${project.build.directory}"
     */
    public String buildDir;
    
    /**
     * WAR name.
     * @parameter expression="${project.build.finalName}"
     */
    public String finalName;
    
    /**
     * Scanner to find test-cases.
     */
    protected FileScanner fileScanner = new DirectoryScanner();
        
    /**
     * Test-cases list.
     */
    protected List<String> testCases = new ArrayList<String>();
    
	/**
	 * The Maven Project Object.
	 * @parameter expression="${project}"
	 */
	protected MavenProject project;
    
    public final static String WAR_EXTENSION = ".war";
    
    public final static String TXT_FILE_NAME = "testCase.txt";
    
    public static final String URLPATTERN_TOKEN = "@urlPattern@";
    
    public static final String URLPATTERN_REPLACEMENT = "TestServlet";
    
    // TO-DO: enable the specification of more patterns
    public static final String INCLUDE_PATTERN = "**/*Test.java";

    public static final String MAIN = "main";

    public static final String WEBAPP = "webapp";

    public static final String SRC = "src";
    
    public static final String WEB_XML = "web.xml";

    public static final String WEB_INF = "WEB-INF";
        
    public WarMojo() {}
    
    /**
     * This method:
     * <ol>
     * <li>Creates <code>${buildDir}/${finalName}</code> directory,</li>
     * <li>Copies <code>index.html</code> and <code>WEB-INF/testCases.txt</code> files into it,</li>
     * <li>Copies file <code>web.xml</code> into <code>${buildDir}/WEB-INF</code> folder.</li> 
     * </ol>
     * Note that this file is referenciable from tag <code>webXml</code> within <code>war</code>
     * plugin configuration - e.g.
     * <pre>
     * &ltplugin&gt
     *    &ltgroupId&gtorg.apache.maven.plugins&lt/groupId&gt
     *    &ltartifactId&gtmaven-war-plugin&lt/artifactId&gt
     *    &ltversion&gt2.0&lt/version&gt
     *    &ltconfiguration&gt
     *       &ltwebXml&gttarget/web.xml&lt/webXml&gt
     *    &lt/configuration&gt
     * &lt/plugin&gt
     * <pre>
     */
    public void execute() throws MojoExecutionException {
    	if (!"war".equals(project.getPackaging())) {
    		getLog().info("Packaging is not 'war', skipping.");
    		return;
    	}

        if (!new File(sourceDir).exists()) {
            getLog().info("No source directory found to scan for tests.");
            return;
        }
        createDir(buildDir);
        createDir(buildDir + File.separator + finalName);
        createDir(buildDir + File.separator + finalName + File.separator + WEB_INF);
        configureScanner();
        configureTestCases();
        if (testCases.size() == 0) {
            getLog().info("No test-cases found (include pattern = " + INCLUDE_PATTERN + ").");
        }
        createIndexHtml(buildDir + File.separator + finalName);
        createTestCaseFile(buildDir + File.separator + finalName + File.separator + WEB_INF);        
        createWebXml(buildDir);
    }
    
    /**
     * Cretes a directory.
     */
    protected void createDir(String target) {
        try {
            new File(target).mkdir();
        } catch (Exception e) {
            throw new BuildException("Error creating directories", e);
        }
    }
    
    /**
     * Configures the scanner.
     */
    protected void configureScanner() {
        getLog().info("Setting test-case scanner source dir: " + sourceDir + ".");
        fileScanner.setBasedir(sourceDir);
        fileScanner.setIncludes(new String[]{INCLUDE_PATTERN});
    }
    
    /**
     * Creates file web.xml
     * @throws BuildException in case of errors
     */
    protected void createWebXml(String target) throws BuildException {
        try {
            File file = new File(target + File.separator + WEB_XML);

            PrintWriter pw = new PrintWriter(new FileOutputStream(file));
            InputStream in = getClass().getClassLoader().getResourceAsStream(WEB_XML);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;

            while((line = reader.readLine()) != null){
                pw.println(line);
            }
            
            reader.close();
            pw.close();
        } catch (java.io.IOException ex) {
            throw new BuildException("Error creating web.xml", ex);
        }
    }
    
    /**
     * Creates file index.html
     * @throws BuildException in case of errors
     */
    protected void createIndexHtml(String target) throws BuildException {
        try {
            File file = new File(target + File.separator + "index.html");

            PrintWriter pw = new PrintWriter(new FileOutputStream(file));
            InputStream in = getClass().getClassLoader().getResourceAsStream("index.html");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;

            StringBuffer bufferList = new StringBuffer();
            for(String tc : testCases){
                    bufferList
                        .append("        <tr><td class=\"cell\"><input type=\"checkbox\" name=\"suite\" value=\"");
                    bufferList.append(tc).append("\">&nbsp;&nbsp;").append(tc).append(
                        "</input></td></tr>\n");
            }

            int index;

            while ((line = reader.readLine()) != null) {
                if(line.startsWith("<!-- ### -->")){
                    pw.print(bufferList.toString());
                } else if ((index = line.indexOf(URLPATTERN_TOKEN)) != -1){
                    pw.print(line.substring(0, index));
                    pw.print(URLPATTERN_REPLACEMENT);
                    pw.println(line.substring(index + URLPATTERN_TOKEN.length()));
                } else {
                    pw.println(line);
                }
            }
            reader.close();
            pw.close();
        } catch (java.io.IOException ex){
            throw new BuildException("Error creating index.html", ex);
        }
    }
    
    /**
     * Creates file containing test-cases names.
     * @throws BuildException in case of errors
     */
    protected void createTestCaseFile(String target) throws BuildException {
        try {
            File file = new File(target + File.separator + TXT_FILE_NAME);
            
            file.createNewFile();
            PrintWriter pw = new PrintWriter(new FileOutputStream(file));
            pw.println("# JunitServletRunner");

            for(String className : testCases){
                pw.println(className);
            }
            pw.close();            
        } catch(IOException e) {
            throw new BuildException("Error creating test case list", e);
        }
    }
    
    /**
     * Configures the test-case list.
     */
    protected void configureTestCases() {
        fileScanner.scan();
        getLog().info("Adding test-cases ...");
        for (String f : fileScanner.getIncludedFiles()) {
            String testClass = JUnitEEWarTask.getTestCaseClassName(f);
            getLog().info("... adding test-case " + testClass);
            testCases.add(testClass);
        }
        getLog().info("Added " + testCases.size() + " test-cases.");
    }
   
}
