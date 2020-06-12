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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/** Run the same tests as the ServletInternalRequestTest but in "sling" mode */
public class SlingInternalRequestTest extends ServletInternalRequestTest {
    protected InternalRequest request(String path, String resourceType, String resourceSuperType) {
        return new SlingInternalRequest(resourceResolver, new MockSlingRequestProcessor(), path)
            .withResourceType(resourceType)
            .withResourceSuperType(resourceSuperType)
        ;
    }

    @Test
    public void verifyClassUnderTestNoParams() {
        assertEquals(SlingInternalRequest.class, request("unused").getClass());
    }

    @Test
    public void verifyClassUnderTestWithParams() {
        assertEquals(SlingInternalRequest.class, request("unused", "with", "resourceType").getClass());
    }
}
