/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.servlethelpers;

import static org.apache.sling.servlethelpers.MockSlingHttpServletResponse.CHARSET_SEPARATOR;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.ReadListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.SlingAdaptable;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.request.RequestProgressTracker;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Mock {@link SlingHttpServletRequest} implementation.
 */
@ConsumerType
public class MockSlingHttpServletRequest extends SlingAdaptable implements SlingHttpServletRequest {

    private final ResourceResolver resourceResolver;
    private final RequestPathInfo requestPathInfo;
    private Map<String, Object> attributeMap = new HashMap<String, Object>();
    private Map<String, MockRequestParameter[]> parameterMap = new LinkedHashMap<>();
    private HttpSession session;
    private Resource resource;
    private String authType;
    private String contextPath = "";
    private String queryString;
    private String scheme = "http";
    private String serverName = "localhost";
    private int serverPort = 80;
    private String servletPath = StringUtils.EMPTY;
    private String pathInfo = null;
    private String method = HttpConstants.METHOD_GET;
    private final HeaderSupport headerSupport = new HeaderSupport();
    private final CookieSupport cookieSupport = new CookieSupport();
    private String contentType;
    private String characterEncoding;
    private byte[] content;
    private String remoteUser;
    private String remoteAddr;
    private String remoteHost;
    private int remotePort;
    private Locale locale = Locale.US;
    private boolean getInputStreamCalled;
    private boolean getReaderCalled;
    private List<Part> parts = new ArrayList<>();

    private MockRequestDispatcherFactory requestDispatcherFactory;
    private String responseContentType;

    protected static final ResourceBundle EMPTY_RESOURCE_BUNDLE = new ListResourceBundle() {
        @Override
        protected Object[][] getContents() {
            return new Object[0][0];
        }
    };

    /**
     * @param resourceResolver Resource resolver
     */
    public MockSlingHttpServletRequest(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
        this.requestPathInfo = newMockRequestPathInfo();
    }

    protected MockHttpSession newMockHttpSession() {
        return new MockHttpSession();
    }

    protected MockRequestPathInfo newMockRequestPathInfo() {
        return new MockRequestPathInfo(this.resourceResolver);
    }

    @Override
    public ResourceResolver getResourceResolver() {
        return this.resourceResolver;
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public HttpSession getSession(boolean create) {
        if (this.session == null && create) {
            this.session = newMockHttpSession();
        }
        return this.session;
    }

    @Override
    public RequestPathInfo getRequestPathInfo() {
        return this.requestPathInfo;
    }

    @Override
    public Object getAttribute(String name) {
        return this.attributeMap.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return IteratorUtils.asEnumeration(this.attributeMap.keySet().iterator());
    }

    @Override
    public void removeAttribute(String name) {
        this.attributeMap.remove(name);
    }

    @Override
    public void setAttribute(String name, Object object) {
        this.attributeMap.put(name, object);
    }

    @Override
    public Resource getResource() {
        return this.resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String getParameter(String name) {
        MockRequestParameter[] params = this.parameterMap.get(name);
        if (params != null && params.length > 0) {
            return params[0].getString();
        }
        return null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        LinkedHashMap<String, String[]> result = new LinkedHashMap<>();
        for (Entry<String, MockRequestParameter[]> entry : this.parameterMap.entrySet()) {
            MockRequestParameter[] values = entry.getValue();
            String[] resultValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                resultValues[i] = values[i].getString();
            }
            result.put(entry.getKey(), resultValues);
        }

        return result;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return IteratorUtils.asEnumeration(this.parameterMap.keySet().iterator());
    }

    @Override
    public String[] getParameterValues(String name) { // NOPMD
        MockRequestParameter[] param = this.parameterMap.get(name);
        if (param != null) {
            String[] values = new String[param.length];
            for (int i = 0; i < param.length; i++) {
                values[i] = param[i].getString();
            }
            return values;
        }
        return null; // NOPMD
    }

    /**
     * @param parameterMap Map of parameters
     */
    public void setParameterMap(Map<String, Object> parameterMap) {
        this.parameterMap.clear();
        for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String[]) {
                String[] array = (String[]) value;
                MockRequestParameter[] values = new MockRequestParameter[array.length];
                for (int i = 0; i < array.length; i++) {
                    values[i] = new MockRequestParameter(key, array[i]);
                }
                this.parameterMap.put(key, values);
            } else if (value instanceof MockRequestParameter[]) {
                this.parameterMap.put(key, (MockRequestParameter[]) value);
            } else if (value != null) {
                this.addRequestParameter(key, value.toString());
            } else {
                this.parameterMap.put(key, null);
            }
        }
        try {
            this.queryString = formatQueryString(this.parameterMap);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String formatQueryString(Map<String, MockRequestParameter[]> map) throws UnsupportedEncodingException {
        StringBuilder querystring = new StringBuilder();
        for (Map.Entry<String, MockRequestParameter[]> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                formatQueryStringParameter(querystring, entry);
            }
        }
        return querystring.length() > 0 ? querystring.toString() : null;
    }

    private static void formatQueryStringParameter(StringBuilder querystring, Map.Entry<String, MockRequestParameter[]> entry) throws UnsupportedEncodingException {
        for (MockRequestParameter value : entry.getValue()) {
            if (querystring.length() != 0) {
                querystring.append('&');
            }
            querystring.append(URLEncoder.encode(entry.getKey(), CharEncoding.UTF_8));
            querystring.append('=');
            if (value.getString() != null) {
                querystring.append(URLEncoder.encode(value.getString(), CharEncoding.UTF_8));
            }
        }
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    /**
     * @param loc Request locale
     */
    public void setLocale(Locale loc) {
        this.locale = loc;
    }

    @Override
    public String getContextPath() {
        return this.contextPath;
    }

    /**
     * @param contextPath Webapp context path
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    /**
     * @param queryString Query string (with proper URL encoding)
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
        try {
            parseQueryString(this.parameterMap, this.queryString);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void parseQueryString(Map<String, MockRequestParameter[]> map, String query) throws UnsupportedEncodingException {
        Map<String, List<String>> queryPairs = new LinkedHashMap<String, List<String>>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), CharEncoding.UTF_8) : pair;
            if (!queryPairs.containsKey(key)) {
                queryPairs.put(key, new ArrayList<String>());
            }
            String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), CharEncoding.UTF_8) : null;
            queryPairs.get(key).add(value);
        }
        map.clear();
        for (Map.Entry<String, List<String>> entry : queryPairs.entrySet()) {
            List<String> valueList = entry.getValue();
            int numEntries = valueList.size();
            MockRequestParameter[] values = new MockRequestParameter[numEntries];
            for (int i = 0; i < numEntries; i++) {
                values[i] = new MockRequestParameter(entry.getKey(), valueList.get(i));
            }
            map.put(entry.getKey(), values);
        }
    }

    @Override
    public String getQueryString() {
        return this.queryString;
    }

    @Override
    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    @Override
    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public int getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public boolean isSecure() {
        return StringUtils.equals("https", getScheme());
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public long getDateHeader(String name) {
        return headerSupport.getDateHeader(name);
    }

    @Override
    public String getHeader(String name) {
        return headerSupport.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return HeaderSupport.toEnumeration(headerSupport.getHeaderNames());
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return HeaderSupport.toEnumeration(headerSupport.getHeaders(name));
    }

    @Override
    public int getIntHeader(String name) {
        return headerSupport.getIntHeader(name);
    }

    /**
     * Add header, keep existing ones with same name.
     * 
     * @param name  Header name
     * @param value Header value
     */
    public void addHeader(String name, String value) {
        headerSupport.addHeader(name, value);
    }

    /**
     * Add header, keep existing ones with same name.
     * 
     * @param name  Header name
     * @param value Header value
     */
    public void addIntHeader(String name, int value) {
        headerSupport.addIntHeader(name, value);
    }

    /**
     * Add header, keep existing ones with same name.
     * 
     * @param name Header name
     * @param date Header value
     */
    public void addDateHeader(String name, long date) {
        headerSupport.addDateHeader(name, date);
    }

    /**
     * Set header, overwrite existing ones with same name.
     * 
     * @param name  Header name
     * @param value Header value
     */
    public void setHeader(String name, String value) {
        headerSupport.setHeader(name, value);
    }

    /**
     * Set header, overwrite existing ones with same name.
     * 
     * @param name  Header name
     * @param value Header value
     */
    public void setIntHeader(String name, int value) {
        headerSupport.setIntHeader(name, value);
    }

    /**
     * Set header, overwrite existing ones with same name.
     * 
     * @param name Header name
     * @param date Header value
     */
    public void setDateHeader(String name, long date) {
        headerSupport.setDateHeader(name, date);
    }

    @Override
    public Cookie getCookie(String name) {
        return cookieSupport.getCookie(name);
    }

    @Override
    public Cookie[] getCookies() {
        return cookieSupport.getCookies();
    }

    /**
     * Set cookie
     * 
     * @param cookie Cookie
     */
    public void addCookie(Cookie cookie) {
        cookieSupport.addCookie(cookie);
    }

    @Override
    public ResourceBundle getResourceBundle(Locale locale) {
        return getResourceBundle(null, locale);
    }

    @Override
    public ResourceBundle getResourceBundle(String baseName, Locale locale) {
        return EMPTY_RESOURCE_BUNDLE;
    }

    @Override
    public RequestParameter getRequestParameter(String name) {
        MockRequestParameter[] params = this.parameterMap.get(name);
        if (params != null && params.length > 0) {
            return params[0];
        }
        return null;
    }

    @Override
    public RequestParameterMap getRequestParameterMap() {
        MockRequestParameterMap map = new MockRequestParameterMap();
        for (Map.Entry<String, String[]> entry : getParameterMap().entrySet()) {
            map.put(entry.getKey(), getRequestParameters(entry.getKey()));
        }
        return map;
    }

    @Override
    public RequestParameter[] getRequestParameters(String name) {
        return this.parameterMap.get(name);
    }

    // part of Sling API 2.7
    public List<RequestParameter> getRequestParameterList() {
        List<RequestParameter> params = new ArrayList<RequestParameter>();
        for (RequestParameter[] requestParameters : getRequestParameterMap().values()) {
            params.addAll(Arrays.asList(requestParameters));
        }
        return params;
    }

    /**
     * Add a request parameter that consists of a simple name/value pair. This
     * emulates a simple form field.
     * 
     * @param name  field name
     * @param value field value
     */
    public void addRequestParameter(String name, String value) {
        MockRequestParameter mockRequestParameter = new MockRequestParameter(name, value);
        addMockRequestParameter(name, mockRequestParameter);
    }

    /**
     * Add a request parameter that emulates a file upload field.
     * 
     * @param name        field name
     * @param content     file content
     * @param contentType mime type of content in the field
     */
    public void addRequestParameter(String name, byte[] content, String contentType) {
        MockRequestParameter mockRequestParameter = new MockRequestParameter(name, content, contentType);
        addMockRequestParameter(name, mockRequestParameter);
    }

    /**
     * Add a request parameter that emulates a file upload field with a filename
     * associated with it.
     * 
     * @param name        field name
     * @param content     file content
     * @param contentType mime type of content in the field
     * @param filename    filename associated with content
     */
    public void addRequestParameter(String name, byte[] content, String contentType, String filename) {
        MockRequestParameter mockRequestParameter = new MockRequestParameter(name, content, contentType, filename);
        addMockRequestParameter(name, mockRequestParameter);
    }

    private void addMockRequestParameter(String name, MockRequestParameter mockRequestParameter) {
        if (this.parameterMap.containsKey(name)) {
            List<MockRequestParameter> list = new ArrayList<>(Arrays.asList(this.parameterMap.get(name)));
            list.add(mockRequestParameter);
            this.parameterMap.put(name, list.toArray(new MockRequestParameter[0]));
        } else {
            this.parameterMap.put(name, new MockRequestParameter[] { mockRequestParameter });
        }
    }

    @Override
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.characterEncoding = charset;
    }

    @Override
    public String getContentType() {
        if (this.contentType == null) {
            return null;
        } else {
            return this.contentType + (StringUtils.isNotBlank(characterEncoding) ? CHARSET_SEPARATOR + characterEncoding : "");
        }
    }

    public void setContentType(String type) {
        this.contentType = type;
        if (StringUtils.contains(this.contentType, CHARSET_SEPARATOR)) {
            this.characterEncoding = StringUtils.substringAfter(this.contentType, CHARSET_SEPARATOR);
            this.contentType = StringUtils.substringBefore(this.contentType, CHARSET_SEPARATOR);
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        if (getReaderCalled) {
            throw new IllegalStateException();
        }
        getInputStreamCalled = true;
        return new ServletInputStream() {
            private final InputStream is = content == null ? new ByteArrayInputStream(new byte[0]) : new ByteArrayInputStream(content);

            @Override
            public int read() throws IOException {
                return is.read();
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public boolean isFinished() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int getContentLength() {
        if (content == null) {
            return 0;
        }
        return content.length;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        if (requestDispatcherFactory == null) {
            throw new IllegalStateException("Please provdide a MockRequestDispatcherFactory (setRequestDispatcherFactory).");
        }
        return requestDispatcherFactory.getRequestDispatcher(path, null);
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path, RequestDispatcherOptions options) {
        if (requestDispatcherFactory == null) {
            throw new IllegalStateException("Please provdide a MockRequestDispatcherFactory (setRequestDispatcherFactory).");
        }
        return requestDispatcherFactory.getRequestDispatcher(path, options);
    }

    @Override
    public RequestDispatcher getRequestDispatcher(Resource resource) {
        if (requestDispatcherFactory == null) {
            throw new IllegalStateException("Please provdide a MockRequestDispatcherFactory (setRequestDispatcherFactory).");
        }
        return requestDispatcherFactory.getRequestDispatcher(resource, null);
    }

    @Override
    public RequestDispatcher getRequestDispatcher(Resource resource, RequestDispatcherOptions options) {
        if (requestDispatcherFactory == null) {
            throw new IllegalStateException("Please provdide a MockRequestDispatcherFactory (setRequestDispatcherFactory).");
        }
        return requestDispatcherFactory.getRequestDispatcher(resource, options);
    }

    public void setRequestDispatcherFactory(MockRequestDispatcherFactory requestDispatcherFactory) {
        this.requestDispatcherFactory = requestDispatcherFactory;
    }

    @Override
    public String getRemoteUser() {
        return remoteUser;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    @Override
    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    @Override
    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    @Override
    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    @Override
    public String getServletPath() {
        return this.servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    @Override
    public String getPathInfo() {
        if (this.pathInfo != null) {
            return this.pathInfo;
        }

        RequestPathInfo requestPathInfo = this.getRequestPathInfo();

        if (StringUtils.isEmpty(requestPathInfo.getResourcePath())) {
            return null;
        }

        StringBuilder pathInfo = new StringBuilder();

        pathInfo.append(requestPathInfo.getResourcePath());

        if (StringUtils.isNotEmpty(requestPathInfo.getSelectorString())) {
            pathInfo.append('.');
            pathInfo.append(requestPathInfo.getSelectorString());
        }

        if (StringUtils.isNotEmpty(requestPathInfo.getExtension())) {
            pathInfo.append('.');
            pathInfo.append(requestPathInfo.getExtension());
        }

        if (StringUtils.isNotEmpty(requestPathInfo.getSuffix())) {
            pathInfo.append(requestPathInfo.getSuffix());
        }

        return pathInfo.toString();
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    @Override
    public String getRequestURI() {
        StringBuilder requestUri = new StringBuilder();

        if (StringUtils.isNotEmpty(this.getServletPath())) {
            requestUri.append(this.getServletPath());
        }

        if (StringUtils.isNotEmpty(this.getPathInfo())) {
            requestUri.append(this.getPathInfo());
        }

        if (StringUtils.isEmpty(requestUri)) {
            return "/";
        } else {
            return requestUri.toString();
        }
    }

    @Override
    public StringBuffer getRequestURL() {
        StringBuffer requestUrl = new StringBuffer();

        requestUrl.append(this.getScheme());
        requestUrl.append("://");
        requestUrl.append(getServerName());
        if ((StringUtils.equals(this.getScheme(), "http") && this.getServerPort() != 80) || (StringUtils.equals(this.getScheme(), "https") && this.getServerPort() != 443)) {
            requestUrl.append(':');
            requestUrl.append(getServerPort());
        }
        requestUrl.append(getRequestURI());

        return requestUrl;
    }

    @Override
    public String getAuthType() {
        return this.authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    @Override
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        return AdaptableUtil.adaptToWithoutCaching(this, type);
    }

    @Override
    public String getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    @Override
    public Enumeration<String> getResponseContentTypes() {
        return Collections.enumeration(Collections.singleton(responseContentType));
    }

    @Override
    public BufferedReader getReader() {
        if (getInputStreamCalled) {
            throw new IllegalStateException();
        }
        getReaderCalled = true;
        if (this.content == null) {
            return new BufferedReader(new StringReader(""));
        } else {
            String content;
            try {
                if (characterEncoding == null) {
                    content = new String(this.content, Charset.defaultCharset());
                } else {
                    content = new String(this.content, characterEncoding);
                }
            } catch (UnsupportedEncodingException e) {
                content = new String(this.content, Charset.defaultCharset());
            }
            return new BufferedReader(new StringReader(content));
        }

    }

    @Override
    public RequestProgressTracker getRequestProgressTracker() {
        return new MockRequestProgressTracker();
    }
    
    public void addPart(Part part) {
    	if ( part == null )
    		throw new IllegalArgumentException("part may not be null");
    	this.parts.add(part);
    }
    
    @Override
    public Collection<Part> getParts() {
        return parts;
    }

    @Override
    public Part getPart(String name) {
        return parts.stream()
			.filter( p -> p.getName().equals(name))
			.findFirst()
			.orElse(null);
    }

    // --- unsupported operations ---

    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUserInRole(String role) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLocalAddr() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLocalName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLocalPort() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return Collections.enumeration(Collections.singleton(getLocale()));
    }

    @Override
    public String getProtocol() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRealPath(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean authenticate(HttpServletResponse response) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void login(String pUsername, String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void logout() throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncContext startAsync() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAsyncStarted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAsyncSupported() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String changeSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getContentLengthLong() {
        throw new UnsupportedOperationException();
    }

}
