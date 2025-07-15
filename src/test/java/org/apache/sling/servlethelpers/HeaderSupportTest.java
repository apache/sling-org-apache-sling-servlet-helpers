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

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class HeaderSupportTest {

    private HeaderSupport headerSupport = new HeaderSupport();

    /**
     * Test method for {@link org.apache.sling.servlethelpers.HeaderSupport#addHeader(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testAddHeader() {
        headerSupport.addHeader("name1", "value1");
        assertEquals("value1", headerSupport.getHeader("name1"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.HeaderSupport#addIntHeader(java.lang.String, int)}.
     */
    @Test
    public void testAddIntHeader() {
        headerSupport.addIntHeader("name1", 2);
        assertEquals(2, headerSupport.getIntHeader("name1"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.HeaderSupport#addDateHeader(java.lang.String, long)}.
     */
    @Test
    public void testAddDateHeaderStringLong() {
        long now = System.currentTimeMillis();
        headerSupport.addDateHeader("name1", now);
        assertEquals(new Date(now).toString(), new Date(headerSupport.getDateHeader("name1")).toString());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.HeaderSupport#addDateHeader(java.lang.String, java.time.Instant)}.
     */
    @Test
    public void testAddDateHeaderStringInstant() {
        long now = System.currentTimeMillis();
        headerSupport.addDateHeader("name1", Instant.ofEpochMilli(now));
        assertEquals(new Date(now).toString(), new Date(headerSupport.getDateHeader("name1")).toString());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.HeaderSupport#setHeader(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testSetHeader() {
        headerSupport.addHeader("name1", "value1");
        headerSupport.setHeader("name1", "value2");
        Collection<String> headers = headerSupport.getHeaders("name1");
        assertEquals(1, headers.size());
        assertTrue(headers.contains("value2"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.HeaderSupport#setIntHeader(java.lang.String, int)}.
     */
    @Test
    public void testSetIntHeader() {
        headerSupport.addIntHeader("name1", 1);
        headerSupport.setIntHeader("name1", 2);
        Collection<String> headers = headerSupport.getHeaders("name1");
        assertEquals(1, headers.size());
        assertTrue(headers.contains("2"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.HeaderSupport#setDateHeader(java.lang.String, long)}.
     */
    @Test
    public void testSetDateHeader() {
        long now = System.currentTimeMillis();
        headerSupport.addDateHeader("name1", now);
        long later = now + TimeUnit.MINUTES.toMillis(5);
        headerSupport.setDateHeader("name1", later);
        assertEquals(new Date(later).toString(), new Date(headerSupport.getDateHeader("name1")).toString());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.HeaderSupport#containsHeader(java.lang.String)}.
     */
    @Test
    public void testContainsHeader() {
        assertFalse(headerSupport.containsHeader("name1"));
        headerSupport.setHeader("name1", "value2");
        assertTrue(headerSupport.containsHeader("name1"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.HeaderSupport#getHeader(java.lang.String)}.
     */
    @Test
    public void testGetHeader() {
        assertNull(headerSupport.getHeader("name1"));
        headerSupport.setHeader("name1", "value2");
        assertEquals("value2", headerSupport.getHeader("name1"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.HeaderSupport#getIntHeader(java.lang.String)}.
     */
    @Test
    public void testGetIntHeader() {
        assertEquals(0, headerSupport.getIntHeader("name1"));
        headerSupport.setIntHeader("name1", 2);
        assertEquals(2, headerSupport.getIntHeader("name1"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.HeaderSupport#getDateHeader(java.lang.String)}.
     */
    @Test
    public void testGetDateHeader() {
        assertEquals(0L, headerSupport.getDateHeader("name1"));
        long now = System.currentTimeMillis();
        headerSupport.addDateHeader("name1", now);
        assertEquals(new Date(now).toString(), new Date(headerSupport.getDateHeader("name1")).toString());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.HeaderSupport#getHeaders(java.lang.String)}.
     */
    @Test
    public void testGetHeaders() {
        assertTrue(headerSupport.getHeaders("name1").isEmpty());
        headerSupport.setHeader("name1", "value2");
        Collection<String> headers = headerSupport.getHeaders("name1");
        assertEquals(1, headers.size());
        assertTrue(headers.contains("value2"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.HeaderSupport#getHeaderNames()}.
     */
    @Test
    public void testGetHeaderNames() {
        assertTrue(headerSupport.getHeaderNames().isEmpty());
        headerSupport.setHeader("name1", "value2");
        Collection<String> headerNames = headerSupport.getHeaderNames();
        assertEquals(1, headerNames.size());
        assertTrue(headerNames.contains("name1"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.HeaderSupport#reset()}.
     */
    @Test
    public void testReset() {
        headerSupport.setHeader("name1", "value2");
        headerSupport.reset();
        assertTrue(headerSupport.getHeaderNames().isEmpty());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.HeaderSupport#toEnumeration(java.util.Collection)}.
     */
    @Test
    public void testToEnumeration() {
        Enumeration<String> e = HeaderSupport.toEnumeration(List.of("one", "two"));
        assertEquals("one", e.nextElement());
        assertEquals("two", e.nextElement());
    }
}
