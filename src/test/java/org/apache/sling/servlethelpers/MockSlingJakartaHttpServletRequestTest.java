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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import jakarta.servlet.ReadListener;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.adapter.AdapterManager;
import org.apache.sling.api.adapter.SlingAdaptable;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestProgressTracker;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MockSlingJakartaHttpServletRequestTest {

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Resource resource;

    @Mock
    private AdapterManager adapterManager;

    private MockSlingJakartaHttpServletRequest request;

    @Before
    public void setUp() {
        request = new MockSlingJakartaHttpServletRequest(resourceResolver);
        SlingAdaptable.setAdapterManager(adapterManager);
    }

    @After
    public void tearDown() {
        SlingAdaptable.unsetAdapterManager(adapterManager);
    }

    @Test
    public void testResourceResolver() {
        assertSame(resourceResolver, request.getResourceResolver());
    }

    @Test
    public void testDefaultResourceResolver() {
        assertNotNull(request.getResourceResolver());
    }

    @Test
    public void testSession() {
        HttpSession session = request.getSession(false);
        assertNull(session);
        session = request.getSession();
        assertNotNull(session);
    }

    @Test
    public void testPathInfo() {
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setResourcePath("/content/resource");
        requestPathInfo.setExtension("html");
        requestPathInfo.setSelectorString("a1.a2");
        requestPathInfo.setSuffix("/content/another/resource.html");

        assertEquals("/content/resource.a1.a2.html/content/another/resource.html", request.getPathInfo());

        requestPathInfo.setSelectorString(null);

        assertEquals("/content/resource.html/content/another/resource.html", request.getPathInfo());

        requestPathInfo.setSuffix(null);

        assertEquals("/content/resource.html", request.getPathInfo());

        requestPathInfo.setResourcePath(null);

        assertNull(request.getPathInfo());
    }

    @Test
    public void testRequestUri() {
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setResourcePath("/content/resource");
        requestPathInfo.setExtension("html");
        requestPathInfo.setSelectorString("a1.a2");
        requestPathInfo.setSuffix("/content/another/resource.html");

        assertEquals("/content/resource.a1.a2.html/content/another/resource.html", request.getRequestURI());

        request.setServletPath("/my");

        assertEquals("/my/content/resource.a1.a2.html/content/another/resource.html", request.getRequestURI());
    }

    @Test
    public void testRequestUrl() {
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setResourcePath("/content/resource");
        requestPathInfo.setExtension("html");

        assertEquals(
                "http://localhost/content/resource.html",
                request.getRequestURL().toString());

        request.setServerPort(8080);

        assertEquals(
                "http://localhost:8080/content/resource.html",
                request.getRequestURL().toString());

        request.setScheme("https");
        request.setServerPort(443);

        assertEquals(
                "https://localhost/content/resource.html",
                request.getRequestURL().toString());

        request.setServerPort(8443);

        assertEquals(
                "https://localhost:8443/content/resource.html",
                request.getRequestURL().toString());
    }

    @Test
    public void testRequestPathInfo() {
        assertNotNull(request.getRequestPathInfo());
    }

    @Test
    public void testAttributes() {
        request.setAttribute("attr1", "value1");
        assertTrue(request.getAttributeNames().hasMoreElements());
        assertEquals("value1", request.getAttribute("attr1"));
        request.removeAttribute("attr1");
        assertFalse(request.getAttributeNames().hasMoreElements());
    }

    @Test
    public void testResource() {
        assertNull(request.getResource());
        request.setResource(resource);
        assertSame(resource, request.getResource());
    }

    @Test
    public void testContextPath() {
        assertEquals("", request.getContextPath());
        request.setContextPath("/ctx");
        assertEquals("/ctx", request.getContextPath());
    }

    @Test
    public void testLocale() {
        assertEquals(Locale.US, request.getLocale());
        request.setLocale(Locale.GERMAN);
        assertEquals(Locale.GERMAN, request.getLocale());
        Enumeration<Locale> locales = request.getLocales();
        assertTrue(locales.hasMoreElements());
        assertEquals(Locale.GERMAN, locales.nextElement());
        assertFalse(locales.hasMoreElements());
    }

    @Test
    public void testQueryString() {
        assertNull(request.getQueryString());
        assertEquals(0, request.getParameterMap().size());
        assertFalse(request.getParameterNames().hasMoreElements());

        request.setQueryString(
                "param1=123&param2=" + URLEncoder.encode("äöüß€!:!", StandardCharsets.UTF_8) + "&param3=a&param3=b");

        assertNotNull(request.getQueryString());
        assertEquals(3, request.getParameterMap().size());
        assertTrue(request.getParameterNames().hasMoreElements());
        assertEquals("123", request.getParameter("param1"));
        assertEquals("äöüß€!:!", request.getParameter("param2"));
        assertArrayEquals(new String[] {"a", "b"}, request.getParameterValues("param3"));

        Map<String, Object> paramMap = new LinkedHashMap<>();
        paramMap.put("p1", "a");
        paramMap.put("p2", new String[] {"b", "c"});
        paramMap.put("p3", null);
        paramMap.put("p4", new String[] {null});
        paramMap.put("p5", 22);
        request.setParameterMap(paramMap);

        assertEquals("p1=a&p2=b&p2=c&p4=&p5=22", request.getQueryString());
    }

    @Test
    public void testSchemeSecure() {
        assertEquals("http", request.getScheme());
        assertFalse(request.isSecure());

        request.setScheme("https");
        assertEquals("https", request.getScheme());
        assertTrue(request.isSecure());
    }

    @Test
    public void testServerNamePort() {
        assertEquals("localhost", request.getServerName());
        assertEquals(80, request.getServerPort());

        request.setServerName("myhost");
        request.setServerPort(12345);
        assertEquals("myhost", request.getServerName());
        assertEquals(12345, request.getServerPort());
    }

    @Test
    public void testMethod() {
        assertEquals(HttpConstants.METHOD_GET, request.getMethod());

        request.setMethod(HttpConstants.METHOD_POST);
        assertEquals(HttpConstants.METHOD_POST, request.getMethod());
    }

    @Test
    public void testHeaders() {
        assertFalse(request.getHeaderNames().hasMoreElements());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        long dateValue = calendar.getTimeInMillis();

        request.addHeader("header1", "value1");
        request.addIntHeader("header2", 5);
        request.addDateHeader("header3", dateValue);

        assertEquals("value1", request.getHeader("Header1"));
        assertEquals(5, request.getIntHeader("headeR2"));
        assertEquals(dateValue, request.getDateHeader("header3"));

        request.setHeader("header1", "value2");
        request.addIntHeader("Header2", 10);

        Enumeration<String> header1Values = request.getHeaders("header1");
        assertEquals("value2", header1Values.nextElement());
        assertFalse(header1Values.hasMoreElements());

        Enumeration<String> header2Values = request.getHeaders("header2");
        assertEquals("5", header2Values.nextElement());
        assertEquals("10", header2Values.nextElement());
        assertFalse(header2Values.hasMoreElements());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDateHeader() {
        request.addHeader("header1", "thisIsNotADate");
        // make sure IllegalArgumentException is thrown as defined by the HttpServletRequest API
        request.getDateHeader("header1");
    }

    @Test
    public void testCookies() {
        assertNull(request.getCookies());

        request.addCookie(new Cookie("cookie1", "value1"));
        request.addCookie(new Cookie("cookie2", "value2"));

        assertEquals("value1", request.getCookie("cookie1").getValue());

        Cookie[] cookies = request.getCookies();
        assertEquals(2, cookies.length);
        assertEquals("value1", cookies[0].getValue());
        assertEquals("value2", cookies[1].getValue());
    }

    @Test
    public void testDefaultResourceBundle() {
        ResourceBundle bundle = request.getResourceBundle(Locale.US);
        assertNotNull(bundle);
        assertFalse(bundle.getKeys().hasMoreElements());
    }

    @Test
    public void testRequestParameter() {
        request.setQueryString(
                "param1=123&param2=" + URLEncoder.encode("äöüß€!:!", StandardCharsets.UTF_8) + "&param3=a&param3=b");

        assertEquals(3, request.getRequestParameterMap().size());
        assertEquals(4, request.getRequestParameterList().size());
        assertEquals("123", request.getRequestParameter("param1").getString());
        assertEquals("äöüß€!:!", request.getRequestParameter("param2").getString());
        assertEquals("a", request.getRequestParameters("param3")[0].getString());
        assertEquals("b", request.getRequestParameters("param3")[1].getString());

        assertNull(request.getRequestParameter("unknown"));
        assertNull(request.getRequestParameters("unknown"));

        assertEquals("param1", ((MockRequestParameter) request.getRequestParameter("param1")).getName());
    }

    @Test
    public void testFormRequestParameters() throws UnsupportedEncodingException {
        request.addRequestParameter("param1", "value1");
        request.addRequestParameter("param2", "value2".getBytes("UTF-8"), "application/xml");
        request.addRequestParameter("param3", "value3".getBytes("UTF-8"), "application/json", "param3.json");
        request.addRequestParameter("param4", "value4a".getBytes("UTF-8"), "application/xml");
        request.addRequestParameter("param4", "value4b".getBytes("UTF-8"), "application/json");

        RequestParameter param1 = request.getRequestParameter("param1");
        assertEquals("value1", param1.getString());
        assertArrayEquals("value1".getBytes(), param1.get());
        assertEquals("text/plain", param1.getContentType());
        assertArrayEquals("value1".getBytes(), param1.get());

        RequestParameter param2 = request.getRequestParameter("param2");
        assertEquals("value2", param2.getString());
        assertEquals("application/xml", param2.getContentType());
        assertArrayEquals("value2".getBytes(), param2.get());

        RequestParameter param3 = request.getRequestParameter("param3");
        assertEquals("value3", param3.getString());
        assertEquals("application/json", param3.getContentType());
        assertArrayEquals("value3".getBytes(), param3.get());
        assertEquals("param3.json", param3.getFileName());

        RequestParameter param4 = request.getRequestParameter("param4");
        assertEquals("value4a", param4.getString());
        assertEquals("application/xml", param4.getContentType());
        assertArrayEquals("value4a".getBytes(), param4.get());

        RequestParameter[] param4array = request.getRequestParameters("param4");
        assertEquals(2, param4array.length);
        assertEquals("value4a", param4array[0].getString());
        assertEquals("value4b", param4array[1].getString());
        assertEquals("application/xml", param4array[0].getContentType());
        assertEquals("application/json", param4array[1].getContentType());
        assertArrayEquals("value4a".getBytes(), param4array[0].get());
        assertArrayEquals("value4b".getBytes(), param4array[1].get());
    }

    @Test
    public void testContentTypeCharset() {
        assertNull(request.getContentType());
        assertNull(request.getCharacterEncoding());

        request.setContentType("image/gif");
        assertEquals("image/gif", request.getContentType());
        assertNull(request.getCharacterEncoding());

        request.setContentType("text/plain;charset=UTF-8");
        assertEquals("text/plain;charset=UTF-8", request.getContentType());
        assertEquals(StandardCharsets.UTF_8.name(), request.getCharacterEncoding());

        request.setCharacterEncoding(StandardCharsets.ISO_8859_1.name());
        assertEquals("text/plain;charset=ISO-8859-1", request.getContentType());
        assertEquals(StandardCharsets.ISO_8859_1.name(), request.getCharacterEncoding());
    }

    @Test
    public void testContent() throws Exception {
        assertEquals(0, request.getContentLength());
        assertNotNull(request.getInputStream());
        assertArrayEquals(new byte[0], IOUtils.toByteArray(request.getInputStream()));

        byte[] data = new byte[] {0x01, 0x02, 0x03};
        request.setContent(data);

        assertEquals(data.length, request.getContentLength());
        assertArrayEquals(data, IOUtils.toByteArray(request.getInputStream()));
    }

    @Test
    public void testContentFromReader() throws Exception {
        Charset utf8 = Charset.forName("UTF-8");
        request.setContent("hello".getBytes(utf8));
        assertEquals(5, request.getContentLength());
        BufferedReader reader = request.getReader();
        String content = IOUtils.toString(reader);
        assertEquals("hello", content);
    }

    @Test
    public void testGetReaderAfterGetInputStream() {
        boolean thrown = false;
        request.getInputStream();
        try {
            request.getReader();
        } catch (IllegalStateException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void testGetInputStreamAfterGetReader() {
        boolean thrown = false;
        request.getReader();
        try {
            request.getInputStream();
        } catch (IllegalStateException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void testGetInputStreamIsReady() {
        ServletInputStream inputStream = request.getInputStream();
        assertTrue(inputStream.isReady());
    }

    @Test
    public void testGetInputStreamIsFinished() {
        ServletInputStream inputStream = request.getInputStream();
        assertThrows(UnsupportedOperationException.class, inputStream::isFinished);
    }

    @Test
    public void testGetInputStreamSetReadListener() {
        ServletInputStream inputStream = request.getInputStream();
        ReadListener mockReadListener = Mockito.mock(ReadListener.class);
        assertThrows(UnsupportedOperationException.class, () -> inputStream.setReadListener(mockReadListener));
    }

    @Test
    public void testGetRequestDispatcher() {
        MockJakartaRequestDispatcherFactory requestDispatcherFactory = mock(MockJakartaRequestDispatcherFactory.class);
        RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);
        when(requestDispatcherFactory.getRequestDispatcher(any(Resource.class), any()))
                .thenReturn(requestDispatcher);
        when(requestDispatcherFactory.getRequestDispatcher(any(String.class), any()))
                .thenReturn(requestDispatcher);

        request.setRequestDispatcherFactory(requestDispatcherFactory);

        assertSame(requestDispatcher, request.getRequestDispatcher("/path"));
        assertSame(requestDispatcher, request.getRequestDispatcher("/path", new RequestDispatcherOptions()));
        assertSame(requestDispatcher, request.getRequestDispatcher(resource));
        assertSame(requestDispatcher, request.getRequestDispatcher(resource, new RequestDispatcherOptions()));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetRequestDispatcherWithoutFactory() {
        request.getRequestDispatcher("/path");
    }

    @Test
    public void testGetRemoteUser() {
        assertNull(null, request.getRemoteUser());

        request.setRemoteUser("admin");
        assertEquals("admin", request.getRemoteUser());
    }

    @Test
    public void testGetRemoteAddr() {
        assertNull(null, request.getRemoteAddr());

        request.setRemoteAddr("1.2.3.4");
        assertEquals("1.2.3.4", request.getRemoteAddr());
    }

    @Test
    public void testGetRemoteHost() {
        assertNull(null, request.getRemoteHost());

        request.setRemoteHost("host1");
        assertEquals("host1", request.getRemoteHost());
    }

    @Test
    public void testGetRemotePort() {
        assertEquals(0, request.getRemotePort());

        request.setRemotePort(1234);
        assertEquals(1234, request.getRemotePort());
    }

    @Test
    public void testServletPathWithPathInfo() {
        request.setServletPath("/my/path");
        request.setPathInfo("/myinfo");

        assertEquals("/my/path", request.getServletPath());
        assertEquals("/myinfo", request.getPathInfo());
    }

    @Test
    public void testServletPathWithOutPathInfo() {
        request.setServletPath("/my/path");

        assertEquals("/my/path", request.getServletPath());
        assertNull(request.getPathInfo());
    }

    @Test
    public void testGetSuffixResource() {
        assertNull(request.getRequestPathInfo().getSuffixResource());

        ((MockRequestPathInfo) request.getRequestPathInfo()).setSuffix("/suffix");
        Resource mockResource = mock(Resource.class);
        when(resourceResolver.getResource("/suffix")).thenReturn(mockResource);

        assertSame(mockResource, request.getRequestPathInfo().getSuffixResource());
    }

    @Test
    public void testAdaptTo() {
        when(adapterManager.getAdapter(request, String.class)).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return UUID.randomUUID().toString();
            }
        });

        // make sure adaptTo results are not cached; each invocation should produce a different result
        String result1 = request.adaptTo(String.class);
        assertNotNull(result1);

        String result2 = request.adaptTo(String.class);
        assertNotEquals(result1, result2);
    }

    @Test
    public void testNoParts() {
        assertThat(request.getParts()).as("request parts").isEmpty();
        assertThat(request.getPart("some-part")).as("missing part").isNull();
    }

    @Test
    public void testExistingParts() {
        ByteArrayPart part = ByteArrayPart.builder()
                .withName("log.txt")
                .withContent("hello, world".getBytes(UTF_8))
                .build();

        request.addPart(part);

        assertThat(request.getParts())
                .as("request parts")
                .hasSize(1)
                .extracting(Part::getName)
                .containsExactly("log.txt");

        assertThat(request.getPart("log.txt")).as("part looked up by name").isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPart() {
        request.addPart(null);
    }

    @Test
    public void testGetUserPrincipal() {
        assertNull(null, request.getUserPrincipal());

        request.setRemoteUser("admin");
        Principal userPrincipal = request.getUserPrincipal();
        assertNotNull(userPrincipal);
        assertEquals("admin", userPrincipal.getName());
    }

    @Test
    public void testGetUserPrincipalWithNullResourceResolver() {
        request = new MockSlingJakartaHttpServletRequest(null);
        assertNull(null, request.getUserPrincipal());

        request.setRemoteUser("admin");
        Principal userPrincipal = request.getUserPrincipal();
        assertNotNull(userPrincipal);
        assertEquals("admin", userPrincipal.getName());
    }

    @Test
    public void testGetUserPrincipalFromResourceResolver() {
        Mockito.when(resourceResolver.adaptTo(Principal.class)).thenReturn(() -> "rruser");
        // always returns null for anonymous user
        assertNull(request.getUserPrincipal());

        // make remote user not anonymous
        request.setRemoteUser("remoteuser");
        Principal userPrincipal = request.getUserPrincipal();
        assertNotNull(userPrincipal);
        assertEquals("rruser", userPrincipal.getName());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getPathTranslated()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetPathTranslated() {
        request.getPathTranslated();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getRequestedSessionId()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetRequestedSessionId() {
        request.getRequestedSessionId();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#isRequestedSessionIdFromCookie()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testIsRequestedSessionIdFromCookie() {
        request.isRequestedSessionIdFromCookie();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#isRequestedSessionIdFromURL()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testIsRequestedSessionIdFromURL() {
        request.isRequestedSessionIdFromURL();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#isRequestedSessionIdValid()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testIsRequestedSessionIdValid() {
        request.isRequestedSessionIdValid();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#isUserInRole(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testIsUserInRole() {
        request.isUserInRole("role1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getLocalAddr()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetLocalAddr() {
        request.getLocalAddr();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getLocalName()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetLocalName() {
        request.getLocalName();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getLocalPort()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetLocalPort() {
        request.getLocalPort();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getProtocol()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetProtocol() {
        request.getProtocol();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#authenticate(jakarta.servlet.http.HttpServletResponse)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAuthenticate() {
        request.authenticate(Mockito.mock(HttpServletResponse.class));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#login(java.lang.String, java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testLogin() {
        request.login("user1", "pwd1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#logout()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testLogout() throws ServletException {
        request.logout();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getServletContext()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetServletContext() {
        request.getServletContext();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#startAsync()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testStartAsync() {
        request.startAsync();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#startAsync(jakarta.servlet.ServletRequest, jakarta.servlet.ServletResponse)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testStartAsyncServletRequestServletResponse() {
        request.startAsync(Mockito.mock(ServletRequest.class), Mockito.mock(ServletResponse.class));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#isAsyncStarted()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testIsAsyncStarted() {
        request.isAsyncStarted();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#isAsyncSupported()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testIsAsyncSupported() {
        request.isAsyncSupported();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getAsyncContext()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetAsyncContext() {
        request.getAsyncContext();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getDispatcherType()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetDispatcherType() {
        request.getDispatcherType();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#changeSessionId()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testChangeSessionId() {
        request.changeSessionId();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#upgrade(java.lang.Class)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testUpgrade() throws IOException, ServletException {
        request.upgrade(HttpUpgradeHandler.class);
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getContentLengthLong()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetContentLengthLong() {
        request.getContentLengthLong();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getRequestId()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetRequestId() {
        request.getRequestId();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getProtocolRequestId()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetProtocolRequestId() {
        request.getProtocolRequestId();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getServletConnection()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetServletConnection() {
        request.getServletConnection();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getRequestProgressTracker()}.
     */
    @Test
    public void testGetRequestProgressTracker() {
        RequestProgressTracker requestProgressTracker = request.getRequestProgressTracker();
        assertNotNull(requestProgressTracker);
        assertNotNull(requestProgressTracker.getMessages());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getAuthType()}.
     */
    @Test
    public void testGetAuthType() {
        assertNull(request.getAuthType());
        request.setAuthType("FORMS1");
        assertEquals("FORMS1", request.getAuthType());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#setAuthType(java.lang.String)}.
     */
    @Test
    public void testSetAuthType() {
        request.setAuthType("FORMS2");
        assertEquals("FORMS2", request.getAuthType());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#setIntHeader(java.lang.String, int)}.
     */
    @Test
    public void testSetIntHeader() {
        request.setIntHeader("header1", 1);
        assertEquals(1, request.getIntHeader("header1"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#setDateHeader(java.lang.String, long)}.
     */
    @Test
    public void testSetDateHeader() {
        long now = System.currentTimeMillis();
        request.setDateHeader("header1", now);
        assertEquals(new Date(now).toString(), new Date(request.getDateHeader("header1")).toString());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getResponseContentType()}.
     */
    @Test
    public void testGetResponseContentType() {
        assertNull(request.getResponseContentType());
        request.setResponseContentType("text/html");
        assertEquals("text/html", request.getResponseContentType());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#setResponseContentType(java.lang.String)}.
     */
    @Test
    public void testSetResponseContentType() {
        request.setResponseContentType("text/html");
        assertEquals("text/html", request.getResponseContentType());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getResponseContentTypes()}.
     */
    @Test
    public void testGetResponseContentTypes() {
        request.setResponseContentType("text/html");
        Enumeration<String> responseContentTypes = request.getResponseContentTypes();
        assertTrue(responseContentTypes.hasMoreElements());
        assertEquals("text/html", responseContentTypes.nextElement());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getSession(boolean)}.
     */
    @Test
    public void testGetSessionBoolean() {
        assertNull(request.getSession(false));
        HttpSession session = request.getSession(true);
        assertNotNull(session);
        HttpSession session2 = request.getSession(true);
        assertSame(session2, session);
        HttpSession session3 = request.getSession(false);
        assertSame(session3, session);
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getParameter(java.lang.String)}.
     */
    @Test
    public void testGetParameter() {
        assertNull(request.getParameter("param1"));
        request.setParameterMap(Map.of("param1", new String[0]));
        assertNull(request.getParameter("param1"));
        request.setParameterMap(Map.of("param1", new String[] {"value1", "value2"}));
        assertEquals("value1", request.getParameter("param1"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getParameterValues(java.lang.String)}.
     */
    @Test
    public void testGetParameterValues() {
        assertNull(request.getParameterValues("param1"));
        request.setParameterMap(Map.of("param1", new String[0]));
        assertArrayEquals(new String[0], request.getParameterValues("param1"));
        request.setParameterMap(Map.of("param1", new String[] {"value1", "value2"}));
        assertArrayEquals(new String[] {"value1", "value2"}, request.getParameterValues("param1"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getRequestDispatcher(java.lang.String)}.
     */
    @Test
    public void testGetRequestDispatcherString() {
        String path = "/path1";
        assertThrows(IllegalStateException.class, () -> request.getRequestDispatcher(path));
        MockJakartaRequestDispatcherFactory mockFactory = Mockito.mock(MockJakartaRequestDispatcherFactory.class);
        RequestDispatcher mockDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(mockFactory.getRequestDispatcher(path, null)).thenReturn(mockDispatcher);
        request.setRequestDispatcherFactory(mockFactory);
        assertSame(mockDispatcher, request.getRequestDispatcher(path));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getRequestDispatcher(java.lang.String, org.apache.sling.api.request.RequestDispatcherOptions)}.
     */
    @Test
    public void testGetRequestDispatcherStringRequestDispatcherOptions() {
        String path = "/path1";
        RequestDispatcherOptions options = new RequestDispatcherOptions();
        assertThrows(IllegalStateException.class, () -> request.getRequestDispatcher(path, options));
        MockJakartaRequestDispatcherFactory mockFactory = Mockito.mock(MockJakartaRequestDispatcherFactory.class);
        RequestDispatcher mockDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(mockFactory.getRequestDispatcher(path, options)).thenReturn(mockDispatcher);
        request.setRequestDispatcherFactory(mockFactory);
        assertSame(mockDispatcher, request.getRequestDispatcher(path, options));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getRequestDispatcher(org.apache.sling.api.resource.Resource)}.
     */
    @Test
    public void testGetRequestDispatcherResource() {
        Resource mockResource = Mockito.mock(Resource.class);
        assertThrows(IllegalStateException.class, () -> request.getRequestDispatcher(mockResource));
        MockJakartaRequestDispatcherFactory mockFactory = Mockito.mock(MockJakartaRequestDispatcherFactory.class);
        RequestDispatcher mockDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(mockFactory.getRequestDispatcher(mockResource, null)).thenReturn(mockDispatcher);
        request.setRequestDispatcherFactory(mockFactory);
        assertSame(mockDispatcher, request.getRequestDispatcher(mockResource));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getRequestDispatcher(org.apache.sling.api.resource.Resource, org.apache.sling.api.request.RequestDispatcherOptions)}.
     */
    @Test
    public void testGetRequestDispatcherResourceRequestDispatcherOptions() {
        Resource mockResource = Mockito.mock(Resource.class);
        RequestDispatcherOptions options = new RequestDispatcherOptions();
        assertThrows(IllegalStateException.class, () -> request.getRequestDispatcher(mockResource, options));
        MockJakartaRequestDispatcherFactory mockFactory = Mockito.mock(MockJakartaRequestDispatcherFactory.class);
        RequestDispatcher mockDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(mockFactory.getRequestDispatcher(mockResource, options)).thenReturn(mockDispatcher);
        request.setRequestDispatcherFactory(mockFactory);
        assertSame(mockDispatcher, request.getRequestDispatcher(mockResource, options));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getReader()}.
     */
    @Test
    public void testGetReader() {
        // null content
        BufferedReader reader = request.getReader();
        assertNotNull(reader);

        // non-null content + null characterEncoding
        request.setContent("hello".getBytes());
        BufferedReader reader2 = request.getReader();
        assertNotNull(reader2);

        // non-null content + non-null characterEncoding
        request.setContent("hello".getBytes());
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        BufferedReader reader3 = request.getReader();
        assertNotNull(reader3);

        // non-null content + invalid characterEncoding
        request.setContent("hello".getBytes());
        request.setCharacterEncoding("invalid");
        BufferedReader reader4 = request.getReader();
        assertNotNull(reader4);
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#setParameterMap(java.util.Map)}.
     */
    @Test
    public void testSetParameterMap() {
        request.setParameterMap(Map.of(
                "key1", new String[] {"value1", "value2"},
                "key2",
                        new MockRequestParameter[] {
                            new MockRequestParameter("key2", "value3"), new MockRequestParameter("key2", "value4")
                        },
                "key3", "value5"));
        assertEquals("value1", request.getParameter("key1"));
        assertEquals("value3", request.getParameter("key2"));
        assertEquals("value5", request.getParameter("key3"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getRequestParameter(java.lang.String)}.
     */
    @Test
    public void testGetRequestParameter() {
        assertNull(request.getRequestParameter("key1"));
        request.setParameterMap(Map.of("key1", new String[0]));
        assertNull(request.getRequestParameter("key1"));
        request.setParameterMap(Map.of("key1", new String[] {"value1"}));
        assertEquals("value1", request.getRequestParameter("key1").getString());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#setQueryString(java.lang.String)}.
     */
    @Test
    public void testSetQueryString() {
        request.setQueryString("key1=value1&key1=value2&key2=value3&&key3&key4=");
        assertArrayEquals(new String[] {"value1", "value2"}, request.getParameterValues("key1"));
        assertEquals("value3", request.getParameter("key2"));
        assertNull(request.getParameter("key3"));
        assertNull(request.getParameter("key4"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletRequest#getRequestURI()}.
     */
    @Test
    public void testGetRequestURI() {
        assertEquals("/", request.getRequestURI());
        request.setPathInfo("/pathinfo");
        assertEquals("/pathinfo", request.getRequestURI());
        request.setServletPath("/servletpath");
        assertEquals("/servletpath/pathinfo", request.getRequestURI());
    }
}
