package com.i4c.plugins;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.i4c.plugins.WebXml.*;

public class WebXmlParserTest {
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(WebXmlParserTest.class);
	@Test
	public void testWebXmlFaces() throws Exception {
		InputStream in = getClass().getClassLoader().getResourceAsStream("webapp1/web.xml");
		try {
			WebXml webXml = WebXmlParser.parse(in);
			assertEquals("Movie Manager Core Module", webXml.getDisplayName().getName());
			assertEquals(1, webXml.getContextParams().size());
			ContextParam contextParam = webXml.getContextParams().get(0);
			assertEquals("javax.faces.CONFIG_FILES", contextParam.getName());
			assertEquals("/WEB-INF/faces-config-core.xml", contextParam.getValue());
			List<WelcomeFile> welcomeFiles = webXml.getWelcomeFileList().getFiles();
			WelcomeFile welcomeFile = welcomeFiles.get(0);
			assertEquals("faces/search.jsp", welcomeFile.getFile());
			LoginConfig authMethod = webXml.getLoginConfig();
			assertEquals("BASIC", authMethod.getMethod());
		} finally {
			if (in != null) in.close();
		}
	}

	@Test
	public void testWebXml2() throws Exception {
		InputStream in = getClass().getClassLoader().getResourceAsStream("webapp2/web.xml");
		try {
			WebXml webXml = WebXmlParser.parse(in);
			assertEquals("Movie Manager Details Module", webXml.getDisplayName().getName());
			assertEquals(1, webXml.getContextParams().size());
			ContextParam contextParam = webXml.getContextParams().get(0);
			assertEquals("javax.faces.CONFIG_FILES", contextParam.getName());
			assertEquals("/WEB-INF/faces-config-details.xml", contextParam.getValue());
			Servlet servlet = webXml.getServlets().get(0);
			assertEquals("Faces Servlet", servlet.getName());
			assertEquals("javax.faces.webapp.FacesServlet", servlet.getClazz());
			ServletMapping servletMapping = webXml.getServletMappings().get(0);
			assertEquals("Faces Servlet", servletMapping.getName());
			assertEquals("/faces/*", servletMapping.getPattern());
		} finally {
			if (in != null) in.close();
		}
	}

	// Ho cambiato la versione da 2.3 a 2.5 nell'XML
	@Test
	public void testWebXmlStruts() throws Exception {
		InputStream in = getClass().getClassLoader().getResourceAsStream("web-struts.xml");
		try {
			WebXml webXml = WebXmlParser.parse(in);

			assertEquals("E4CPower", webXml.getDisplayName().getName());

			assertEquals(16, webXml.getContextParams().size());

			ContextParam contextParam = webXml.getContextParams().get(1);
			assertEquals("immaginetitolo", contextParam.getName());
			assertEquals("titolo_applicazione.jpg", contextParam.getValue());
			assertEquals(null, contextParam.getDescr());

			assertEquals(9, webXml.getServlets().size());

			Servlet actionServlet = webXml.getServlets().get(0);
			assertEquals("action", actionServlet.getName());
			assertEquals("org.apache.struts.action.ActionServlet", actionServlet.getClazz());

			List<InitParam> initParams = actionServlet.getInitParams();
			assertEquals(3, initParams.size());
			assertEquals("application", initParams.get(2).getName());
			assertEquals("ApplicationResources", initParams.get(2).getValue());

			assertEquals("2", actionServlet.getLoadOnStartup());

			assertEquals(11, webXml.getServletMappings().size());

			ServletMapping servletMapping = webXml.getServletMappings().get(0);
			assertEquals("DisplayChart", servletMapping.getName());
			assertEquals("/servlet/DisplayChart", servletMapping.getPattern());
		} finally {
			if (in != null) in.close();
		}
	}

	@Test
	public void testMerge() throws Exception {
		InputStream in1 = getClass().getClassLoader().getResourceAsStream("webapp1/web.xml");
		InputStream in2 = getClass().getClassLoader().getResourceAsStream("webapp2/web.xml");
		try {
			WebXml webXml1 = WebXmlParser.parse(in1);
			WebXml webXml2 = WebXmlParser.parse(in2);
			WebXml webXml = WebXmlParser.merge(webXml1, webXml2);

			assertEquals("Movie Manager Core Module", webXml.getDisplayName().getName());

			assertEquals(1, webXml.getContextParams().size());
			ContextParam contextParam = webXml.getContextParams().get(0);
			assertEquals("javax.faces.CONFIG_FILES", contextParam.getName());
			assertEquals("/WEB-INF/faces-config-core.xml,/WEB-INF/faces-config-details.xml", contextParam.getValue());

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
			assertEquals("faces/search.jsp", welcomeFile.getFile());

			LoginConfig authMethod = webXml.getLoginConfig();
			assertEquals("BASIC", authMethod.getMethod());
		} finally {
			if (in1 != null) in1.close();
			if (in2 != null) in2.close();
		}
	}

	@Test
	public void testToString() {
		Tag t = new Tag("myname", new Tag("subtag", "value"));
		assertEquals(
				"<myname>" + Tag.NL +
				Tag.TAB + "<subtag>" + "value" + "</subtag>" + Tag.NL +
				"</myname>" + Tag.NL,
				t.getText());
	}

	@Test
	public void testWebXmlFacesComplex() throws Exception {
		InputStream in = getClass().getClassLoader().getResourceAsStream("web-faces-complex.xml");
		try {
			WebXml webXml = WebXmlParser.parse(in);

			List<SecurityConstraint> securityConstraints = webXml.getSecurityConstraints();
			assertNotNull(securityConstraints);
			assertEquals(2, securityConstraints.size());

			SecurityConstraint sc1 = securityConstraints.get(0);
			List<WebResourceCollection> wrc1 = sc1.getResourceCollections();
			assertEquals(1, wrc1.size());

			WebResourceCollection wrc11 = wrc1.get(0);
			assertEquals("Raw-JSF-JSP-Pages", wrc11.getName());
			assertEquals("Description", wrc11.getDescription());
			assertEquals(Arrays.asList("/resources/*", "*.index.jsp"), wrc11.getPatterns());

			AuthConstraint ac1 = sc1.getAuthConstraint();
			assertNotNull(ac1);
			assertEquals("Any description", ac1.getDescription());
			assertEquals("My Role Name", ac1.getRoleName());

			SecurityConstraint sc2 = securityConstraints.get(1);
			List<WebResourceCollection> wrc2 = sc2.getResourceCollections();
			assertEquals(2, wrc2.size());

			WebResourceCollection wrc21 = wrc2.get(0);
			assertEquals("Second security constraint", wrc21.getName());
			assertEquals("Second description", wrc21.getDescription());
			assertEquals(Arrays.asList("/resourcez/*", "*.indecks.jsp"), wrc21.getPatterns());

			WebResourceCollection wrc22 = wrc2.get(1);
			assertEquals("Second security constraint, part II", wrc22.getName());
			assertEquals("Second description bis", wrc22.getDescription());
			assertEquals(Arrays.asList("/resourcez/yeah", "*.indecks.jsf"), wrc22.getPatterns());

			AuthConstraint ac2 = sc2.getAuthConstraint();
			assertNotNull(ac2);
			assertEquals("Still no constraint!", ac2.getDescription());
			assertNull(ac2.getRoleName());

			List<Listener> listeners = webXml.getListeners();
			assertEquals(2, listeners.size());
			Listener l1 = listeners.get(0);
			assertEquals("com.sun.faces.config.ConfigureListener", l1.getClazz());
			assertNull(l1.getDisplayName());
			Listener l2 = listeners.get(1);
			assertEquals("com.i4c.utils.WebActionHelper", l2.getClazz());
			assertEquals("WebAction setup", l2.getDisplayName());

			List<Filter> filters = webXml.getFilters();
			assertEquals(3, filters.size());

			Filter f1 = filters.get(0);
			assertEquals("RichFaces Filter", f1.getDisplayName());
			assertEquals("richfaces", f1.getName());
			assertEquals("org.ajax4jsf.Filter", f1.getClazz());

			assertEquals(3, f1.getInitParams().size());

			assertEquals("forceparser", f1.getInitParams().get(0).getName());
			assertEquals("false", f1.getInitParams().get(0).getValue());

			assertEquals("createTempFiles", f1.getInitParams().get(1).getName());
			assertEquals("false", f1.getInitParams().get(1).getValue());

			assertEquals("maxRequestSize", f1.getInitParams().get(2).getName());
			assertEquals("50000000", f1.getInitParams().get(2).getValue());

			Filter f2 = filters.get(1);
			assertNull(f2.getDisplayName());
			assertEquals("AuthentificationFilter", f2.getName());
			assertEquals("com.i4c.web.filters.AuthentificationFilter", f2.getClazz());

			Filter f3 = filters.get(2);
			assertNull(f3.getDisplayName());
			assertEquals("BlobHandlerFilter", f3.getName());
			assertEquals("com.i4c.web.filters.BlobHandlerFilter", f3.getClazz());

			List<FilterMapping> fm = webXml.getFilterMappings();
			assertEquals(3, fm.size());

			FilterMapping fm1 = fm.get(0);
			assertEquals("richfaces", fm1.getName());
			assertEquals(0, fm1.getUrlPatterns().size());
			assertEquals(1, fm1.getServletNames().size());
			assertEquals("Faces Servlet", fm1.getServletNames().get(0));
			assertEquals(3, fm1.getDispatchers().size());
			assertEquals("REQUEST", fm1.getDispatchers().get(0));
			assertEquals("FORWARD", fm1.getDispatchers().get(1));
			assertEquals("INCLUDE", fm1.getDispatchers().get(2));

			FilterMapping fm2 = fm.get(1);
			assertEquals("AuthentificationFilter", fm2.getName());
			assertEquals(0, fm2.getUrlPatterns().size());
			assertEquals(1, fm2.getServletNames().size());
			assertEquals("Faces Servlet", fm2.getServletNames().get(0));
			assertEquals(0, fm2.getDispatchers().size());

			FilterMapping fm3 = fm.get(2);
			assertEquals("BlobHandlerFilter", fm3.getName());
			assertEquals(0, fm3.getUrlPatterns().size());
			assertEquals(1, fm3.getServletNames().size());
			assertEquals("Faces Servlet", fm3.getServletNames().get(0));
			assertEquals(0, fm3.getDispatchers().size());

			List<ErrorPage> errs = webXml.getErrorPages();
			assertEquals(2, errs.size());

			assertNull(errs.get(0).getExceptionType());
			assertEquals("500", errs.get(0).getErrorCode());
			assertEquals("/error.jsf", errs.get(0).getLocation());

			assertEquals("javax.faces.application.ViewExpiredException", errs.get(1).getExceptionType());
			assertNull(errs.get(1).getErrorCode());
			assertEquals("/wsso.jsf?sessionExpired=true", errs.get(1).getLocation());
		} finally {
			if (in != null) in.close();
		}
	}
}
