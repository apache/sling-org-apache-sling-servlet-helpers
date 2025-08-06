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

import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.felix.http.jakartawrappers.CookieWrapper;
import org.apache.felix.http.jakartawrappers.PartWrapper;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import static org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest.PLEASE_PROVDIDE_REQUEST_DISPATCHER_FACTORY;

/**
 * Mock {@link SlingHttpServletRequest} implementation.
 *
 * @deprecated Use {@link MockSlingJakartaHttpServletRequest} instead.
 */
@ConsumerType
@Deprecated(since = "2.0.0")
public class MockSlingHttpServletRequest extends JakartaToJavaxRequestWrapper {
    private MockSlingJakartaHttpServletRequest wrappedRequest;
    private MockRequestDispatcherFactory requestDispatcherFactory;

    protected static final ResourceBundle EMPTY_RESOURCE_BUNDLE = new ListResourceBundle() {
        @Override
        protected Object[][] getContents() {
            return new Object[0][0];
        }
    };

    public MockSlingHttpServletRequest(MockSlingJakartaHttpServletRequest wrappedRequest) {
        super(wrappedRequest);
        this.wrappedRequest = wrappedRequest;
    }

    protected MockHttpSession newMockHttpSession() {
        return new MockHttpSession(this.wrappedRequest.newMockHttpSession());
    }

    protected MockRequestPathInfo newMockRequestPathInfo() {
        return new MockRequestPathInfo(this.wrappedRequest.getResourceResolver());
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public HttpSession getSession(boolean create) {
        final jakarta.servlet.http.HttpSession session = this.wrappedRequest.getSession(create);
        if (session != null) {
            return new MockHttpSession((MockJakartaHttpSession) session);
        }
        return null;
    }

    public void setResource(Resource resource) {
        this.wrappedRequest.setResource(resource);
    }

    public void setParameterMap(Map<String, Object> parameterMap) {
        this.wrappedRequest.setParameterMap(parameterMap);
    }

    public void setLocale(Locale loc) {
        this.wrappedRequest.setLocale(loc);
    }

    public void setContextPath(String contextPath) {
        this.wrappedRequest.setContextPath(contextPath);
    }

    public void setQueryString(String queryString) {
        this.wrappedRequest.setQueryString(queryString);
    }

    public void setScheme(String scheme) {
        this.wrappedRequest.setScheme(scheme);
    }

    public void setServerName(String serverName) {
        this.wrappedRequest.setServerName(serverName);
    }

    public void setServerPort(int serverPort) {
        this.wrappedRequest.setServerPort(serverPort);
    }

    public void setMethod(String method) {
        this.wrappedRequest.setMethod(method);
    }

    public void addHeader(String name, String value) {
        this.wrappedRequest.addHeader(name, value);
    }

    public void addIntHeader(String name, int value) {
        this.wrappedRequest.addIntHeader(name, value);
    }

    public void addDateHeader(String name, long date) {
        this.wrappedRequest.addDateHeader(name, date);
    }

    public void setHeader(String name, String value) {
        this.wrappedRequest.setHeader(name, value);
    }

    public void setIntHeader(String name, int value) {
        this.wrappedRequest.setIntHeader(name, value);
    }

    public void setDateHeader(String name, long date) {
        this.wrappedRequest.setDateHeader(name, date);
    }

    @SuppressWarnings({"java:S2092", "java:S3330"})
    public void addCookie(Cookie cookie) {
        this.wrappedRequest.addCookie(new CookieWrapper(cookie));
    }

    public void addRequestParameter(String name, String value) {
        this.wrappedRequest.addRequestParameter(name, value);
    }

    public void addRequestParameter(String name, byte[] content, String contentType) {
        this.wrappedRequest.addRequestParameter(name, content, contentType);
    }

    public void addRequestParameter(String name, byte[] content, String contentType, String filename) {
        this.wrappedRequest.addRequestParameter(name, content, contentType, filename);
    }

    public void setContentType(String type) {
        this.wrappedRequest.setContentType(type);
    }

    public void setContent(byte[] content) {
        this.wrappedRequest.setContent(content);
    }

    public void setRequestDispatcherFactory(MockRequestDispatcherFactory requestDispatcherFactory) {
        this.requestDispatcherFactory = requestDispatcherFactory;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        if (requestDispatcherFactory == null) {
            throw new IllegalStateException(PLEASE_PROVDIDE_REQUEST_DISPATCHER_FACTORY);
        }
        return requestDispatcherFactory.getRequestDispatcher(path, null);
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path, RequestDispatcherOptions options) {
        if (requestDispatcherFactory == null) {
            throw new IllegalStateException(PLEASE_PROVDIDE_REQUEST_DISPATCHER_FACTORY);
        }
        return requestDispatcherFactory.getRequestDispatcher(path, options);
    }

    @Override
    public RequestDispatcher getRequestDispatcher(Resource resource) {
        if (requestDispatcherFactory == null) {
            throw new IllegalStateException(PLEASE_PROVDIDE_REQUEST_DISPATCHER_FACTORY);
        }
        return requestDispatcherFactory.getRequestDispatcher(resource, null);
    }

    @Override
    public RequestDispatcher getRequestDispatcher(Resource resource, RequestDispatcherOptions options) {
        if (requestDispatcherFactory == null) {
            throw new IllegalStateException(PLEASE_PROVDIDE_REQUEST_DISPATCHER_FACTORY);
        }
        return requestDispatcherFactory.getRequestDispatcher(resource, options);
    }

    public void setRemoteUser(String remoteUser) {
        this.wrappedRequest.setRemoteUser(remoteUser);
    }

    public void setRemoteAddr(String remoteAddr) {
        this.wrappedRequest.setRemoteAddr(remoteAddr);
    }

    public void setRemoteHost(String remoteHost) {
        this.wrappedRequest.setRemoteHost(remoteHost);
    }

    public void setRemotePort(int remotePort) {
        this.wrappedRequest.setRemotePort(remotePort);
    }

    public void setServletPath(String servletPath) {
        this.wrappedRequest.setServletPath(servletPath);
    }

    public void setPathInfo(String pathInfo) {
        this.wrappedRequest.setPathInfo(pathInfo);
    }

    public void setAuthType(String authType) {
        this.wrappedRequest.setAuthType(authType);
    }

    public void setResponseContentType(String responseContentType) {
        this.wrappedRequest.setResponseContentType(responseContentType);
    }

    public void addPart(Part part) {
        PartWrapper wrappedPart = part == null ? null : new PartWrapper(part);
        this.wrappedRequest.addPart(wrappedPart);
    }

    @Override
    public boolean authenticate(HttpServletResponse response) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull RequestPathInfo getRequestPathInfo() {
        return this.wrappedRequest.getRequestPathInfo();
    }

    @Override
    public <T> T adaptTo(Class<T> type) {
        return AdaptableUtil.adaptToWithoutCaching(this, type);
    }
}
