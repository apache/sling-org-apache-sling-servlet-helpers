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
package org.apache.sling.servlethelpers.internalrequests;

import java.util.UUID;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class ServletResolutionResourceTest {
    private final String id = UUID.randomUUID().toString();
    private final String path = "P_" + id;
    private final String resourceType = "RT_" + id;
    private final String resourceSuperType = "RST_" + id;
    private final ResourceResolver resourceResolver = Mockito.mock(ResourceResolver.class);
    private final ServletResolutionResource r =
            new ServletResolutionResource(resourceResolver, path, resourceType, resourceSuperType);

    @Test
    public void testImplementedMethods() {
        assertEquals(path, r.getPath());
        assertEquals(resourceType, r.getResourceType());
        assertEquals(resourceSuperType, r.getResourceSuperType());
        assertEquals(resourceResolver, r.getResourceResolver());
    }

    @Test(expected = ServletResolutionResource.NotImplementedException.class)
    public void testAdaptTo() {
        r.adaptTo(Object.class);
    }

    @Test(expected = ServletResolutionResource.NotImplementedException.class)
    public void testGetName() {
        r.getName();
    }

    @Test(expected = ServletResolutionResource.NotImplementedException.class)
    public void testGetParent() {
        r.getParent();
    }

    @Test(expected = ServletResolutionResource.NotImplementedException.class)
    public void testListChildren() {
        r.listChildren();
    }

    @Test(expected = ServletResolutionResource.NotImplementedException.class)
    public void testGetChildren() {
        r.getChildren();
    }

    @Test(expected = ServletResolutionResource.NotImplementedException.class)
    public void testGetChild() {
        r.getChild("451");
    }

    @Test(expected = ServletResolutionResource.NotImplementedException.class)
    public void testIsResourceType() {
        r.isResourceType("farenheit");
    }

    @Test(expected = ServletResolutionResource.NotImplementedException.class)
    public void testResourceMetadata() {
        r.getResourceMetadata();
    }
}
