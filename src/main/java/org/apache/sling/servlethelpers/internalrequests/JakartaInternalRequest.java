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
package org.apache.sling.servlethelpers.internalrequests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.sling.api.SlingJakartaHttpServletRequest;
import org.apache.sling.api.SlingJakartaHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlethelpers.MockRequestPathInfo;
import org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest;
import org.apache.sling.servlethelpers.MockSlingJakartaHttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;

/** Fluent helper for Sling internal requests.
 *
 *  The {@link ServletInternalRequest} and {@link SlingInternalRequest}
 *  subclasses provide two modes for executing the
 *  internal requests, one that's very similar to the way Sling
 *  executes an HTTP request and another one that's faster by
 *  calling Servlets or Scripts directly.
 */
public abstract class JakartaInternalRequest extends BaseInternalRequest {
    private MockSlingJakartaHttpServletRequest request;
    private MockSlingJakartaHttpServletResponse response;

    /** Clients use subclasses of this one  */
    protected JakartaInternalRequest(@NotNull ResourceResolver resourceResolver, @NotNull String path) {
        super(resourceResolver, path);
    }

    @Override
    public JakartaInternalRequest withRequestMethod(String method) {
        return (JakartaInternalRequest) super.withRequestMethod(method);
    }

    @Override
    public JakartaInternalRequest withContentType(String contentType) {
        return (JakartaInternalRequest) super.withContentType(contentType);
    }

    @Override
    public JakartaInternalRequest withBody(Reader bodyContent) {
        return (JakartaInternalRequest) super.withBody(bodyContent);
    }

    @Override
    public JakartaInternalRequest withSelectors(String... selectors) {
        return (JakartaInternalRequest) super.withSelectors(selectors);
    }

    @Override
    public JakartaInternalRequest withExtension(String extension) {
        return (JakartaInternalRequest) super.withExtension(extension);
    }

    @Override
    public JakartaInternalRequest withParameter(String key, Object value) {
        return (JakartaInternalRequest) super.withParameter(key, value);
    }

    @Override
    public JakartaInternalRequest withParameters(Map<String, Object> additionalParameters) {
        return (JakartaInternalRequest) super.withParameters(additionalParameters);
    }

    /** Execute the internal request. Can be called right after
     *  creating it, if no options need to be set.
     *
     *  @throws IOException if the request was already executed,
     *      or if an error occurs during execution.
     */
    public final JakartaInternalRequest execute() throws IOException {
        if (request != null) {
            throw new IOException("Request was already executed");
        }
        final Resource resource = getExecutionResource();
        request = new MockSlingJakartaHttpServletRequest(resourceResolver) {
            @Override
            protected MockRequestPathInfo newMockRequestPathInfo() {
                MockRequestPathInfo rpi = super.newMockRequestPathInfo();
                rpi.setResourcePath(path);
                rpi.setExtension(extension);
                rpi.setSelectorString(selectorString);
                return rpi;
            }

            @Override
            public BufferedReader getReader() {
                if (bodyReader != null) {
                    return new BufferedReader(bodyReader);
                } else {
                    return super.getReader();
                }
            }
        };
        request.setMethod(requestMethod);
        request.setContentType(contentType);
        request.setResource(resource);
        request.setParameterMap(parameters);

        response = new MockSlingJakartaHttpServletResponse();

        MDC.put(MDC_KEY, toString());
        try {
            delegateExecute(request, response, resourceResolver);
        } catch (ServletException sx) {
            throw new IOException("ServletException in execute()", sx);
        }
        return this;
    }

    /** Execute the supplied Request */
    protected abstract void delegateExecute(
            SlingJakartaHttpServletRequest request,
            SlingJakartaHttpServletResponse response,
            ResourceResolver resourceResolver)
            throws ServletException, IOException;

    protected void assertRequestExecuted() throws IOException {
        if (request == null) {
            throw new IOException("Request hasn't been executed");
        }
    }

    @Override
    public JakartaInternalRequest checkStatus(int... acceptableValues) throws IOException {
        return (JakartaInternalRequest) super.checkStatus(acceptableValues);
    }

    /** If response status hasn't been explicitly checked, ensure it's 200 */
    private void maybeCheckOkStatus() throws IOException {
        if (!explicitStatusCheck) {
            try {
                checkStatus(HttpServletResponse.SC_OK);
            } finally {
                explicitStatusCheck = false;
            }
        }
    }

    /** After executing the request, checks that the response content-type
     *  is as expected.
     *
     *  @throws IOException if the actual content-type doesn't match the expected one
     */
    public JakartaInternalRequest checkResponseContentType(String contentType) throws IOException {
        assertRequestExecuted();
        if (!contentType.equals(response.getContentType())) {
            throw new IOException("Expected content type " + contentType + " but got " + response.getContentType());
        }
        return this;
    }

    /** Return the response status. The execute method must be called before this one.
     *  @throws IOException if the request hasn't been executed yet
     */
    public int getStatus() throws IOException {
        assertRequestExecuted();
        return response.getStatus();
    }

    /** Return the response object. The execute method must be called before this one.
     *  A check for "200 OK" status is done automatically unless {@link #checkStatus} has
     *  been called before.
     *
     *  @throws IOException if the request hasn't been executed yet or if the status
     *      check fails.
     */
    public SlingJakartaHttpServletResponse getResponse() throws IOException {
        assertRequestExecuted();
        maybeCheckOkStatus();
        return response;
    }

    /** Return the response as a String. The execute method must be called before this one.
     *
     *  A check for "200 OK" status is done automatically unless {@link #checkStatus} has
     *  been called before.
     *
     *  @throws IOException if the request hasn't been executed yet or if the status
     *      check fails.
     */
    public String getResponseAsString() throws IOException {
        assertRequestExecuted();
        maybeCheckOkStatus();
        return response.getOutputAsString();
    }
}
