package com.i4c.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import com.i4c.plugins.WebXml.AuthConstraint;
import com.i4c.plugins.WebXml.ErrorPage;
import com.i4c.plugins.WebXml.Filter;
import com.i4c.plugins.WebXml.FilterMapping;
import com.i4c.plugins.WebXml.InitParam;
import com.i4c.plugins.WebXml.Listener;
import com.i4c.plugins.WebXml.LoginConfig;
import com.i4c.plugins.WebXml.ContextParam;
import com.i4c.plugins.WebXml.DisplayName;
import com.i4c.plugins.WebXml.SecurityConstraint;
import com.i4c.plugins.WebXml.Servlet;
import com.i4c.plugins.WebXml.ServletMapping;
import com.i4c.plugins.WebXml.WebResourceCollection;
import com.i4c.plugins.WebXml.WelcomeFile;
import com.i4c.plugins.WebXml.WelcomeFileList;

public class WebXmlParser {

	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(WebXmlParser.class);

	private static Namespace NS = Namespace.getNamespace("http://java.sun.com/xml/ns/javaee");
	private static String FACES_CONFIG_STRING = "javax.faces.CONFIG_FILES";
	private static final String COMMA = ",";

	public WebXmlParser() {}

	/**
	 * Effettua il parsing del web.xml a partire dal suo input stream.
	 * @param in input stream del web.xml da parsare; il flusso <b>non</b> viene chiuso da questo metodo
	 * @return un DTO che rappresenta il risultato del parsing del web.xml
	 * @throws JDOMException in caso di errori di merge
	 * @throws IOException in caso di errori di I/O
	 */
	@SuppressWarnings("unchecked")
	public static WebXml parse(InputStream in) throws JDOMException, IOException {
		WebXml webXml = new WebXml();
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);
		Element webApp = doc.getRootElement();

		webXml.setDisplayName(new DisplayName(webApp.getChildText("display-name", NS)));

		List<Element> contextParamEls = webApp.getChildren("context-param", NS);
		List<ContextParam> cps = new ArrayList<ContextParam>();
		if (contextParamEls != null) {
			for (Element cp : contextParamEls) {
				cps.add(new ContextParam(cp.getChildText("param-name", NS), cp.getChildText("param-value", NS), cp.getChildText("description", NS)));
			}
		}
		webXml.setContextParams(cps);

		List<Element> servletEls = webApp.getChildren("servlet", NS);
		if (servletEls != null) {
			List<Servlet> servlets = new ArrayList<Servlet>();
			for (Element sel : servletEls) {
				servlets.add(new Servlet(sel.getChildText("description", NS), sel.getChildText("servlet-name", NS),
						sel.getChildText("servlet-class", NS), sel.getChildText("load-on-startup", NS), parseInitParams(sel)));
			}
			webXml.setServlets(servlets);
		}

		List<Element> servletMapEls = webApp.getChildren("servlet-mapping", NS);
		if (servletEls != null) {
			List<ServletMapping> servletMappings = new ArrayList<ServletMapping>();
			for (Element sel : servletMapEls) {
				servletMappings.add(new ServletMapping(sel.getChildText("servlet-name", NS), sel.getChildText("url-pattern", NS)));
			}
			webXml.setServletMappings(servletMappings);
		}

		Element wfl = webApp.getChild("welcome-file-list", NS);
		if (wfl != null) {
			List<Element> wfels = wfl.getChildren();
			List<WelcomeFile> wlfs = new ArrayList<WelcomeFile>();
			for (Element wf : wfels) {
				wlfs.add(new WelcomeFile(wf.getValue()));
			}
			webXml.setWelcomeFileList(new WelcomeFileList(wlfs.toArray(new WelcomeFile[0])));
		}

		List<Element> secConstrEls = webApp.getChildren("security-constraint", NS);
		if (secConstrEls != null) {
			List<SecurityConstraint> secConstrs = new ArrayList<SecurityConstraint>();
			for (Element secConstrEl : secConstrEls) {
				List<Element> webResColEls = secConstrEl.getChildren("web-resource-collection", NS);
				List<WebResourceCollection> webResCols = new ArrayList<WebResourceCollection>();
				for (Element webResColEl : webResColEls) { // C'è almeno un web-resource-collection
					String webResName = webResColEl.getChildText("web-resource-name", NS);
					String descr = webResColEl.getChildText("description", NS);
					List<Element> urlPatternEls = webResColEl.getChildren("url-pattern", NS);
					List<String> urlPatterns = new ArrayList<String>();
					for (Element urlPatternEl : urlPatternEls) { // C'è almeno un url-pattern
						urlPatterns.add(urlPatternEl.getValue());
					}
					webResCols.add(new WebResourceCollection(webResName, descr, urlPatterns));
				}

				Element authConstrEl = secConstrEl.getChild("auth-constraint", NS);
				AuthConstraint authConstr = null;
				if (authConstrEl != null) {
					String description = authConstrEl.getChildText("description", NS);
					String roleName = authConstrEl.getChildText("role-name", NS);
					authConstr = new AuthConstraint(description, roleName);
				}
				secConstrs.add( new SecurityConstraint(webResCols, authConstr) );
			}
			webXml.setSecurityConstraints(secConstrs);
		}

		List<Element> listEls = webApp.getChildren("listener", NS);
		if (listEls != null) {
			List<Listener> listeners = new ArrayList<Listener>();
			for (Element listEl : listEls) {
				listeners.add(new Listener(listEl.getChildText("display-name", NS), listEl.getChildText("listener-class", NS)));
			}
			webXml.setListeners(listeners);
		}

		List<Element> filtEls = webApp.getChildren("filter", NS);
		if (filtEls != null) {
			List<Filter> filters = new ArrayList<Filter>();
			for (Element filtEl : filtEls) {
				filters.add(new Filter(filtEl.getChildText("display-name", NS), filtEl.getChildText("filter-name", NS),
						filtEl.getChildText("filter-class", NS), parseInitParams(filtEl)));
			}
			webXml.setFilters(filters);
		}

		List<Element> filtMappEls = webApp.getChildren("filter-mapping", NS);
		if (filtMappEls != null) {
			List<FilterMapping> fmaps = new ArrayList<FilterMapping>();
			for (Element filtMappEl : filtMappEls) {
				List<String> urlPatterns = new ArrayList<String>();
				List<String> servletNames = new ArrayList<String>();
				List<String> dispatchers = new ArrayList<String>();
				List<Element> urlPattEls = filtMappEl.getChildren("url-pattern", NS);
				if (urlPattEls != null) {
					for (Element urlPattEl : urlPattEls) {
						urlPatterns.add(urlPattEl.getValue());
					}
				}
				List<Element> servlNameEls = filtMappEl.getChildren("servlet-name", NS);
				if (servlNameEls != null) {
					for (Element servlNameEl : servlNameEls) {
						servletNames.add(servlNameEl.getValue());
					}
				}
				List<Element> dispEls = filtMappEl.getChildren("dispatcher", NS);
				if (dispEls != null) {
					for (Element dispEl : dispEls) {
						dispatchers.add(dispEl.getValue());
					}
				}
				fmaps.add( new FilterMapping(filtMappEl.getChildText("filter-name", NS), urlPatterns, servletNames, dispatchers) );
			}
			webXml.setFilterMappings(fmaps);
		}

		Element loginCfgEl = webApp.getChild("login-config", NS);
		if (loginCfgEl != null) {
			Element authMethEl = loginCfgEl.getChild("auth-method", NS);
			webXml.setLoginConfig(new LoginConfig(authMethEl.getValue()));
		}

		List<Element> errPageEls = webApp.getChildren("error-page", NS);
		if (errPageEls != null) {
			List<ErrorPage> errPages = new ArrayList<ErrorPage>();
			for (Element errPageEl : errPageEls) {
				errPages.add(new ErrorPage(errPageEl.getChildText("error-code", NS), errPageEl.getChildText("exception-type", NS),
						errPageEl.getChildText("location", NS)));
			}
			webXml.setErrorPages(errPages);
		}

		return webXml;
	}

	@SuppressWarnings("unchecked")
	private static List<InitParam> parseInitParams(Element element) {
		List<InitParam> ips = new ArrayList<InitParam>();
		List<Element> ipels = element.getChildren("init-param", NS);
		if (ips != null) {
			for (Element ipel : ipels) {
				ips.add(new InitParam(ipel.getChildText("param-name", NS), ipel.getChildText("param-value", NS)));
			}
		}
		return ips;
	}

	/**
	 * Effettua il merge dei due web.xml specificati. La strategia per i tag supportati è la seguente:
	 * <ul>
	 * <li><b><tt>display-name</tt></b>: vince il primo incontrato</li>
	 * <li><b><tt>context-param</tt></b>: vengono aggiunti tutti, nell'ordine con cui vengono incontrati;
	 * inoltre, gli elementi con nome {@link #FACES_CONFIG_STRING} vengono unificati in una lista separata
	 * da virgole, i cui elementi sono aggiunti nell'ordine con cui vengono trovati</li>
	 * <li><b><tt>servlet-mapping</tt></b>: vengono aggiunti tutti, nell'ordine con cui vengono incontrati</li>
	 * <li><b><tt>servlet</tt></b>: vengono aggiunti tutti, nell'ordine con cui vengono incontrati</li>
	 * <li><b><tt>welcome-file</tt></b>: vengono aggiunti tutti, nell'ordine con cui vengono incontrati</li>
	 * <li><b><tt>auth-method</tt></b>: vince il primo incontrato</li>
	 * </ul>
	 * @param left primo file da mergiare
	 * @param right secondo file da mergiare
	 * @return risultato del merge
	 */
	public static WebXml merge(WebXml left, WebXml right) {
		WebXml result = new WebXml();

		if (left.getDisplayName() != null) {
			result.setDisplayName(left.getDisplayName());
		} else {
			result.setDisplayName(right.getDisplayName());
		}

		List<ContextParam> params = new ArrayList<ContextParam>(left.getContextParams());
		params.addAll(right.getContextParams());
		result.setContextParams(mergeFacesConfigs(params));

		List<Servlet> servlets = new ArrayList<Servlet>(left.getServlets());
		servlets.addAll(right.getServlets());
		result.setServlets(servlets);

		List<ServletMapping> mappings = new ArrayList<ServletMapping>(left.getServletMappings());
		mappings.addAll(right.getServletMappings());
		result.setServletMappings(mappings);

		List<WelcomeFile> welcomeFiles = new ArrayList<WelcomeFile>(left.getWelcomeFileList().getFiles());
		if (right.getWelcomeFileList() != null) welcomeFiles.addAll(right.getWelcomeFileList().getFiles());
		result.setWelcomeFileList(new WelcomeFileList(welcomeFiles.toArray(new WelcomeFile[0])));

		List<SecurityConstraint> securityConstraints = new ArrayList<SecurityConstraint>(left.getSecurityConstraints());
		securityConstraints.addAll(right.getSecurityConstraints());
		result.setSecurityConstraints(securityConstraints);

		List<Listener> listeners = new ArrayList<Listener>(left.getListeners());
		listeners.addAll(right.getListeners());
		result.setListeners(listeners);

		List<Filter> filters = new ArrayList<Filter>(left.getFilters());
		filters.addAll(right.getFilters());
		result.setFilters(filters);

		List<FilterMapping> filterMappings = new ArrayList<FilterMapping>(left.getFilterMappings());
		filterMappings.addAll(right.getFilterMappings());
		result.setFilterMappings(filterMappings);

		if (left.getLoginConfig() != null) {
			result.setLoginConfig(left.getLoginConfig());
		} else {
			result.setLoginConfig(right.getLoginConfig());
		}

		List<ErrorPage> errorPages = new ArrayList<ErrorPage>(left.getErrorPages());
		errorPages.addAll(right.getErrorPages());
		result.setErrorPages(errorPages);

		return result;
	}

	private static List<ContextParam> mergeFacesConfigs(List<ContextParam> params) {
		List<ContextParam> merged = new ArrayList<ContextParam>();
		List<ContextParam> facesParams = new ArrayList<ContextParam>();
		for (ContextParam p : params) {
			if (!FACES_CONFIG_STRING.equals(p.getName())) {
				merged.add(p);
			} else {
				facesParams.add(p);
			}
		}

		if (facesParams.size() > 0) {
			StringBuilder sb = new StringBuilder(1024);
			int i = 0;
			for (ContextParam p : facesParams) {
				sb.append(p.getValue());
				if (++i < facesParams.size())
					sb.append(COMMA);
			}

			merged.add(new ContextParam(FACES_CONFIG_STRING, sb.toString()));
		}

		return merged;
	}
}
