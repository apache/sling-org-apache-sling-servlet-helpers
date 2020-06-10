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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.ServletResolver;
import org.apache.sling.engine.SlingRequestProcessor;
import org.apache.sling.servlethelpers.MockRequestPathInfo;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.apache.sling.servlethelpers.MockSlingHttpServletResponse;
import org.jetbrains.annotations.NotNull;

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
    protected final String path;
    private String selectorString;
    private String extension;
    private String requestMethod;
    private String resourceType;
    private String resourceSuperType;
    private String contentType;
    private Reader bodyReader;
    private Map<String, Object> parameters = new HashMap<>();
    private final ResourceResolver resourceResolver;
    private MockSlingHttpServletRequest request;
    private MockSlingHttpServletResponse response;

    public static final String DEFAULT_METHOD = "GET";

    protected InternalRequest(ResourceResolver resourceResolver, String path) {
        this.resourceResolver = resourceResolver;
        this.path = path;
        this.requestMethod = DEFAULT_METHOD;
    }

    /** Start preparing an internal request that uses the "SlingRequest"Processor mode.
     * 
     * @param resourceResolver Used for access control
     * @param processor The SlingRequestProcessor to use for processing
     * @param path The path of the request
     * @return a fluent InternalRequest 
     */
    @NotNull
    public static InternalRequest slingRequest(
        @NotNull ResourceResolver resourceResolver, 
        @NotNull SlingRequestProcessor processor,
        @NotNull String path) {
        return new SlingInternalRequest(resourceResolver, processor, path);
    }

    /** Start preparing an internal request that calls the resolved Servlet
     *  directly. This bypasses the Servlet Filters used by the default
     *  Sling request processing pipeline, which are often not needed
     *  for internal requests.
     * 
     * @param resourceResolver Used for access control
     * @param servletResolver Resolves the Servlet or Script used to process the internal request
     * @param path The path of the request
     * @return a fluent InternalRequest 
     */
    public static InternalRequest servletRequest(ResourceResolver resourceResolver, ServletResolver servletResolver, String path) {
        return new ServletInternalRequest(resourceResolver, servletResolver, path);
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

    /** Sets the sling:resourceSuperType of the fake Resource used to resolve
     *  the Script or Servlet to use for the internal request */
    public InternalRequest withResourceSuperType(String resourceSuperType) {
        this.resourceSuperType = resourceSuperType;
        return this;
    }

    /** Sets the sling:resourceType of the fake Resource used to resolve
     *  the Script or Servlet to use for the internal request */
    public InternalRequest withResourceType(String resourceType) {
        this.resourceType = resourceType;
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

    /** Call the other execute() method, expecting a response with status 200 OK */
    public InternalRequest execute() throws IOException {
        return execute(HttpServletResponse.SC_OK);
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
    public final InternalRequest execute(int expectedResponseStatus) throws IOException {
        if(request != null) {
            throw new IOException("Request was already executed");
        }
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
        request.setResource(new MockResource(resourceResolver, path, resourceType, resourceSuperType));
        request.setParameterMap(parameters);

        response = new MockSlingHttpServletResponse();
        try {
            delegateExecute(request, response, resourceResolver);
        } catch(ServletException sx) {
            throw new IOException("ServletException in execute()", sx);
        }

        if(expectedResponseStatus > 0 && (expectedResponseStatus != response.getStatus())) {
            throw new IOException("Expected response status " + expectedResponseStatus + " but got " + response.getStatus());
        }

        return this;
    }

    protected abstract void delegateExecute(SlingHttpServletRequest request, SlingHttpServletResponse response, ResourceResolver resourceResolver)
    throws ServletException, IOException;

    protected void assertRequestExecuted() throws IOException {
        if(request == null) {
            throw new IOException("Request hasn't been executed");
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

    /** Return the response object. The execute method must be called before this one.
     *  @throws IOException if the request hasn't been executed yet
     */
    public SlingHttpServletResponse getResponse() throws IOException {
        assertRequestExecuted();
        return response;
    }

    /** Return the response as a String. The execute method must be called before this one.
     *  @throws IOException if the request hasn't been executed yet
     */
    public String getResponseAsString() throws IOException {
        assertRequestExecuted();
        return response.getOutputAsString();
    }
}