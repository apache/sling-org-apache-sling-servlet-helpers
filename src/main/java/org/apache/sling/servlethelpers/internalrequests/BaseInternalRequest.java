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

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Fluent helper for Sling internal requests.
 *
 *  The {@link ServletInternalRequest} and {@link SlingInternalRequest}
 *  subclasses provide two modes for executing the
 *  internal requests, one that's very similar to the way Sling
 *  executes an HTTP request and another one that's faster by
 *  calling Servlets or Scripts directly.
 */
public abstract class BaseInternalRequest {
    protected final ResourceResolver resourceResolver;
    protected final String path;
    protected String selectorString;
    protected String extension;
    protected String requestMethod = DEFAULT_METHOD;
    protected String contentType;
    protected Reader bodyReader;
    protected boolean explicitStatusCheck;
    protected Map<String, Object> parameters = new HashMap<>();

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public static final String DEFAULT_METHOD = "GET";

    /** An slf4j MDC value is set at this key with request information.
     *  That's useful for troubleshooting when using multiple internal
     *  requests in the context of a single HTTP request.
     */
    public static final String MDC_KEY = "sling." + BaseInternalRequest.class.getSimpleName();

    /** Clients use subclasses of this one  */
    protected BaseInternalRequest(@NotNull ResourceResolver resourceResolver, @NotNull String path) {
        checkNotNull(ResourceResolver.class, resourceResolver);
        checkNotNull("path", path);
        this.resourceResolver = resourceResolver;
        this.path = path;
    }

    protected void checkNotNull(String info, Object candidate) {
        if (candidate == null) {
            throw new IllegalArgumentException(info + " is null");
        }
    }

    protected void checkNotNull(Class<?> clazz, Object candidate) {
        checkNotNull(clazz.getSimpleName(), candidate);
    }

    /** Set the HTTP request method to use - defaults to GET */
    public BaseInternalRequest withRequestMethod(String method) {
        this.requestMethod = method.toUpperCase();
        return this;
    }

    /** Set the HTTP request's Content-Type */
    public BaseInternalRequest withContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    /** Use the supplied Reader as the request's body content */
    public BaseInternalRequest withBody(Reader bodyContent) {
        bodyReader = bodyContent;
        return this;
    }

    /** Sets the optional selectors of the internal request, which influence
     *  the Servlet/Script resolution.
     */
    public BaseInternalRequest withSelectors(String... selectors) {
        if (selectors == null) {
            return this;
        }
        StringBuilder sb = new StringBuilder();
        Arrays.stream(selectors)
                .forEach(sel -> sb.append(sb.length() == 0 ? "" : ".").append(sel));
        selectorString = sb.toString();
        return this;
    }

    /** Sets the optional extension of the internal request, which influence
     *  the Servlet/Script resolution.
     */
    public BaseInternalRequest withExtension(String extension) {
        this.extension = extension;
        return this;
    }

    /** Set a request parameter */
    public BaseInternalRequest withParameter(String key, Object value) {
        if (key != null && value != null) {
            parameters.put(key, value);
        } else {
            throw new IllegalArgumentException("Null key or value");
        }
        return this;
    }

    /** Add the supplied request parameters to the current ones */
    public BaseInternalRequest withParameters(Map<String, Object> additionalParameters) {
        if (additionalParameters != null) {
            parameters.putAll(additionalParameters);
        }

        return this;
    }

    public abstract BaseInternalRequest execute() throws IOException;

    /** Provide the Resource to use to execute the request */
    protected abstract Resource getExecutionResource();

    protected abstract void assertRequestExecuted() throws IOException;

    /** After executing the request, checks that the response content-type
     *  is as expected.
     *
     *  @throws IOException if the actual content-type doesn't match the expected one
     */
    public abstract BaseInternalRequest checkResponseContentType(String contentType) throws IOException;

    /** After executing the request, checks that the request status is one
     *  of the supplied values.
     *
     *  If this is not called before methods that access the response, a check
     *  for a 200 OK status is done automatically unless this was called
     *  with no arguments before.
     *
     *  This makes sure a status check is done or explicitly disabled.
     *
     *  @param acceptableValues providing no values means "don't care"
     *  @throws IOException if status doesn't match any of these values
     */
    public BaseInternalRequest checkStatus(int... acceptableValues) throws IOException {
        assertRequestExecuted();
        explicitStatusCheck = true;

        if (acceptableValues == null || acceptableValues.length == 0) {
            return this;
        }

        final int actualStatus = getStatus();
        final OptionalInt found = Arrays.stream(acceptableValues)
                .filter(expected -> expected == actualStatus)
                .findFirst();
        if (!found.isPresent()) {
            String sb = Arrays.stream(acceptableValues)
                    .mapToObj(String::valueOf) // Convert each int to its String representation
                    .collect(Collectors.joining(",")); // Join the strings with a comma delimiter
            throw new IOException("Unexpected response status " + actualStatus + ", expected one of '" + sb + "'");
        }
        return this;
    }

    /** Return the response status. The execute method must be called before this one.
     *  @throws IOException if the request hasn't been executed yet
     */
    public abstract int getStatus() throws IOException;

    public abstract String getResponseAsString() throws IOException;
}
