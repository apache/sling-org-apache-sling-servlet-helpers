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

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.sling.api.adapter.AdapterManager;
import org.apache.sling.api.adapter.SlingAdaptable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MockSlingJakartaHttpServletResponseTest {

    private MockSlingJakartaHttpServletResponse response;

    @Mock
    private AdapterManager adapterManager;

    @Before
    public void setUp() throws Exception {
        this.response = new MockSlingJakartaHttpServletResponse();
        SlingAdaptable.setAdapterManager(adapterManager);
    }

    @After
    public void tearDown() throws Exception {
        SlingAdaptable.unsetAdapterManager(adapterManager);
    }

    @Test
    public void testContentTypeCharset() throws Exception {
        assertNull(response.getContentType());
        assertNull(response.getCharacterEncoding());

        response.setContentType("image/gif");
        assertEquals("image/gif", response.getContentType());
        assertNull(response.getCharacterEncoding());

        response.setContentType("text/plain;charset=UTF-8");
        assertEquals("text/plain;charset=UTF-8", response.getContentType());
        assertEquals(StandardCharsets.UTF_8.name(), response.getCharacterEncoding());

        response.setCharacterEncoding(StandardCharsets.ISO_8859_1.name());
        assertEquals("text/plain;charset=ISO-8859-1", response.getContentType());
        assertEquals(StandardCharsets.ISO_8859_1.name(), response.getCharacterEncoding());
    }

    @Test
    public void testContentLength() throws Exception {
        assertEquals(0, response.getContentLength());

        response.setContentLength(55);
        assertEquals(55, response.getContentLength());
    }

    @Test
    public void testHeaders() throws Exception {
        assertEquals(0, response.getHeaderNames().size());

        response.addHeader("header1", "value1");
        response.addIntHeader("header2", 5);
        response.addDateHeader("header3", System.currentTimeMillis());

        assertEquals(3, response.getHeaderNames().size());
        assertTrue(response.containsHeader("Header1"));
        assertEquals("value1", response.getHeader("headeR1"));
        assertEquals("5", response.getHeader("header2"));
        assertNotNull(response.getHeader("header3"));

        response.setHeader("header1", "value2");
        response.addIntHeader("header2", 10);

        assertEquals(3, response.getHeaderNames().size());

        Collection<String> header1Values = response.getHeaders("Header1");
        assertEquals(1, header1Values.size());
        assertEquals("value2", header1Values.iterator().next());

        Collection<String> header2Values = response.getHeaders("header2");
        assertEquals(2, header2Values.size());
        Iterator<String> header2Iterator = header2Values.iterator();
        assertEquals("5", header2Iterator.next());
        assertEquals("10", header2Iterator.next());

        response.reset();
        assertEquals(0, response.getHeaderNames().size());
    }

    @Test
    public void testRedirect() throws Exception {
        response.sendRedirect("/location.html");
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals("/location.html", response.getHeader("Location"));
    }

    @Test
    public void testSendError() throws Exception {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void testSendErrorWithMEssage() throws Exception {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "my error message");
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertEquals("my error message", response.getStatusMessage());
    }

    @Test
    public void testSetStatus() throws Exception {
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
        assertEquals(HttpServletResponse.SC_BAD_GATEWAY, response.getStatus());

        response.reset();
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    public void testWriteStringContent() throws Exception {
        final String TEST_CONTENT = "Der Jodelkaiser äöüß€ ᚠᛇᚻ";
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(TEST_CONTENT);

        assertEquals(TEST_CONTENT, new String(response.getOutput(), StandardCharsets.UTF_8));
        assertEquals(TEST_CONTENT, response.getOutputAsString());

        response.resetBuffer();
        assertEquals(0, response.getOutputAsString().length());
    }

    @Test
    public void testWriteBinaryContent() throws Exception {
        final byte[] TEST_DATA = new byte[] {0x01, 0x02, 0x03, 0x04, 0x05};
        response.getOutputStream().write(TEST_DATA);
        assertArrayEquals(TEST_DATA, response.getOutput());

        response.resetBuffer();
        assertEquals(0, response.getOutput().length);
    }

    @Test
    public void testIsCommitted() throws Exception {
        assertFalse(response.isCommitted());
        response.flushBuffer();
        assertTrue(response.isCommitted());
    }

    @Test
    public void testCookies() {
        assertNull(response.getCookies());

        response.addCookie(new Cookie("cookie1", "value1"));
        response.addCookie(new Cookie("cookie2", "value2"));

        assertEquals("value1", response.getCookie("cookie1").getValue());

        Cookie[] cookies = response.getCookies();
        assertEquals(2, cookies.length);
        assertEquals("value1", cookies[0].getValue());
        assertEquals("value2", cookies[1].getValue());

        response.reset();
        assertNull(response.getCookies());
    }

    @Test
    public void testLocale() {
        assertEquals(Locale.US, response.getLocale());
        response.setLocale(Locale.GERMAN);
        assertEquals(Locale.GERMAN, response.getLocale());
    }

    @Test
    public void testAdaptTo() {
        when(adapterManager.getAdapter(response, String.class)).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return UUID.randomUUID().toString();
            }
        });

        // make sure adaptTo results are not cached; each invocation should produce a different result
        String result1 = response.adaptTo(String.class);
        assertNotNull(result1);

        String result2 = response.adaptTo(String.class);
        assertNotEquals(result1, result2);
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletResponse#setIntHeader(java.lang.String, int)}.
     */
    @Test
    public void testSetIntHeader() {
        response.setIntHeader("header1", 1);
        assertEquals("1", response.getHeader("header1"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletResponse#setDateHeader(java.lang.String, long)}.
     */
    @Test
    public void testSetDateHeader() {
        long now = System.currentTimeMillis();
        response.setDateHeader("header1", now);
        assertNotNull(response.getHeader("header1"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletResponse#getBufferSize()}.
     */
    @Test
    public void testGetBufferSize() {
        assertEquals(8192, response.getBufferSize());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletResponse#setBufferSize(int)}.
     */
    @Test
    public void testSetBufferSize() {
        response.setBufferSize(100);
        assertEquals(100, response.getBufferSize());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletResponse#reset()}.
     */
    @Test
    public void testReset() {
        response.getWriter().print("Hello");
        assertEquals("Hello", response.getOutputAsString());
        response.setHeader("header1", "value1");
        assertEquals("value1", response.getHeader("header1"));

        response.reset();
        assertEquals("", response.getOutputAsString());
        assertNull(response.getHeader("header1"));
    }

    @Test
    public void testResetWhenAlreadyCommitted() {
        response.getWriter().print("Hello");
        response.flushBuffer();
        assertThrows(IllegalStateException.class, () -> response.reset());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletResponse#resetBuffer()}.
     */
    @Test
    public void testResetBuffer() {
        response.getWriter().print("Hello");
        assertEquals("Hello", response.getOutputAsString());

        response.resetBuffer();
        assertEquals("", response.getOutputAsString());
    }

    @Test
    public void testResetBufferWhenAlreadyCommitted() {
        response.getWriter().print("Hello");
        response.flushBuffer();
        assertThrows(IllegalStateException.class, () -> response.resetBuffer());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletResponse#encodeRedirectURL(java.lang.String)}.
     */
    @Test
    public void testEncodeRedirectURL() {
        assertThrows(UnsupportedOperationException.class, () -> response.encodeRedirectURL("url"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletResponse#encodeURL(java.lang.String)}.
     */
    @Test
    public void testEncodeURL() {
        assertThrows(UnsupportedOperationException.class, () -> response.encodeURL("url"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockSlingJakartaHttpServletResponse#setContentLength(long)}.
     */
    @Test
    public void testSetContentLengthLong() {
        assertThrows(UnsupportedOperationException.class, () -> response.setContentLengthLong(100L));
    }
}
