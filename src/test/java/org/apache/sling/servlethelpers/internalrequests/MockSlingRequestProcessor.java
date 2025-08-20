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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.apache.sling.api.SlingJakartaHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.SlingRequestProcessor;

class MockSlingRequestProcessor implements SlingRequestProcessor {

    /**
     * @deprecated Use {@link #resolve(SlingJakartaHttpServletRequest)} instead.
     */
    @Deprecated(since = "2.0.0")
    @Override
    public void processRequest(
            HttpServletRequest httpRequest, HttpServletResponse response, ResourceResolver resourceResolver)
            throws ServletException, IOException {
        final org.apache.sling.api.SlingHttpServletRequest request =
                (org.apache.sling.api.SlingHttpServletRequest) httpRequest;
        if (request.getResource() != null
                && "/NOSERVLET".equals(request.getResource().getPath())) {
            response.sendError(404);
        } else {
            new RequestInfoServlet((org.apache.sling.api.SlingHttpServletRequest) request).service(request, response);
        }
    }

    @Override
    public void processRequest(
            jakarta.servlet.http.HttpServletRequest httpRequest,
            jakarta.servlet.http.HttpServletResponse response,
            ResourceResolver resourceResolver)
            throws IOException {
        final SlingJakartaHttpServletRequest request = (SlingJakartaHttpServletRequest) httpRequest;
        if (request.getResource() != null
                && "/NOSERVLET".equals(request.getResource().getPath())) {
            response.sendError(404);
        } else {
            try {
                new JakartaRequestInfoServlet((SlingJakartaHttpServletRequest) request).service(request, response);
            } catch (jakarta.servlet.ServletException e) {
                throw new IOException(e);
            }
        }
    }
}
