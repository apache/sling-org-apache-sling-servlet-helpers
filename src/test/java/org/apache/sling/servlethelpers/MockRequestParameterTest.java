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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class MockRequestParameterTest {

    private MockRequestParameter param = new MockRequestParameter("key1", "value1");

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameter#MockRequestParameter(java.lang.String, byte[], java.lang.String)}.
     */
    @Test
    public void testMockRequestParameterStringByteArrayString() {
        param = new MockRequestParameter("key1a", "value1a".getBytes(), "text/custom");
        assertEquals("text/custom", param.getContentType());
        assertEquals("key1a", param.getName());
        assertEquals("value1a", param.getString());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameter#MockRequestParameter(java.lang.String, byte[], java.lang.String, java.lang.String)}.
     */
    @Test
    public void testMockRequestParameterStringByteArrayStringString() {
        param = new MockRequestParameter("key1a", "value1a".getBytes(), "text/custom", "customfilename");
        assertEquals("text/custom", param.getContentType());
        assertEquals("key1a", param.getName());
        assertEquals("value1a", param.getString());
        assertEquals("customfilename", param.getFileName());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameter#setName(java.lang.String)}.
     */
    @Test
    public void testSetName() {
        param.setName("key1a");
        assertEquals("key1a", param.getName());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameter#getName()}.
     */
    @Test
    public void testGetName() {
        assertEquals("key1", param.getName());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameter#setEncoding(java.lang.String)}.
     */
    @Test
    public void testSetEncoding() {
        param.setEncoding(StandardCharsets.US_ASCII.name());
        assertEquals(StandardCharsets.US_ASCII.name(), param.getEncoding());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameter#getEncoding()}.
     */
    @Test
    public void testGetEncoding() {
        assertEquals(StandardCharsets.UTF_8.name(), param.getEncoding());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameter#get()}.
     */
    @Test
    public void testGet() {
        assertArrayEquals("value1".getBytes(), param.get());
    }

    @Test
    public void testGetWithNullValue() {
        param = new MockRequestParameter("key1", null);
        assertNull(param.get());
    }

    @Test
    public void testGetWithInvalidEncoding() {
        param.setEncoding("invalid");
        assertArrayEquals("value1".getBytes(), param.get());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameter#getContentType()}.
     */
    @Test
    public void testGetContentType() {
        assertEquals("text/plain", param.getContentType());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameter#getInputStream()}.
     */
    @Test
    public void testGetInputStream() throws IOException {
        try (InputStream is = param.getInputStream()) {
            assertNotNull(is);
        }
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameter#getFileName()}.
     */
    @Test
    public void testGetFileName() {
        assertNull(param.getFileName());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameter#getSize()}.
     */
    @Test
    public void testGetSize() {
        assertEquals(6, param.getSize());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameter#getString()}.
     */
    @Test
    public void testGetString() {
        assertEquals("value1", param.getString());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameter#getString(java.lang.String)}.
     */
    @Test
    public void testGetStringString() throws UnsupportedEncodingException {
        assertEquals("value1", param.getString(StandardCharsets.UTF_8.name()));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameter#isFormField()}.
     */
    @Test
    public void testIsFormField() {
        assertTrue(param.isFormField());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameter#toString()}.
     */
    @Test
    public void testToString() {
        assertEquals("value1", param.toString());
    }
}
