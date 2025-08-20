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

import javax.servlet.http.Cookie;

import java.util.Arrays;

import org.apache.felix.http.javaxwrappers.CookieWrapper;
import org.apache.sling.api.wrappers.JakartaToJavaxResponseWrapper;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Mock {@link org.apache.sling.api.SlingHttpServletResponse} implementation.
 *
 * @deprecated Use {@link MockSlingJakartaHttpServletResponse} instead.
 */
@ConsumerType
@Deprecated(since = "2.0.0")
public class MockSlingHttpServletResponse extends JakartaToJavaxResponseWrapper {
    private MockSlingJakartaHttpServletResponse wrappedResponse;

    public MockSlingHttpServletResponse(MockSlingJakartaHttpServletResponse wrappedResponse) {
        super(wrappedResponse);
        this.wrappedResponse = wrappedResponse;
    }

    public int getContentLength() {
        return this.wrappedResponse.getContentLength();
    }

    public byte[] getOutput() {
        return this.wrappedResponse.getOutput();
    }

    public String getOutputAsString() {
        return this.wrappedResponse.getOutputAsString();
    }

    @SuppressWarnings({"java:S2092", "java:S3330"})
    public Cookie getCookie(String name) {
        return new CookieWrapper(this.wrappedResponse.getCookie(name));
    }

    public Cookie[] getCookies() {
        jakarta.servlet.http.Cookie[] cookies = this.wrappedResponse.getCookies();
        if (cookies == null) {
            return null; // NOSONAR
        } else {
            return Arrays.stream(cookies).map(CookieWrapper::new).toArray(Cookie[]::new);
        }
    }

    public String getStatusMessage() {
        return this.wrappedResponse.getStatusMessage();
    }

    @Override
    public void setStatus(final int sc, final String sm) {
        this.wrappedResponse.sendError(sc, sm);
    }

    @Override
    public <T> T adaptTo(Class<T> type) {
        return AdaptableUtil.adaptToWithoutCaching(this, type);
    }
}
