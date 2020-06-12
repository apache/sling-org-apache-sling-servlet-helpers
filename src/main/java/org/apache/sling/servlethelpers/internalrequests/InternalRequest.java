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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlethelpers.MockRequestPathInfo;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.apache.sling.servlethelpers.MockSlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/** Fluent helper for Sling internal requests. 
 * 
 *  Two modes are supported: "full Sling request processing chain" which
 *  uses a SlingRequestProcessor, and a "direct request to the resolved 
 *  Servlet or Script" mode which is faster but less faithful to the way
 *  Sling processes HTTP requests.
 * 
 *  Both modes use the standard Sling request attributes (resource type
 *  and supertype, HTTP method, selectors, extension) to resolve the
 *  Servlet or Script to use. This allows that powerful resolution mechanism
 *  to be used to other purposes than processing incoming HTTP requests,
 *  like content aggregation, generating query schemas dynamically, etc.
 */
public abstract class InternalRequest {
    protected final ResourceResolver resourceResolver;
    protected final String path;
    protected String selectorString;
    protected String extension;
    protected String requestMethod = DEFAULT_METHOD;
    protected String contentType;
    private Reader bodyReader;
    private boolean explicitStatusCheck;
    private Map<String, Object> parameters = new HashMap<>();
    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public static final String DEFAULT_METHOD = "GET";

    /** An slf4j MDC value is set at this key with request information.
     *  That's useful for debugging when using multiple internal requests
     *  in the context of a single HTTP request
     */
    public static final String MDC_KEY = "sling." + InternalRequest.class.getSimpleName();

    protected InternalRequest(ResourceResolver resourceResolver, String path) {
        this.resourceResolver = resourceResolver;
        this.path = path;
    }

    /** Set the HTTP request method to use - defaults to GET */
    public InternalRequest withRequestMethod(String method) {
        this.requestMethod = method.toUpperCase();
        return this;
    }

    /** Set the HTTP request method to use - defaults to GET */
    public InternalRequest withContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    /** Use the supplied Reader as the request's body content */
    public InternalRequest withBody(Reader bodyContent) {
        bodyReader = bodyContent;
        return this;
    }

    /** Sets the optional selectors of the internal request, which influence
     *  the Servlet/Script resolution.
     */
    public InternalRequest withSelectors(String ... selectors) {
        if(selectors == null) {
            return this;
        }
        StringBuilder sb = new StringBuilder();
        Arrays.stream(selectors).forEach(sel -> sb.append(sb.length() == 0 ? "" : ".").append(sel));
        selectorString = sb.toString();
        return this;
    }

    /** Sets the optional extension of the internal request, which influence
     *  the Servlet/Script resolution.
     */
    public InternalRequest withExtension(String extension) {
        this.extension = extension;
        return this;
    }

    /** Set a request parameter */
    public InternalRequest withParameter(String key, Object value) {
        if(key != null && value != null) {
            parameters.put(key, value);
        } else {
            throw new IllegalArgumentException("Null key or value");
        }
        return this;
    }

    /** Add the supplied request parameters to the current ones */
    public InternalRequest withParameters(Map<String, Object> additionalParameters) {
        if(additionalParameters != null) {
            parameters.putAll(additionalParameters);
        };
        return this;
    }

    /** Execute the internal request. Can be called right after
     *  creating it, if not options need to be set.
     * 
     *  @throws IOException if the request was already executed,
     *      if an error occurs during execution or if the
     *      response status doesn't match the provided value
     * 
     *  @param expectedResponseStatus a negative value means "do not check the status"
     */
    public final InternalRequest execute() throws IOException {
        if(request != null) {
            throw new IOException("Request was already executed");
        }
        final Resource resource = getExecutionResource();
        request = new MockSlingHttpServletRequest(resourceResolver) {
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
                if(bodyReader != null) {
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

        response = new MockSlingHttpServletResponse();

        MDC.put(MDC_KEY, toString());
        try {
            delegateExecute(request, response, resourceResolver);
        } catch(ServletException sx) {
            throw new IOException("ServletException in execute()", sx);
        }
        return this;
    }

    /** Return the Resource to use to execute the request */
    protected abstract Resource getExecutionResource();

    /** Execute the supplied Request */
    protected abstract void delegateExecute(SlingHttpServletRequest request, SlingHttpServletResponse response, ResourceResolver resourceResolver)
    throws ServletException, IOException;

    protected void assertRequestExecuted() throws IOException {
        if(request == null) {
            throw new IOException("Request hasn't been executed");
        }
    }

    /** After executing the request, checks that the request status is one of the supplied values.
     *  If this not called before methods that access the response, a check for a 200 OK status
     *  is done automatically, to make sure client's don't forget to check it.
     *  @param acceptableValues providing no values means "don't care"
     *  @throws IOException if status doesn't match any of these values
     */
    public InternalRequest checkStatus(int ... acceptableValues) throws IOException {
        assertRequestExecuted();
        explicitStatusCheck = true;

        if(acceptableValues == null || acceptableValues.length == 0) {
            return this;
        }

        final int actualStatus = getStatus();
        final OptionalInt found = Arrays.stream(acceptableValues).filter(expected -> expected == actualStatus).findFirst();
        if(!found.isPresent()) {
            final StringBuilder sb = new StringBuilder();
            Arrays.stream(acceptableValues).forEach(val -> sb.append(sb.length() == 0 ? "" : ",").append(val));
            throw new IOException("Unexpected response status " + actualStatus + ", expected one of '" + sb + "'");
        }
         return this;
    }

    /** If response status hasn't been explicitly checked, ensure it's 200 */
    private void maybeCheckOkStatus() throws IOException {
        if(!explicitStatusCheck) {
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
    public InternalRequest checkResponseContentType(String contentType) throws IOException {
        assertRequestExecuted();
        if(!contentType.equals(response.getContentType())) {
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
     *  @throws IOException if the request hasn't been executed yet
     */
    public SlingHttpServletResponse getResponse() throws IOException {
        assertRequestExecuted();
        maybeCheckOkStatus();
        return response;
    }

    /** Return the response as a String. The execute method must be called before this one.
     *  @throws IOException if the request hasn't been executed yet
     */
    public String getResponseAsString() throws IOException {
        assertRequestExecuted();
        maybeCheckOkStatus();
        return response.getOutputAsString();
    }
}