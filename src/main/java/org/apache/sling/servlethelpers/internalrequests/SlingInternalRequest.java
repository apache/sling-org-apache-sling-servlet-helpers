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

import javax.servlet.ServletException;

import java.io.IOException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.SlingRequestProcessor;
import org.jetbrains.annotations.NotNull;

/** Internal request that uses a SlingRequestProcessor.
 *  This executes the complete Sling request processing
 *  pipeline.
 *
 *  That's the same processing than Sling uses
 *  for HTTP requests, but it's not as efficient as the
 *  {@link ServletInternalRequest} which resolves and
 *  calls a Servlet or Script directly.
 *
 *  This variant of internal requests is useful when no
 *  Resource is available, as it builds its own Resource
 *  based on the supplied parameters to drive the
 *  Servlet/Script resolution mechanism.
 */
public class SlingInternalRequest extends InternalRequest {
    private final SlingRequestProcessor processor;
    private String resourceType;
    private String resourceSuperType;

    /** Setup an internal request that uses a SlingRequestProcessor */
    public SlingInternalRequest(
            @NotNull ResourceResolver resourceResolver, @NotNull SlingRequestProcessor p, @NotNull String path) {
        super(resourceResolver, path);
        checkNotNull(SlingRequestProcessor.class, p);
        this.processor = p;
    }

    /** Return essential request info, used to set the logging MDC  */
    public String toString() {
        return String.format(
                "%s: %s P=%s S=%s EXT=%s RT=%s(%s)",
                getClass().getSimpleName(),
                requestMethod,
                path,
                selectorString,
                extension,
                resourceType,
                resourceSuperType);
    }

    /** Sets the sling:resourceSuperType of the fake Resource used to resolve
     *  the Script or Servlet used for the internal request */
    public SlingInternalRequest withResourceSuperType(String resourceSuperType) {
        this.resourceSuperType = resourceSuperType;
        return this;
    }

    /** Sets the sling:resourceType of the fake Resource used to resolve
     *  the Script or Servlet used for the internal request */
    public SlingInternalRequest withResourceType(String resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    @Override
    protected void delegateExecute(
            SlingHttpServletRequest request, SlingHttpServletResponse response, ResourceResolver resourceResolver)
            throws ServletException, IOException {
        log.debug("Executing request using a SlingRequestProcessor");
        processor.processRequest(request, response, resourceResolver);
    }

    @Override
    protected Resource getExecutionResource() {
        return new ServletResolutionResource(resourceResolver, path, resourceType, resourceSuperType);
    }
}
