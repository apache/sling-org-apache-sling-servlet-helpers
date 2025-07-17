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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Mock {@link SlingHttpServletRequest} implementation.
 *
 * @deprecated Use {@link MockSlingJakartaHttpServletRequest} instead.
 */
@ConsumerType
@Deprecated(since = "2.0.0")
public class MockSlingHttpServletRequest extends BaseMockSlingHttpServletRequest implements SlingHttpServletRequest {

    private HttpSession session;
    private final CookieSupport cookieSupport = new CookieSupport();
    private List<Part> parts = new ArrayList<>();

    private MockRequestDispatcherFactory requestDispatcherFactory;

    /**
     * @param resourceResolver Resource resolver
     */
    public MockSlingHttpServletRequest(ResourceResolver resourceResolver) {
        super(resourceResolver);
    }

    protected MockHttpSession newMockHttpSession() {
        return new MockHttpSession();
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
    public ServletInputStream getInputStream() {
        if (getReaderCalled) {
            throw new IllegalStateException();
        }
        getInputStreamCalled = true;
        return new ServletInputStream() {
            private final InputStream is =
                    content == null ? new ByteArrayInputStream(new byte[0]) : new ByteArrayInputStream(content);

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

    public void setRequestDispatcherFactory(MockRequestDispatcherFactory requestDispatcherFactory) {
        this.requestDispatcherFactory = requestDispatcherFactory;
    }

    public void addPart(Part part) {
        if (part == null) throw new IllegalArgumentException("part may not be null");
        this.parts.add(part);
    }

    @Override
    public Collection<Part> getParts() {
        return parts;
    }

    @Override
    public Part getPart(String name) {
        return parts.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }

    // --- unsupported operations ---

    @Override
    public boolean authenticate(HttpServletResponse response) {
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
    public AsyncContext getAsyncContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        throw new UnsupportedOperationException();
    }

    // --- unsupported operations ---

    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException();
    }
}
