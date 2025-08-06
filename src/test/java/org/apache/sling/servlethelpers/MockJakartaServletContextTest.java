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

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.EventListener;
import java.util.Set;

import jakarta.servlet.Filter;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.SessionTrackingMode;
import org.apache.sling.servlethelpers.it.JakartaTestServlet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class MockJakartaServletContextTest {

    private static final class TestEventListener implements EventListener {}

    private ServletContext servletContext;

    @Before
    public void setUp() {
        this.servletContext = new MockJakartaServletContext();
    }

    @Test
    public void testGetMimeType() {
        assertEquals("application/octet-stream", this.servletContext.getMimeType("any"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getAttribute(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetAttribute() {
        this.servletContext.getAttribute("attr1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getAttributeNames()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetAttributeNames() {
        this.servletContext.getAttributeNames();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getContext(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetContext() {
        this.servletContext.getContext("/path1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getContextPath()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetContextPath() {
        this.servletContext.getContextPath();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getInitParameter(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetInitParameter() {
        this.servletContext.getInitParameter("param1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getInitParameterNames()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetInitParameterNames() {
        this.servletContext.getInitParameterNames();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getMajorVersion()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetMajorVersion() {
        this.servletContext.getMajorVersion();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getMinorVersion()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetMinorVersion() {
        this.servletContext.getMinorVersion();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getNamedDispatcher(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetNamedDispatcher() {
        this.servletContext.getNamedDispatcher("name1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getRealPath(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetRealPath() {
        this.servletContext.getRealPath("/path1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getRequestDispatcher(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetRequestDispatcher() {
        this.servletContext.getRequestDispatcher("/path1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getResource(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetResource() throws MalformedURLException {
        this.servletContext.getResource("/resource1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getResourceAsStream(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetResourceAsStream() {
        this.servletContext.getResourceAsStream("/path1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getResourcePaths(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetResourcePaths() {
        this.servletContext.getResourcePaths("/path1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getServerInfo()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetServerInfo() {
        this.servletContext.getServerInfo();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getServletContextName()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetServletContextName() {
        this.servletContext.getServletContextName();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#log(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testLogString() {
        this.servletContext.log("log1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#log(java.lang.String, java.lang.Throwable)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testLogStringThrowable() {
        this.servletContext.log("msg1", new Exception("test"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#removeAttribute(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveAttribute() {
        this.servletContext.removeAttribute("attr1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#setAttribute(java.lang.String, java.lang.Object)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSetAttribute() {
        this.servletContext.setAttribute("attr1", "value1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getEffectiveMajorVersion()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetEffectiveMajorVersion() {
        this.servletContext.getEffectiveMajorVersion();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getEffectiveMinorVersion()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetEffectiveMinorVersion() {
        this.servletContext.getEffectiveMinorVersion();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#setInitParameter(java.lang.String, java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSetInitParameter() {
        this.servletContext.setInitParameter("param1", "value1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#addServlet(java.lang.String, java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddServletStringString() {
        this.servletContext.addServlet("servletName", JakartaTestServlet.class.getName());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#addServlet(java.lang.String, jakarta.servlet.Servlet)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddServletStringServlet() {
        this.servletContext.addServlet("servletName", new JakartaTestServlet());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#addServlet(java.lang.String, java.lang.Class)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddServletStringClassOfQextendsServlet() {
        this.servletContext.addServlet("servletName", JakartaTestServlet.class);
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#createServlet(java.lang.Class)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testCreateServlet() throws ServletException {
        this.servletContext.createServlet(JakartaTestServlet.class);
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getServletRegistration(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetServletRegistration() {
        this.servletContext.getServletRegistration("servletName");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getServletRegistrations()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetServletRegistrations() {
        this.servletContext.getServletRegistrations();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#addFilter(java.lang.String, java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddFilterStringString() {
        Filter mockFilter = Mockito.mock(Filter.class);
        this.servletContext.addFilter("filterName", mockFilter.getClass().getName());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#addFilter(java.lang.String, jakarta.servlet.Filter)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddFilterStringFilter() {
        Filter mockFilter = Mockito.mock(Filter.class);
        this.servletContext.addFilter("filterName", mockFilter);
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#addFilter(java.lang.String, java.lang.Class)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddFilterStringClassOfQextendsFilter() {
        Filter mockFilter = Mockito.mock(Filter.class);
        this.servletContext.addFilter("filterName", mockFilter.getClass());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#createFilter(java.lang.Class)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testCreateFilter() throws ServletException {
        Filter mockFilter = Mockito.mock(Filter.class);
        this.servletContext.createFilter(mockFilter.getClass());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getFilterRegistration(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetFilterRegistration() {
        this.servletContext.getFilterRegistration("filterName");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getFilterRegistrations()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetFilterRegistrations() {
        this.servletContext.getFilterRegistrations();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getSessionCookieConfig()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetSessionCookieConfig() {
        this.servletContext.getSessionCookieConfig();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#setSessionTrackingModes(java.util.Set)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSetSessionTrackingModes() {
        this.servletContext.setSessionTrackingModes(Set.of(SessionTrackingMode.COOKIE));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getDefaultSessionTrackingModes()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetDefaultSessionTrackingModes() {
        this.servletContext.getDefaultSessionTrackingModes();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getEffectiveSessionTrackingModes()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetEffectiveSessionTrackingModes() {
        this.servletContext.getEffectiveSessionTrackingModes();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#addListener(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddListenerString() {
        this.servletContext.addListener(TestEventListener.class.getName());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#addListener(java.util.EventListener)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddListenerT() {
        this.servletContext.addListener(new TestEventListener());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#addListener(java.lang.Class)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddListenerClassOfQextendsEventListener() {
        this.servletContext.addListener(TestEventListener.class);
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#createListener(java.lang.Class)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testCreateListener() throws ServletException {
        this.servletContext.createListener(TestEventListener.class);
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getJspConfigDescriptor()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetJspConfigDescriptor() {
        this.servletContext.getJspConfigDescriptor();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getClassLoader()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetClassLoader() {
        this.servletContext.getClassLoader();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#declareRoles(java.lang.String[])}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testDeclareRoles() {
        this.servletContext.declareRoles("role1", "role2");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getVirtualServerName()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetVirtualServerName() {
        this.servletContext.getVirtualServerName();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#addJspFile(java.lang.String, java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddJspFile() {
        this.servletContext.addJspFile("servletNanme", "/jspFile.jsp");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getSessionTimeout()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetSessionTimeout() {
        this.servletContext.getSessionTimeout();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#setSessionTimeout(int)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSetSessionTimeout() {
        this.servletContext.setSessionTimeout(100);
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getRequestCharacterEncoding()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetRequestCharacterEncoding() {
        this.servletContext.getRequestCharacterEncoding();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#setRequestCharacterEncoding(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSetRequestCharacterEncoding() {
        this.servletContext.setRequestCharacterEncoding(StandardCharsets.UTF_8.name());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#getResponseCharacterEncoding()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetResponseCharacterEncoding() {
        this.servletContext.getResponseCharacterEncoding();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockJakartaServletContext#setResponseCharacterEncoding(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSetResponseCharacterEncoding() {
        this.servletContext.setResponseCharacterEncoding(StandardCharsets.UTF_16.name());
    }
}
