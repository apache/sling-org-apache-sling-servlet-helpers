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

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.ServletResolver;
import org.jetbrains.annotations.NotNull;

/** Internal request that a Servlet or Script directly,
 *  after resolving it using a ServletResolver.
 *
 *  This bypasses the Servlet Filters used by the default
 *  Sling request processing pipeline, which are often not
 *  needed for internal requests.
 *
 *  That's more efficient than the {@link SlingInternalRequest}
 *  variant, but less faithful to the way Sling processes HTTP
 *  requests.
 */
public class ServletInternalRequest extends InternalRequest {
    protected final ServletResolver servletResolver;
    private final Resource resource;

    /** Setup an internal request to the supplied Resource, using
     *  the supplied servlet/script resolver.
     */
    public ServletInternalRequest(@NotNull ServletResolver servletResolver, @NotNull Resource resource) {
        super(resource.getResourceResolver(), resource.getPath());
        checkNotNull(ServletResolver.class, servletResolver);
        checkNotNull(Resource.class, resource);
        this.resource = resource;
        this.servletResolver = servletResolver;
    }

    /** Return essential request info, used to set the logging MDC  */
    public String toString() {
        return String.format(
                "%s: %s P=%s S=%s EXT=%s RT=%s(%s)",
                getClass().getSimpleName(),
                requestMethod,
                resource.getPath(),
                selectorString,
                extension,
                resource.getResourceType(),
                resource.getResourceSuperType());
    }

    @Override
    protected Resource getExecutionResource() {
        return resource;
    }

    @Override
    protected void delegateExecute(
            SlingHttpServletRequest request, SlingHttpServletResponse response, ResourceResolver resourceResolver)
            throws ServletException, IOException {
        final Servlet s = servletResolver.resolveServlet(request);
        log.debug("ServletResolver provides servlet '{}'", s);
        if (s == null) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Servlet not found by " + getClass().getName());
        } else {
            s.service(request, response);
        }
    }
}
