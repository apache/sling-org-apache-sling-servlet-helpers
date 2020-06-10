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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

class RequestInfoServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 1L;
    private final static String PREFIX = "TEST_";
    private final String resolutionInfo;

    RequestInfoServlet(SlingHttpServletRequest resolutionRequest) {
        final StringBuilder sb = new StringBuilder();

        // Verify that we get the usual Sling request
        // attributes in the resolution call
        final RequestPathInfo rpi = resolutionRequest.getRequestPathInfo();
        sb.append("M_").append(resolutionRequest.getMethod());
        sb.append(" PI_").append(resolutionRequest.getPathInfo());
        sb.append(" RPI_EXT_").append(rpi.getExtension());
        sb.append(" RPI_SEL_").append(rpi.getSelectorString());
        sb.append(" RPI_P_").append(rpi.getResourcePath());
        if(resolutionRequest.getResource() != null) {
            sb.append(" RT_").append(resolutionRequest.getResource().getResourceType());
            sb.append(" RST_").append(resolutionRequest.getResource().getResourceSuperType());
        }
        sb.append(" RRA_").append(resolutionRequest.getResource().getResourceResolver().getAttribute("testAttribute"));

        resolutionInfo = sb.toString();
    }

    @Override
    public void service(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        if(request.getMethod().equals("EXCEPTION")) {
            throw new IOException("Failing as designed");
        }
        if(request.getMethod().equals("STATUS")) {
            response.sendError(451);
            return;
        }
        response.setContentType(PREFIX + request.getContentType());
        response.getWriter().write(resolutionInfo);
        response.setContentType("CT_" + request.getContentType());
    }
}