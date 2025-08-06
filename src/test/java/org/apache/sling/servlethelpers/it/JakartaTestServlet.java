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
package org.apache.sling.servlethelpers.it;

import java.io.IOException;

import jakarta.servlet.Servlet;
import org.apache.sling.api.SlingJakartaHttpServletRequest;
import org.apache.sling.api.SlingJakartaHttpServletResponse;
import org.apache.sling.api.servlets.SlingJakartaAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

/** Not all servlets can be called directly from a {@link JakartaServletInternalRequest},
 *  depending on the environment that they expect, request attributes etc.
 *
 *  This is a test one with zero environment requirements.
 */
@Component(
        service = Servlet.class,
        property = {"sling.servlet.resourceTypes=sling/servlet/default", "sling.servlet.extensions=JakartaTestServlet"})
public class JakartaTestServlet extends SlingJakartaAllMethodsServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(SlingJakartaHttpServletRequest request, SlingJakartaHttpServletResponse response)
            throws IOException {
        response.getWriter().write(getClass().getName());
    }
}
