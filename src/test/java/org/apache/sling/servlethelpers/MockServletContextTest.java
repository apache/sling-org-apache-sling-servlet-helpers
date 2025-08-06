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

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionTrackingMode;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.EventListener;
import java.util.Set;

import org.apache.sling.servlethelpers.it.JakartaTestServlet;
import org.apache.sling.servlethelpers.it.TestServlet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

/**
 * @deprecated Use {@link MockJakartaServletContextTest} instead.
 */
@Deprecated(since = "2.0.0")
public class MockServletContextTest {

    private static final class TestEventListener implements EventListener {}

    private ServletContext servletContext;

    @Before
    public void setUp() {
        this.servletContext = new MockServletContext(new MockJakartaServletContext());
    }

    @Test
    public void testGetMimeType() {
        assertEquals("application/octet-stream", this.servletContext.getMimeType("any"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getAttribute(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetAttribute() {
        this.servletContext.getAttribute("attr1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getAttributeNames()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetAttributeNames() {
        this.servletContext.getAttributeNames();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getContext(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetContext() {
        this.servletContext.getContext("/path1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getContextPath()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetContextPath() {
        this.servletContext.getContextPath();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getInitParameter(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetInitParameter() {
        this.servletContext.getInitParameter("param1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getInitParameterNames()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetInitParameterNames() {
        this.servletContext.getInitParameterNames();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getMajorVersion()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetMajorVersion() {
        this.servletContext.getMajorVersion();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getMinorVersion()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetMinorVersion() {
        this.servletContext.getMinorVersion();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getNamedDispatcher(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetNamedDispatcher() {
        this.servletContext.getNamedDispatcher("name1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getRealPath(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetRealPath() {
        this.servletContext.getRealPath("/path1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getRequestDispatcher(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetRequestDispatcher() {
        this.servletContext.getRequestDispatcher("/path1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getResource(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetResource() throws MalformedURLException {
        this.servletContext.getResource("/resource1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getResourceAsStream(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetResourceAsStream() {
        this.servletContext.getResourceAsStream("/path1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getResourcePaths(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetResourcePaths() {
        this.servletContext.getResourcePaths("/path1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getServerInfo()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetServerInfo() {
        this.servletContext.getServerInfo();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getServlet()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetServlet() throws ServletException {
        this.servletContext.getServlet("nope");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getServletContextName()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetServletContextName() {
        this.servletContext.getServletContextName();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getServletNames()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetServletNames() {
        this.servletContext.getServletNames();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getServlets()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetServlets() {
        this.servletContext.getServlets();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#log(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testLogString() {
        this.servletContext.log("log1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#log(java.lang.String, java.lang.Throwable)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testLogExceptionString() {
        this.servletContext.log(new Exception("test"), "msg1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#log(java.lang.String, java.lang.Throwable)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testLogStringThrowable() {
        this.servletContext.log("msg1", new Exception("test"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#removeAttribute(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveAttribute() {
        this.servletContext.removeAttribute("attr1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#setAttribute(java.lang.String, java.lang.Object)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSetAttribute() {
        this.servletContext.setAttribute("attr1", "value1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getEffectiveMajorVersion()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetEffectiveMajorVersion() {
        this.servletContext.getEffectiveMajorVersion();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getEffectiveMinorVersion()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetEffectiveMinorVersion() {
        this.servletContext.getEffectiveMinorVersion();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#setInitParameter(java.lang.String, java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSetInitParameter() {
        this.servletContext.setInitParameter("param1", "value1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#addServlet(java.lang.String, java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddServletStringString() {
        this.servletContext.addServlet("servletName", JakartaTestServlet.class.getName());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#addServlet(java.lang.String, jakarta.servlet.Servlet)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddServletStringServlet() {
        this.servletContext.addServlet("servletName", new TestServlet());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#addServlet(java.lang.String, java.lang.Class)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddServletStringClassOfQextendsServlet() {
        this.servletContext.addServlet("servletName", TestServlet.class);
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#createServlet(java.lang.Class)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testCreateServlet() throws ServletException {
        this.servletContext.createServlet(TestServlet.class);
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getServletRegistration(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetServletRegistration() {
        this.servletContext.getServletRegistration("servletName");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getServletRegistrations()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetServletRegistrations() {
        this.servletContext.getServletRegistrations();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#addFilter(java.lang.String, java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddFilterStringString() {
        Filter mockFilter = Mockito.mock(Filter.class);
        this.servletContext.addFilter("filterName", mockFilter.getClass().getName());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#addFilter(java.lang.String, jakarta.servlet.Filter)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddFilterStringFilter() {
        Filter mockFilter = Mockito.mock(Filter.class);
        this.servletContext.addFilter("filterName", mockFilter);
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#addFilter(java.lang.String, java.lang.Class)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddFilterStringClassOfQextendsFilter() {
        Filter mockFilter = Mockito.mock(Filter.class);
        this.servletContext.addFilter("filterName", mockFilter.getClass());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#createFilter(java.lang.Class)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testCreateFilter() throws ServletException {
        Filter mockFilter = Mockito.mock(Filter.class);
        this.servletContext.createFilter(mockFilter.getClass());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getFilterRegistration(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetFilterRegistration() {
        this.servletContext.getFilterRegistration("filterName");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getFilterRegistrations()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetFilterRegistrations() {
        this.servletContext.getFilterRegistrations();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getSessionCookieConfig()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetSessionCookieConfig() {
        this.servletContext.getSessionCookieConfig();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#setSessionTrackingModes(java.util.Set)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSetSessionTrackingModes() {
        this.servletContext.setSessionTrackingModes(Set.of(SessionTrackingMode.COOKIE));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getDefaultSessionTrackingModes()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetDefaultSessionTrackingModes() {
        this.servletContext.getDefaultSessionTrackingModes();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getEffectiveSessionTrackingModes()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetEffectiveSessionTrackingModes() {
        this.servletContext.getEffectiveSessionTrackingModes();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#addListener(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddListenerString() {
        this.servletContext.addListener(TestEventListener.class.getName());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#addListener(java.util.EventListener)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddListenerT() {
        this.servletContext.addListener(new TestEventListener());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#addListener(java.lang.Class)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddListenerClassOfQextendsEventListener() {
        this.servletContext.addListener(TestEventListener.class);
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#createListener(java.lang.Class)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testCreateListener() throws ServletException {
        this.servletContext.createListener(TestEventListener.class);
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getJspConfigDescriptor()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetJspConfigDescriptor() {
        this.servletContext.getJspConfigDescriptor();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getClassLoader()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetClassLoader() {
        this.servletContext.getClassLoader();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#declareRoles(java.lang.String[])}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testDeclareRoles() {
        this.servletContext.declareRoles("role1", "role2");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getVirtualServerName()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetVirtualServerName() {
        this.servletContext.getVirtualServerName();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#addJspFile(java.lang.String,
     * java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddJspFile() {
        this.servletContext.addJspFile("servletNanme", "/jspFile.jsp");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#getSessionTimeout()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetSessionTimeout() {
        this.servletContext.getSessionTimeout();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockServletContextTest#setSessionTimeout(int)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSetSessionTimeout() {
        this.servletContext.setSessionTimeout(100);
    }

    /**
     * Test method for {@link
     * org.apache.sling.servlethelpers.MockServletContextTest#getRequestCharacterEncoding()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetRequestCharacterEncoding() {
        this.servletContext.getRequestCharacterEncoding();
    }

    /**
     * Test method for {@link
     * org.apache.sling.servlethelpers.MockServletContextTest#setRequestCharacterEncoding(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSetRequestCharacterEncoding() {
        this.servletContext.setRequestCharacterEncoding(StandardCharsets.UTF_8.name());
    }

    /**
     * Test method for {@link
     * org.apache.sling.servlethelpers.MockServletContextTest#getResponseCharacterEncoding()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetResponseCharacterEncoding() {
        this.servletContext.getResponseCharacterEncoding();
    }

    /**
     * Test method for {@link
     * org.apache.sling.servlethelpers.MockServletContextTest#setResponseCharacterEncoding(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSetResponseCharacterEncoding() {
        this.servletContext.setResponseCharacterEncoding(StandardCharsets.UTF_16.name());
    }
}
