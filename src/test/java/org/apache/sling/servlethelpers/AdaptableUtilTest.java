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

import org.apache.sling.api.adapter.AdapterManager;
import org.apache.sling.api.adapter.SlingAdaptable;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 */
public class AdaptableUtilTest {

    /**
     * Test method for {@link org.apache.sling.servlethelpers.AdaptableUtil#adaptToWithoutCaching(java.lang.Object, java.lang.Class)}.
     */
    @Test
    public void testAdaptToWithoutCachingWithNullAdapterManager() {
        String val = AdaptableUtil.adaptToWithoutCaching("hello", String.class);
        assertNull(val);
    }

    @Test
    public void testAdaptToWithoutCaching() {
        AdapterManager mockAdapterManager = Mockito.mock(AdapterManager.class);
        Mockito.when(mockAdapterManager.getAdapter("hello", String.class)).thenReturn("hello adapted");
        try {
            SlingAdaptable.setAdapterManager(mockAdapterManager);

            String val = AdaptableUtil.adaptToWithoutCaching("hello", String.class);
            assertEquals("hello adapted", val);
        } finally {
            // clear out the mock
            SlingAdaptable.unsetAdapterManager(mockAdapterManager);
        }
    }
}
