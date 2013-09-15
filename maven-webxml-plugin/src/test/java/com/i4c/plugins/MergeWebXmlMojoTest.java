package com.i4c.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.project.MavenProject;

import com.i4c.plugins.WebXml.ContextParam;
import com.i4c.plugins.WebXml.LoginConfig;
import com.i4c.plugins.WebXml.Servlet;
import com.i4c.plugins.WebXml.ServletMapping;
import com.i4c.plugins.WebXml.WelcomeFile;

public class MergeWebXmlMojoTest extends AbstractMojoTestCase {

	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(MergeWebXmlMojoTest.class);
	private MergeWebXmlMojo mojo;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		File myPom = new File(getBasedir(), "target/test-classes/mypom.xml");
		mojo = (MergeWebXmlMojo) lookupMojo("merge", myPom);
		MavenProject project = new MavenProjectStub();
		project.setPackaging("war");
		mojo.project = project;
	}

	public void testVariables() throws Exception {
		assertNotNull(mojo);
		assertEquals(mojo.buildDir, "target/mergedWebXml");
		assertEquals(mojo.sourceDir, "target/test-classes");
	}

	public void testExecution() throws Exception {
		File webXmlFile = new File(getBasedir(), "target/mergedWebXml/web.xml");
		if (webXmlFile.exists()) {
			if (!webXmlFile.delete()) {
				fail("Cannot delete web.xml");
			}
		}
		mojo.execute();
		assertTrue(webXmlFile.exists());
		InputStream in = new FileInputStream(webXmlFile);
		try {
			WebXml webXml = WebXmlParser.parse(in);

			assertEquals("Movie Manager Core Module", webXml.getDisplayName().getName().trim());

			assertEquals(1, webXml.getContextParams().size());
			ContextParam contextParam = webXml.getContextParams().get(0);
			assertEquals("javax.faces.CONFIG_FILES", contextParam.getName().trim());
			assertEquals("/WEB-INF/faces-config-core.xml,/WEB-INF/faces-config-details.xml", contextParam.getValue().trim());

			Servlet myServlet = webXml.getServlets().get(0);
			assertEquals("MyServlet", myServlet.getName());
			assertEquals("com.i4c.servlets.MyServlet", myServlet.getClazz());

			ServletMapping myServletMapping = webXml.getServletMappings().get(0);
			assertEquals("MyServlet", myServletMapping.getName());
			assertEquals("/myservlet/*", myServletMapping.getPattern());

			Servlet facesServlet = webXml.getServlets().get(1);
			assertEquals("Faces Servlet", facesServlet.getName());
			assertEquals("javax.faces.webapp.FacesServlet", facesServlet.getClazz());

			ServletMapping facesServletMapping = webXml.getServletMappings().get(1);
			assertEquals("Faces Servlet", facesServletMapping.getName());
			assertEquals("/faces/*", facesServletMapping.getPattern());

			List<WelcomeFile> welcomeFiles = webXml.getWelcomeFileList().getFiles();
			WelcomeFile welcomeFile = welcomeFiles.get(0);
			assertEquals("faces/search.jsp", welcomeFile.getFile().trim());

			LoginConfig authMethod = webXml.getLoginConfig();
			assertEquals("BASIC", authMethod.getMethod().trim());
		} finally {
			if (in != null) in.close();
		}

	}

}
