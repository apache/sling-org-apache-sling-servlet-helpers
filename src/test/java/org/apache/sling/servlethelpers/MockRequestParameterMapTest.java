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

import java.util.Map;
import java.util.stream.Stream;

import org.apache.sling.api.request.RequestParameter;
import org.junit.Test;
import org.junit.Test.None;

import static org.junit.Assert.*;

/**
 *
 */
public class MockRequestParameterMapTest {

    private MockRequestParameterMap map = new MockRequestParameterMap();

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameterMap#hashCode()}.
     */
    @Test(expected = None.class)
    public void testHashCode() {
        map.hashCode();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameterMap#getValue(java.lang.String)}.
     */
    @Test
    public void testGetValue() {
        assertNull(map.getValue("key"));
        map.put("key", new RequestParameter[0]);
        assertNull(map.getValue("key"));
        map.put("key", new RequestParameter[] {new MockRequestParameter("key", "value1")});
        RequestParameter value = map.getValue("key");
        assertNotNull(value);
        assertEquals("value1", value.getString());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameterMap#getValues(java.lang.String)}.
     */
    @Test
    public void testGetValues() {
        assertNull(map.getValues("key"));
        map.put("key", new RequestParameter[] {
            new MockRequestParameter("key", "value1"), new MockRequestParameter("key", "value2")
        });
        RequestParameter[] values = map.getValues("key");
        assertNotNull(values);
        assertEquals(2, values.length);
        assertTrue(Stream.of(values).anyMatch(v -> "value1".equals(v.getString())));
        assertTrue(Stream.of(values).anyMatch(v -> "value2".equals(v.getString())));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameterMap#size()}.
     */
    @Test
    public void testSize() {
        assertEquals(0, map.size());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameterMap#isEmpty()}.
     */
    @Test
    public void testIsEmpty() {
        assertTrue(map.isEmpty());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameterMap#containsKey(java.lang.Object)}.
     */
    @Test
    public void testContainsKey() {
        assertFalse(map.containsKey("nope"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameterMap#containsValue(java.lang.Object)}.
     */
    @Test
    public void testContainsValue() {
        assertFalse(map.containsValue(new RequestParameter[] {new MockRequestParameter("nope", "value1")}));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameterMap#get(java.lang.Object)}.
     */
    @Test
    public void testGet() {
        assertNull(map.get("key"));
        RequestParameter[] params = new RequestParameter[] {new MockRequestParameter("key", "value1")};
        map.put("key", params);
        assertArrayEquals(params, map.get("key"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameterMap#put(java.lang.String, org.apache.sling.api.request.RequestParameter[])}.
     */
    @Test
    public void testPut() {
        RequestParameter[] params = new RequestParameter[] {new MockRequestParameter("key", "value1")};
        map.put("key", params);
        assertArrayEquals(params, map.get("key"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameterMap#remove(java.lang.Object)}.
     */
    @Test
    public void testRemove() {
        RequestParameter[] params = new RequestParameter[] {new MockRequestParameter("key", "value1")};
        map.put("key", params);
        assertArrayEquals(params, map.get("key"));
        map.remove("key");
        assertNull(map.get("key"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameterMap#putAll(java.util.Map)}.
     */
    @Test
    public void testPutAll() {
        RequestParameter[] key1Values = new RequestParameter[] {new MockRequestParameter("key1", "value1")};
        RequestParameter[] key2Values = new RequestParameter[] {new MockRequestParameter("key2", "value2")};
        map.putAll(Map.of("key1", key1Values, "key2", key2Values));
        assertArrayEquals(key1Values, map.get("key1"));
        assertArrayEquals(key2Values, map.get("key2"));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameterMap#clear()}.
     */
    @Test
    public void testClear() {
        RequestParameter[] params = new RequestParameter[] {new MockRequestParameter("key", "value1")};
        map.put("key", params);
        assertArrayEquals(params, map.get("key"));
        map.clear();
        assertTrue(map.isEmpty());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameterMap#keySet()}.
     */
    @Test
    public void testKeySet() {
        assertNotNull(map.keySet());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameterMap#values()}.
     */
    @Test
    public void testValues() {
        assertNotNull(map.values());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameterMap#entrySet()}.
     */
    @Test
    public void testEntrySet() {
        assertNotNull(map.entrySet());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.MockRequestParameterMap#equals(java.lang.Object)}.
     */
    @Test
    public void testEqualsObject() {
        assertFalse(map.equals(this)); // NOSONAR
    }
}
