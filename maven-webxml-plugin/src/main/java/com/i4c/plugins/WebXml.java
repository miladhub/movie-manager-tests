package com.i4c.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebXml extends Tag {

	// ================ constants ================

	public static String WEB_APP_2_5 =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL +
		"<web-app version=\"2.5\" xmlns=\"http://java.sun.com/xml/ns/javaee\" " + NL +
		TAB + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee " +
		TAB + "http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\"" + NL +
		TAB + "xmlns:web=\"http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\">" + NL;

	// ================ instance variables ================

	private DisplayName displayName;
	private List<ContextParam> contextParams;
	private WelcomeFileList welcomeFileList;
	private LoginConfig loginConfig;
	private List<Servlet> servlets;
	private List<ServletMapping> servletMappings;
	private List<SecurityConstraint> securityConstraints;
	private List<Listener> listeners;
	private List<Filter> filters;
	private List<FilterMapping> filterMappings;
	private List<ErrorPage> errorPages;

	// ================ constructors ================

	public WebXml() { this(null, new ArrayList<ContextParam>(), null, null,  new ArrayList<Servlet>(),  new ArrayList<ServletMapping>(),
			new ArrayList<SecurityConstraint>(), new ArrayList<Listener>(), new ArrayList<Filter>(), new ArrayList<FilterMapping>(),
			new ArrayList<ErrorPage>()); }

	public WebXml(DisplayName displayName, List<ContextParam> contextParams,
			WelcomeFileList welcomeFileList, LoginConfig loginConfig,
			List<Servlet> servlets, List<ServletMapping> servletMappings, List<SecurityConstraint> securityConstraints,
			List<Listener> listeners, List<Filter> filters, List<FilterMapping> filterMappings, List<ErrorPage> errorPages) {
		super("web-app", getContents(displayName, contextParams, welcomeFileList, loginConfig, servletMappings));
		this.displayName = displayName;
		this.contextParams = contextParams;
		this.welcomeFileList = welcomeFileList;
		this.loginConfig = loginConfig;
		this.servlets = servlets;
		this.servletMappings = servletMappings;
		this.securityConstraints = securityConstraints;
		this.listeners = listeners;
		this.filters = filters;
		this.filterMappings = filterMappings;
		this.errorPages = errorPages;
	}

	private static Content[] getContents(DisplayName displayName,
			List<ContextParam> contextParams,
			WelcomeFileList welcomeFileList, LoginConfig loginConfig,
			List<ServletMapping> servletMappings) {
		List<Content> contents = new ArrayList<Content>();
		contents.add(displayName);
		contents.addAll(contextParams);
		contents.add(welcomeFileList);
		contents.add(loginConfig);
		contents.addAll(servletMappings);
		return contents.toArray(new Content[0]);
	}

	@Override protected String openTag() { return WEB_APP_2_5; }

	// ================ getters & setters ================

	public DisplayName getDisplayName() {
		return displayName;
	}
	public void setDisplayName(DisplayName displayName) {
		this.displayName = displayName;
		addContent(displayName);
	}
	public List<ContextParam> getContextParams() {
		return contextParams;
	}
	public void setContextParams(List<ContextParam> contextParams) {
		this.contextParams = contextParams;
		addContents(contextParams);
	}
	public WelcomeFileList getWelcomeFileList() {
		return welcomeFileList;
	}
	public void setWelcomeFileList(WelcomeFileList welcomeFileList) {
		this.welcomeFileList = welcomeFileList;
		addContent(welcomeFileList);
	}
	public LoginConfig getLoginConfig() {
		return loginConfig;
	}
	public void setLoginConfig(LoginConfig loginConfig) {
		this.loginConfig = loginConfig;
		addContent(loginConfig);
	}
	public List<Servlet> getServlets() {
		return servlets;
	}
	public void setServlets(List<Servlet> servlets) {
		this.servlets = servlets;
		addContents(servlets);
	}
	public List<ServletMapping> getServletMappings() {
		return servletMappings;
	}
	public void setServletMappings(List<ServletMapping> servletMappings) {
		this.servletMappings = servletMappings;
		addContents(servletMappings);
	}
	public List<SecurityConstraint> getSecurityConstraints() {
		return securityConstraints;
	}
	public void setSecurityConstraints(List<SecurityConstraint> securityConstraints) {
		this.securityConstraints = securityConstraints;
		addContents(securityConstraints);
	}
	public List<Listener> getListeners() {
		return listeners;
	}
	public void setListeners(List<Listener> listeners) {
		this.listeners = listeners;
		addContents(listeners);
	}
	public List<Filter> getFilters() {
		return filters;
	}
	public void setFilters(List<Filter> filters) {
		this.filters = filters;
		addContents(filters);
	}
	public List<FilterMapping> getFilterMappings() {
		return filterMappings;
	}
	public void setFilterMappings(List<FilterMapping> filterMappings) {
		this.filterMappings = filterMappings;
		addContents(filterMappings);
	}
	public List<ErrorPage> getErrorPages() {
		return errorPages;
	}
	public void setErrorPages(List<ErrorPage> errorPages) {
		this.errorPages = errorPages;
		addContents(errorPages);
	}

	// ================ inner classes ================

	public static final class DisplayName extends Tag {
		private String name;
		public DisplayName(String name) { super("display-name", name); this.name = name; }
		public String getName() { return name; }
	}
	public static final class ContextParam extends Tag {
		private String descr, name, value;
		public ContextParam(String name, String value) { this(name, value, null); }
		public ContextParam(String name, String value, String descr) {
			super("context-param", new Tag("description", descr), new Tag("param-name", name), new Tag("param-value", value));
			this.name = name; this.value = value; this.descr = descr;
		}
		public String getName() { return name; }
		public String getValue() { return value; }
		public String getDescr() { return descr; } // Nota: non c'è nella 2.5, quindi sarà sempre nullo
	}
	public static final class WelcomeFileList extends Tag {
		private WelcomeFile[] files;
		public WelcomeFileList(WelcomeFile... files) {
			super("welcome-file-list", files);
			this.files = files;
		}
		public List<WelcomeFile> getFiles() { return Arrays.asList(files); }
	}
	public static final class WelcomeFile extends Tag {
		private String file;
		public WelcomeFile(String file) { super("welcome-file", file); this.file = file; }
		public String getFile() { return file; }
	}
	public static final class LoginConfig extends Tag {
		private String method;
		public LoginConfig(String method) { super("login-config", new Tag("auth-method", method)); this.method = method; }
		public String getMethod() { return method; }
	}
	public static final class Servlet extends Tag {
		public String name, clazz, loadOnStartup, description;
		public List<InitParam> initParams;
		public Servlet(String description, String name, String clazz, String loadOnStartup, List<InitParam> initParams) {
			super("servlet",
					new Tag("description", description),
					new Tag("servlet-name", name),
					new Tag("servlet-class", clazz));
			this.description = description;
			this.name = name;
			this.clazz = clazz;
			this.loadOnStartup = loadOnStartup;
			this.initParams = initParams;
			this.description = description;
			addContents(initParams);
			addContent(new Tag("load-on-startup", loadOnStartup));
		}
		public String getDescription() { return description; }
		public String getName() { return name; }
		public String getClazz() { return clazz; }
		public String getLoadOnStartup() { return loadOnStartup; }
		public List<InitParam> getInitParams() { return initParams; }

	}
	public static final class ServletMapping extends Tag {
		public String name, pattern;
		public ServletMapping(String name, String pattern) {
			super("servlet-mapping", new Tag("servlet-name", name), new Tag("url-pattern", pattern));
			this.name = name;
			this.pattern = pattern;
		}
		public String getName() { return name; }
		public String getPattern() { return pattern; }
	}
	public static final class InitParam extends Tag {
		private String name, value;
		public InitParam(String name, String value) {
			super("init-param", new Tag("param-name", name), new Tag("param-value", value));
			this.name = name;
			this.value = value;
		}
		public String getName() { return name; }
		public String getValue() { return value; }
	}
	public static final class SecurityConstraint extends Tag {
		private List<WebResourceCollection> resourceCollections; // + (once or more)
		private AuthConstraint authConstraint; // ? (zero or once)
		public SecurityConstraint(List<WebResourceCollection> resourceCollections, AuthConstraint authConstraint) {
			super("security-constraint", append(resourceCollections, authConstraint));
			this.resourceCollections = resourceCollections;
			this.authConstraint = authConstraint;
		}
		public List<WebResourceCollection> getResourceCollections() {
			return resourceCollections;
		}
		public AuthConstraint getAuthConstraint() {
			return authConstraint;
		}
	}
	public static final class WebResourceCollection extends Tag {
		private String name;
		private String description;
		private List<String> patterns;
		public WebResourceCollection(String name, String description, List<String> patterns) {
			super("web-resource-collection", new Tag("web-resource-name", name), new Tag("description", description));
			addContents(getTags("url-pattern", patterns));
			this.name = name;
			this.description = description;
			this.patterns = patterns;
		}
		public String getName() { return name; }
		public String getDescription() { return description; }
		public List<String> getPatterns() { return patterns; }
	}
	public static final class AuthConstraint extends Tag {
		private String description;
		private String roleName;
		public AuthConstraint(String description, String roleName) {
			super("auth-constraint", new Tag("description", description), new Tag("role-name", roleName));
			this.description = description;
			this.roleName = roleName;
		}
		public String getDescription() { return description; }
		public String getRoleName() { return roleName; }
	}
	public static final class Listener extends Tag {
		private String displayName, clazz;
		public Listener(String displayName, String clazz) {
			super("listener", new Tag("display-name", displayName), new Tag("listener-class", clazz));
			this.displayName = displayName;
			this.clazz = clazz;
		}
		public String getDisplayName() { return displayName; }
		public String getClazz() { return clazz; }
	}
	public static final class Filter extends Tag {
		private String displayName, name, clazz;
		private List<InitParam> initParams;
		public Filter(String displayName, String name, String clazz, List<InitParam> initParams) {
			super("filter", new Tag("display-name", displayName), new Tag("filter-name", name), new Tag("filter-class", clazz));
			addContents(initParams);
			this.displayName = displayName;
			this.name = name;
			this.clazz = clazz;
			this.initParams = initParams;
		}
		public String getDisplayName() { return displayName; }
		public String getName() { return name; }
		public String getClazz() { return clazz; }
		public List<InitParam> getInitParams() { return initParams; }
	}
	public static final class FilterMapping extends Tag {
		private String name;
		private List<String> urlPatterns, servletNames, dispatchers;
		public FilterMapping(String name, List<String> urlPatterns, List<String> servletNames, List<String> dispatchers) {
			super("filter-mapping", new Tag("filter-name", name));
			for (String urlPattern : urlPatterns) {
				addContent(new Tag("url-pattern", urlPattern));
			}
			for (String servletName : servletNames) {
				addContent(new Tag("servlet-name", servletName));
			}
			for (String dispatcher : dispatchers) {
				addContent(new Tag("dispatcher", dispatcher));
			}
			this.name = name;
			this.urlPatterns = urlPatterns;
			this.servletNames = servletNames;
			this.dispatchers = dispatchers;
		}
		public String getName() { return name; }
		public List<String> getUrlPatterns() { return urlPatterns; }
		public List<String> getServletNames() { return servletNames; }
		public List<String> getDispatchers() { return dispatchers; }
	}
	public static final class ErrorPage extends Tag {
		private String errorCode, exceptionType, location;
		public ErrorPage(String errorCode, String exceptionType, String location) {
			super("error-page", new Tag("error-code", errorCode), new Tag("exception-type", exceptionType), new Tag("location", location));
			this.errorCode = errorCode;
			this.exceptionType = exceptionType;
			this.location = location;
		}
		public String getErrorCode() { return errorCode; }
		public String getExceptionType() { return exceptionType; }
		public String getLocation() { return location; }
	}
}

abstract class Content {
	protected static final String TAB = "   ", NL = "\n";
	public String getText() { return getText(0); }
	public abstract String getText(int depth);
	protected String getTabs(int depth) {
		StringBuilder sb = new StringBuilder(TAB.length() * depth);
		for (int i = 0; i < depth; i++) { sb.append(TAB); }
		return sb.toString();
	}
	public String toString() { return getText(); }
	public abstract boolean isEmtpy();
	public abstract boolean isNested();
	protected static List<Content> append(List<? extends Content> list, Content... c) {
		List<Content> result = new ArrayList<Content>();
		result.addAll(list);
		result.addAll(Arrays.asList(c));
		return result;
	}
}

class SimpleContent extends Content {
	private String value;
	public SimpleContent(String value) { this.value = value; }
	public String getText(int depth) { return value; }
	@Override public boolean isEmtpy() { return value == null; }
	@Override public boolean isNested() { return false; }
}

class Tag extends Content {
	private static final String BEGIN_OPEN = "<", END_OPEN = "</", CLOSE = ">";
	private String tag;
	private List<Content> contents;
	public Tag(String tag, List<Content> contents) { this.tag = tag; this.contents = contents; }
	public Tag(String tag, Content... contents) { this(tag, new ArrayList<Content>(Arrays.asList(contents))); }
	public Tag(String tag, String value) { this(tag, new SimpleContent(value)); }
	public String getText(int depth) {
		StringBuilder sb = new StringBuilder(1024);

		if (contents.size() == 1 && !contents.get(0).isNested()) {
			sb.append(getTabs(depth) + BEGIN_OPEN + tag + CLOSE);
			sb.append(contents.get(0).getText());
			sb.append(END_OPEN + tag + CLOSE + NL); // </NAME>
		} else {
			sb.append(getTabs(depth) + openTag()); // <NAME>
			for (Content c : contents) {
				if (c != null && !c.isEmtpy())
					sb.append(c.getText(depth + 1));
			}
			sb.append(getTabs(depth) + END_OPEN + tag + CLOSE + NL); // </NAME>
		}
		return sb.toString();
	}
	protected String openTag() {
		return BEGIN_OPEN + tag + CLOSE + NL;
	}
	protected void addContent(Content c) { contents.add(c); }
	protected void addContents(List<? extends Content> cs) { contents.addAll(cs); }
	protected static List<Content> getTags(String tagName, String[] patterns) {
		return getTags(tagName, Arrays.asList(patterns));
	}
	protected static List<Content> getTags(String tagName, List<String> patterns) {
		List<Content> cs = new ArrayList<Content>(patterns.size());
		for (String p : patterns) {
			cs.add(new Tag(tagName, p));
		}
		return cs;
	}
	@Override public boolean isEmtpy() {
		for (Content c : contents) {
			if (!c.isEmtpy()) return false;
		}
		return true;
	}
	@Override public boolean isNested() { return true; }
}
