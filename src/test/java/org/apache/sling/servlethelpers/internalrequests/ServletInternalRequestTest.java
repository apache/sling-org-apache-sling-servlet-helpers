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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ServletInternalRequestTest {
    protected ResourceResolver resourceResolver;

    protected InternalRequest request(String path) {
        return InternalRequest.servletRequest(resourceResolver, new MockServletResolver(), path);
    }

    @Before
    public void setup() {
        resourceResolver = Mockito.mock(ResourceResolver.class);
        Mockito.when(resourceResolver.getAttribute(Mockito.any(String.class))).thenReturn("RR_attribute");
    }

    @Test
    public void minimalParameters() throws IOException {
        assertEquals(
            "M_GET PI_/monday RPI_EXT_null RPI_SEL_null RPI_P_/monday RT_null RST_null RRA_RR_attribute", 
            request("/monday").execute().getResponseAsString());
    }

    @Test
    public void allOptions() throws IOException {
        final String content = request("/451")
            .withResourceType("quincy")
            .withResourceSuperType("jones")
            .withSelectors("leo", "nardo")
            .withExtension("davinci")
            .execute()
            .checkResponseContentType("CT_null")
            .getResponseAsString();

        // Verify that the servlet that got called (our RequestInfoServlet)
        // got all the objects and values that influence servlet/script resolution
        assertEquals(
            "M_GET PI_/451.leo.nardo.davinci RPI_EXT_davinci RPI_SEL_leo.nardo RPI_P_/451 RT_quincy RST_jones RRA_RR_attribute",
            content);
    }

    @Test
    public void postMethod() throws IOException {
        assertEquals(
            "M_POST PI_/tuesday RPI_EXT_null RPI_SEL_null RPI_P_/tuesday RT_null RST_null RRA_RR_attribute", 
            request("/tuesday").withRequestMethod("post").execute().getResponseAsString());
    }

    @Test(expected = IOException.class)
    public void doubleExecute() throws IOException {
        request("/never").execute().execute();
    }

    @Test(expected = IOException.class)
    public void servletException() throws IOException {
        request("/never").withRequestMethod("EXCEPTION").execute();
    }

    @Test
    public void non200Status() throws IOException {
        final InternalRequest req = request("/never").withRequestMethod("STATUS");
        try {
            req.execute();
            fail("Expecting status check to fail");
        } catch(IOException asExpected) {
        }
        assertTrue("Expecting non-200 status to return no content", req.getResponseAsString().isEmpty());
    }

    @Test
    public void specificStatus() throws IOException {
        request("/never").withRequestMethod("STATUS").execute(451);
    }

    @Test
    public void ignoreNon200Status() throws IOException {
        final HttpServletResponse r = request("/ignore").withRequestMethod("STATUS").execute(-1).getResponse();
        assertEquals(451, r.getStatus());
    }

    @Test
    public void nullSelectors() throws IOException {
        final String [] theyAreNull = null;
        assertEquals(
            "M_GET PI_/nothing RPI_EXT_null RPI_SEL_null RPI_P_/nothing RT_null RST_null RRA_RR_attribute", 
            request("/nothing").withSelectors(theyAreNull).execute().getResponseAsString()
        );
    }

    @Test
    public void contentTypeMismatch() throws IOException {
        final InternalRequest req = request("/contentType").execute().checkResponseContentType("CT_null");
        try {
            req.checkResponseContentType("not/this");
            fail("Expecting content type check to fail");
        } catch(IOException asExpected) {
        }
    }

    @Test
    public void responseProvided() throws IOException {
        assertNotNull(request("/response").execute().getResponse());
    }

    @Test(expected=IOException.class)
    public void forgotToExecute() throws IOException {
        request("/response").getResponseAsString();
    }
}