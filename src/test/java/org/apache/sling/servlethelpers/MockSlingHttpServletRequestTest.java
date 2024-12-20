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

import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.sling.api.adapter.AdapterManager;
import org.apache.sling.api.adapter.SlingAdaptable;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestParameter;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MockSlingHttpServletRequestTest {

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Resource resource;

    @Mock
    private AdapterManager adapterManager;

    private MockSlingHttpServletRequest request;

    @Before
    public void setUp() throws Exception {
        request = new MockSlingHttpServletRequest(resourceResolver);
        SlingAdaptable.setAdapterManager(adapterManager);
    }

    @After
    public void tearDown() throws Exception {
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
    public void testQueryString() throws UnsupportedEncodingException {
        assertNull(request.getQueryString());
        assertEquals(0, request.getParameterMap().size());
        assertFalse(request.getParameterNames().hasMoreElements());

        request.setQueryString(
                "param1=123&param2=" + URLEncoder.encode("äöüß€!:!", CharEncoding.UTF_8) + "&param3=a&param3=b");

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
    public void testRequestParameter() throws Exception {
        request.setQueryString(
                "param1=123&param2=" + URLEncoder.encode("äöüß€!:!", CharEncoding.UTF_8) + "&param3=a&param3=b");

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
    public void testFormRequestParameters() throws Exception {
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
    public void testContentTypeCharset() throws Exception {
        assertNull(request.getContentType());
        assertNull(request.getCharacterEncoding());

        request.setContentType("image/gif");
        assertEquals("image/gif", request.getContentType());
        assertNull(request.getCharacterEncoding());

        request.setContentType("text/plain;charset=UTF-8");
        assertEquals("text/plain;charset=UTF-8", request.getContentType());
        assertEquals(CharEncoding.UTF_8, request.getCharacterEncoding());

        request.setCharacterEncoding(CharEncoding.ISO_8859_1);
        assertEquals("text/plain;charset=ISO-8859-1", request.getContentType());
        assertEquals(CharEncoding.ISO_8859_1, request.getCharacterEncoding());
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
    public void testGetRequestDispatcher() {
        MockRequestDispatcherFactory requestDispatcherFactory = mock(MockRequestDispatcherFactory.class);
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
    public void testGetRemoteAddr() throws Exception {
        assertNull(null, request.getRemoteAddr());

        request.setRemoteAddr("1.2.3.4");
        assertEquals("1.2.3.4", request.getRemoteAddr());
    }

    @Test
    public void testGetRemoteHost() throws Exception {
        assertNull(null, request.getRemoteHost());

        request.setRemoteHost("host1");
        assertEquals("host1", request.getRemoteHost());
    }

    @Test
    public void testGetRemotePort() throws Exception {
        assertEquals(0, request.getRemotePort());

        request.setRemotePort(1234);
        assertEquals(1234, request.getRemotePort());
    }

    @Test
    public void testServletPathWithPathInfo() throws Exception {
        request.setServletPath("/my/path");
        request.setPathInfo("/myinfo");
        ;

        assertEquals("/my/path", request.getServletPath());
        assertEquals("/myinfo", request.getPathInfo());
    }

    @Test
    public void testServletPathWithOutPathInfo() throws Exception {
        request.setServletPath("/my/path");

        assertEquals("/my/path", request.getServletPath());
        assertNull(request.getPathInfo());
    }

    @Test
    public void testGetSuffixResource() {
        assertNull(request.getRequestPathInfo().getSuffixResource());

        ((MockRequestPathInfo) request.getRequestPathInfo()).setSuffix("/suffix");
        Resource resource = mock(Resource.class);
        when(resourceResolver.getResource("/suffix")).thenReturn(resource);

        assertSame(resource, request.getRequestPathInfo().getSuffixResource());
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
                .extracting(p -> p.getName())
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
}
