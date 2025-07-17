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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Mock {@link SlingHttpServletResponse} implementation.
 *
 * @deprecated Use {@link MockSlingJakartaHttpServletResponse} instead.
 */
@ConsumerType
@Deprecated(since = "2.0.0")
public class MockSlingHttpServletResponse extends BaseMockSlingHttpServletResponse implements SlingHttpServletResponse {

    private final ResponseBodySupport bodySupport = new ResponseBodySupport();
    private final CookieSupport cookieSupport = new CookieSupport();

    public MockSlingHttpServletResponse() {
        status = HttpServletResponse.SC_OK;
    }

    @Override
    public void sendRedirect(String location) {
        setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        setHeader("Location", location);
    }

    @Override
    public PrintWriter getWriter() {
        return bodySupport.getWriter(getCharacterEncoding());
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return bodySupport.getOutputStream();
    }

    @Override
    public void reset() {
        if (isCommitted()) {
            throw new IllegalStateException("Response already committed.");
        }
        bodySupport.reset();
        headerSupport.reset();
        cookieSupport.reset();
        status = HttpServletResponse.SC_OK;
        contentLength = 0;
    }

    @Override
    public void resetBuffer() {
        if (isCommitted()) {
            throw new IllegalStateException("Response already committed.");
        }
        bodySupport.reset();
    }

    public byte[] getOutput() {
        return bodySupport.getOutput();
    }

    public String getOutputAsString() {
        return bodySupport.getOutputAsString(getCharacterEncoding());
    }

    @Override
    public void addCookie(Cookie cookie) {
        cookieSupport.addCookie(cookie);
    }

    /**
     * Get cookie
     * @param name Cookie name
     * @return Cookie or null
     */
    public Cookie getCookie(String name) {
        return cookieSupport.getCookie(name);
    }

    /**
     * Get cookies
     * @return Cookies array or null if no cookie defined
     */
    public Cookie[] getCookies() {
        return cookieSupport.getCookies();
    }

    /**
     * @deprecated As of package version 1.6, use {@link #getStatusMessage()} instead.
     *
     * @return status message that was set using {@link #setStatus(int, String)} or {@link #sendError(int, String)}
     */
    @Deprecated(since = "1.6")
    public String geStatusMessage() {
        return this.getStatusMessage();
    }

    // --- unsupported operations ---
    @Override
    public String encodeRedirectUrl(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeUrl(String url) {
        throw new UnsupportedOperationException();
    }
}
