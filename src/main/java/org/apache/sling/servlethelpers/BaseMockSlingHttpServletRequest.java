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

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
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

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.sling.api.adapter.SlingAdaptable;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.request.RequestProgressTracker;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;

/**
 * Mock Sling HttpServletRequest implementation.
 */
public abstract class BaseMockSlingHttpServletRequest extends SlingAdaptable {

    protected static final String PLEASE_PROVDIDE_REQUEST_DISPATCHER_FACTORY =
            "Please provdide a MockRequestDispatcherFactory (setRequestDispatcherFactory).";
    private static final String CHARSET_SEPARATOR = ";charset=";

    private final ResourceResolver resourceResolver;
    private final RequestPathInfo requestPathInfo;
    private Map<String, Object> attributeMap = new HashMap<>();
    private Map<String, MockRequestParameter[]> parameterMap = new LinkedHashMap<>();
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
    private String contentType;
    private String characterEncoding;
    protected byte[] content;
    private String remoteUser;
    private String remoteAddr;
    private String remoteHost;
    private int remotePort;
    private Locale locale = Locale.US;
    protected boolean getInputStreamCalled;
    protected boolean getReaderCalled;

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
    protected BaseMockSlingHttpServletRequest(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
        this.requestPathInfo = newMockRequestPathInfo();
    }

    protected MockRequestPathInfo newMockRequestPathInfo() {
        return new MockRequestPathInfo(this.resourceResolver);
    }

    public ResourceResolver getResourceResolver() {
        return this.resourceResolver;
    }

    public RequestPathInfo getRequestPathInfo() {
        return this.requestPathInfo;
    }

    public Object getAttribute(String name) {
        return this.attributeMap.get(name);
    }

    public Enumeration<String> getAttributeNames() {
        return IteratorUtils.asEnumeration(this.attributeMap.keySet().iterator());
    }

    public void removeAttribute(String name) {
        this.attributeMap.remove(name);
    }

    public void setAttribute(String name, Object object) {
        this.attributeMap.put(name, object);
    }

    public Resource getResource() {
        return this.resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public String getParameter(String name) {
        MockRequestParameter[] params = this.parameterMap.get(name);
        if (params != null && params.length > 0) {
            return params[0].getString();
        }
        return null;
    }

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

    public Enumeration<String> getParameterNames() {
        return IteratorUtils.asEnumeration(this.parameterMap.keySet().iterator());
    }

    public String[] getParameterValues(String name) { // NOPMD
        MockRequestParameter[] param = this.parameterMap.get(name);
        if (param != null) {
            String[] values = new String[param.length];
            for (int i = 0; i < param.length; i++) {
                values[i] = param[i].getString();
            }
            return values;
        }
        return null; // NOPMD NOSONAR
    }

    /**
     * @param parameterMap Map of parameters
     */
    public void setParameterMap(Map<String, Object> parameterMap) {
        this.parameterMap.clear();
        for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String[] array) {
                MockRequestParameter[] values = new MockRequestParameter[array.length];
                for (int i = 0; i < array.length; i++) {
                    values[i] = new MockRequestParameter(key, array[i]);
                }
                this.parameterMap.put(key, values);
            } else if (value instanceof MockRequestParameter[] mrp) {
                this.parameterMap.put(key, mrp);
            } else if (value != null) {
                this.addRequestParameter(key, value.toString());
            } else {
                this.parameterMap.put(key, null);
            }
        }
        this.queryString = formatQueryString(this.parameterMap);
    }

    private static String formatQueryString(Map<String, MockRequestParameter[]> map) {
        StringBuilder querystring = new StringBuilder();
        for (Map.Entry<String, MockRequestParameter[]> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                formatQueryStringParameter(querystring, entry);
            }
        }
        return querystring.length() > 0 ? querystring.toString() : null;
    }

    private static void formatQueryStringParameter(
            StringBuilder querystring, Map.Entry<String, MockRequestParameter[]> entry) {
        for (MockRequestParameter value : entry.getValue()) {
            if (querystring.length() != 0) {
                querystring.append('&');
            }
            querystring.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            querystring.append('=');
            if (value.getString() != null) {
                querystring.append(URLEncoder.encode(value.getString(), StandardCharsets.UTF_8));
            }
        }
    }

    public Locale getLocale() {
        return locale;
    }

    /**
     * @param loc Request locale
     */
    public void setLocale(Locale loc) {
        this.locale = loc;
    }

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
        parseQueryString(this.parameterMap, this.queryString);
    }

    private void parseQueryString(Map<String, MockRequestParameter[]> map, String query) {
        Map<String, List<String>> queryPairs = new LinkedHashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8) : pair;
            queryPairs.computeIfAbsent(key, k -> new ArrayList<>());
            String value = idx > 0 && pair.length() > idx + 1
                    ? URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8)
                    : null;
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

    public String getQueryString() {
        return this.queryString;
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public boolean isSecure() {
        return Strings.CS.equals("https", getScheme());
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public long getDateHeader(String name) {
        return headerSupport.getDateHeader(name);
    }

    public String getHeader(String name) {
        return headerSupport.getHeader(name);
    }

    public Enumeration<String> getHeaderNames() {
        return HeaderSupport.toEnumeration(headerSupport.getHeaderNames());
    }

    public Enumeration<String> getHeaders(String name) {
        return HeaderSupport.toEnumeration(headerSupport.getHeaders(name));
    }

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

    public ResourceBundle getResourceBundle(Locale locale) {
        return getResourceBundle(null, locale);
    }

    public ResourceBundle getResourceBundle(String baseName, Locale locale) { // NOSONAR
        return EMPTY_RESOURCE_BUNDLE;
    }

    public RequestParameter getRequestParameter(String name) {
        MockRequestParameter[] params = this.parameterMap.get(name);
        if (params != null && params.length > 0) {
            return params[0];
        }
        return null;
    }

    public RequestParameterMap getRequestParameterMap() {
        MockRequestParameterMap map = new MockRequestParameterMap();
        for (Map.Entry<String, String[]> entry : getParameterMap().entrySet()) {
            map.put(entry.getKey(), getRequestParameters(entry.getKey()));
        }
        return map;
    }

    public RequestParameter[] getRequestParameters(String name) {
        return this.parameterMap.get(name);
    }

    // part of Sling API 2.7
    public List<RequestParameter> getRequestParameterList() {
        List<RequestParameter> params = new ArrayList<>();
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
            this.parameterMap.put(name, new MockRequestParameter[] {mockRequestParameter});
        }
    }

    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public void setCharacterEncoding(String charset) {
        this.characterEncoding = charset;
    }

    public String getContentType() {
        if (this.contentType == null) {
            return null;
        } else {
            return this.contentType
                    + (StringUtils.isNotBlank(characterEncoding) ? CHARSET_SEPARATOR + characterEncoding : "");
        }
    }

    public void setContentType(String type) {
        this.contentType = type;
        if (Strings.CS.contains(this.contentType, CHARSET_SEPARATOR)) {
            this.characterEncoding = StringUtils.substringAfter(this.contentType, CHARSET_SEPARATOR);
            this.contentType = StringUtils.substringBefore(this.contentType, CHARSET_SEPARATOR);
        }
    }

    public int getContentLength() {
        if (content == null) {
            return 0;
        }
        return content.length;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public String getServletPath() {
        return this.servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public String getPathInfo() {
        if (this.pathInfo != null) {
            return this.pathInfo;
        }

        RequestPathInfo requestPathInfo2 = this.getRequestPathInfo();

        if (StringUtils.isEmpty(requestPathInfo2.getResourcePath())) {
            return null;
        }

        StringBuilder pathInfo2 = new StringBuilder();

        pathInfo2.append(requestPathInfo2.getResourcePath());

        if (StringUtils.isNotEmpty(requestPathInfo2.getSelectorString())) {
            pathInfo2.append('.');
            pathInfo2.append(requestPathInfo2.getSelectorString());
        }

        if (StringUtils.isNotEmpty(requestPathInfo2.getExtension())) {
            pathInfo2.append('.');
            pathInfo2.append(requestPathInfo2.getExtension());
        }

        if (StringUtils.isNotEmpty(requestPathInfo2.getSuffix())) {
            pathInfo2.append(requestPathInfo2.getSuffix());
        }

        return pathInfo2.toString();
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

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

    public StringBuffer getRequestURL() { // NOSONAR
        StringBuffer requestUrl = new StringBuffer(); // NOSONAR

        requestUrl.append(this.getScheme());
        requestUrl.append("://");
        requestUrl.append(getServerName());
        if ((Strings.CS.equals(this.getScheme(), "http") && this.getServerPort() != 80)
                || (Strings.CS.equals(this.getScheme(), "https") && this.getServerPort() != 443)) {
            requestUrl.append(':');
            requestUrl.append(getServerPort());
        }
        requestUrl.append(getRequestURI());

        return requestUrl;
    }

    public String getAuthType() {
        return this.authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    @Override
    public <T> T adaptTo(Class<T> type) {
        return AdaptableUtil.adaptToWithoutCaching(this, type);
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    public Enumeration<String> getResponseContentTypes() {
        return Collections.enumeration(Collections.singleton(responseContentType));
    }

    public BufferedReader getReader() {
        if (getInputStreamCalled) {
            throw new IllegalStateException();
        }
        getReaderCalled = true;
        if (this.content == null) {
            return new BufferedReader(new StringReader(""));
        } else {
            String readerContent;
            try {
                if (characterEncoding == null) {
                    readerContent = new String(this.content, Charset.defaultCharset());
                } else {
                    readerContent = new String(this.content, characterEncoding);
                }
            } catch (UnsupportedEncodingException e) {
                readerContent = new String(this.content, Charset.defaultCharset());
            }
            return new BufferedReader(new StringReader(readerContent));
        }
    }

    public RequestProgressTracker getRequestProgressTracker() {
        return new MockRequestProgressTracker();
    }

    public Principal getUserPrincipal() {
        Principal principal = null;
        // always return null for anonymous user
        final String userid = getRemoteUser();
        if (userid != null) {
            ResourceResolver rr = getResourceResolver();
            if (rr != null) {
                principal = rr.adaptTo(Principal.class);
            }

            if (principal == null) {
                // fallback to the userid
                principal = () -> userid;
            }
        }

        return principal;
    }

    // --- unsupported operations ---

    public String getPathTranslated() {
        throw new UnsupportedOperationException();
    }

    public String getRequestedSessionId() {
        throw new UnsupportedOperationException();
    }

    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException();
    }

    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException();
    }

    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException();
    }

    public boolean isUserInRole(String role) {
        throw new UnsupportedOperationException();
    }

    public String getLocalAddr() {
        throw new UnsupportedOperationException();
    }

    public String getLocalName() {
        throw new UnsupportedOperationException();
    }

    public int getLocalPort() {
        throw new UnsupportedOperationException();
    }

    public Enumeration<Locale> getLocales() {
        return Collections.enumeration(Collections.singleton(getLocale()));
    }

    public String getProtocol() {
        throw new UnsupportedOperationException();
    }

    public String getRealPath(String path) {
        throw new UnsupportedOperationException();
    }

    public void login(String pUsername, String password) {
        throw new UnsupportedOperationException();
    }

    public boolean isAsyncStarted() {
        throw new UnsupportedOperationException();
    }

    public boolean isAsyncSupported() {
        throw new UnsupportedOperationException();
    }

    public String changeSessionId() {
        throw new UnsupportedOperationException();
    }

    public long getContentLengthLong() {
        throw new UnsupportedOperationException();
    }
}
