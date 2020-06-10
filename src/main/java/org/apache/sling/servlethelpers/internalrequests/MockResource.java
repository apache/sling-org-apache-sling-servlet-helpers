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

import java.util.Iterator;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;

/** Minimal Resource implementation for our internal requests */
class MockResource implements Resource {

    private final String path;
    private final String resourceType;
    private final String resourceSuperType;
    private final ResourceResolver resourceResolver;

    static class NotNeededException extends UnsupportedOperationException {
        private static final long serialVersionUID = 1L;

        NotNeededException() {
            super("Not implemented - this method should not be needed?");
        }
    }

    MockResource(ResourceResolver resourceResolver, String path, String resourceType, String resourceSuperType) {
        this.path = path;
        this.resourceType = resourceType;
        this.resourceSuperType = resourceSuperType;
        this.resourceResolver = resourceResolver;
    }

    @Override
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        throw new NotNeededException();
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getName() {
        throw new NotNeededException();
    }

    @Override
    public Resource getParent() {
        throw new NotNeededException();
    }

    @Override
    public Iterator<Resource> listChildren() {
        throw new NotNeededException();
    }

    @Override
    public Iterable<Resource> getChildren() {
        throw new NotNeededException();
    }

    @Override
    public Resource getChild(String relPath) {
        throw new NotNeededException();
    }

    @Override
    public String getResourceType() {
        return resourceType;
    }

    @Override
    public String getResourceSuperType() {
        return resourceSuperType;
    }

    @Override
    public boolean isResourceType(String resourceType) {
        throw new NotNeededException();
    }

    @Override
    public ResourceMetadata getResourceMetadata() {
        throw new NotNeededException();
    }

    @Override
    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }
}