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

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.Test.None;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @deprecated Use {@link JakartaServletInternalRequestTest} instead.
 */
@Deprecated(since = "2.0.0")
public class ServletInternalRequestTest {
    protected ResourceResolver resourceResolver;

    protected final InternalRequest request(String path) {
        return request(path, null, null);
    }

    protected InternalRequest request(String path, String resourceType, String resourceSuperType) {
        return new ServletInternalRequest(
                new MockServletResolver(),
                new ServletResolutionResource(resourceResolver, path, resourceType, resourceSuperType));
    }

    @Before
    public void setup() {
        resourceResolver = Mockito.mock(ResourceResolver.class);
        Mockito.when(resourceResolver.getAttribute(Mockito.any(String.class))).thenReturn("RR_attribute");
    }

    @Test
    public void minimalParameters() throws IOException {
        assertEquals(
                "M_GET PI_/monday RPI_EXT_null RPI_SEL_null RPI_P_/monday RT_null RST_null RRA_RR_attribute CT_null P_{} B_",
                request("/monday").execute().getResponseAsString());
    }

    @Test
    public void allOptions() throws IOException {
        final Map<String, Object> params = new HashMap<>();
        params.put("A", "alpha");
        params.put("B", "bravo");

        final String content = request("/451", "quincy", "jones")
                .withSelectors("leo", "nardo")
                .withExtension("davinci")
                .withParameter("K", "willBeOverwritten")
                .withParameter("K", "kilo")
                .withParameters(params)
                .withContentType("the/type")
                .execute()
                .checkStatus(200)
                .checkResponseContentType("CT_the/type")
                .getResponseAsString();

        // Verify that the servlet that got called (our RequestInfoServlet)
        // got all the objects and values that influence servlet/script resolution
        assertEquals(
                "M_GET PI_/451.leo.nardo.davinci RPI_EXT_davinci RPI_SEL_leo.nardo RPI_P_/451 RT_quincy RST_jones RRA_RR_attribute CT_the/type P_{A=[alpha], B=[bravo], K=[kilo]} B_",
                content);
    }

    @Test
    public void postMethodWithBody() throws IOException {
        assertEquals(
                "M_POST PI_/tuesday RPI_EXT_null RPI_SEL_null RPI_P_/tuesday RT_null RST_null RRA_RR_attribute CT_null P_{} B_the body",
                request("/tuesday")
                        .withRequestMethod("post")
                        .withBody(new StringReader("the body"))
                        .execute()
                        .getResponseAsString());
    }

    @Test(expected = None.class)
    public void nullBody() throws IOException {
        request("/nullbody").withRequestMethod("post").withBody(null).execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullParamKey() throws IOException {
        request("/nullparamKey").withParameter(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullParamValue() throws IOException {
        request("/nullparamValue").withParameter("key", null);
    }

    @Test
    public void nullParamsAreIgnored() throws IOException {
        assertEquals(
                "M_GET PI_/nullparams RPI_EXT_null RPI_SEL_null RPI_P_/nullparams RT_null RST_null RRA_RR_attribute CT_null P_{} B_",
                request("/nullparams").withParameters(null).execute().getResponseAsString());
    }

    @Test(expected = IOException.class)
    public void doubleExecute() throws IOException {
        request("/never").execute().execute();
    }

    @Test(expected = IOException.class)
    public void servletIOException() throws IOException {
        request("/EXCEPTION").execute();
    }

    @Test(expected = IOException.class)
    public void servletServletException() throws IOException {
        request("/SERVLET-EXCEPTION").execute();
    }

    @Test
    public void non200Status() throws IOException {
        final InternalRequest req =
                request("/never").withRequestMethod("STATUS").execute();
        try {
            req.checkStatus(200);
            fail("Expecting status check to fail");
        } catch (IOException asExpected) {
        }
        assertTrue(
                "Expecting non-200 status to return no content",
                req.getResponseAsString().isEmpty());
    }

    @Test
    public void specificStatus() throws IOException {
        request("/never").withRequestMethod("STATUS").execute().checkStatus(451);
    }

    @Test
    public void specificStatusOutOfSeveral() throws IOException {
        request("/never").withRequestMethod("STATUS").execute().checkStatus(41, 42, 451, 1234);
    }

    @Test
    public void implicitStatusCheck() throws IOException {
        final InternalRequest r = request("/ignore").withRequestMethod("STATUS").execute();

        final String msg = "Expecting an IOException - status wasn't checked and not 200";

        // These shouldn't fail, even if we haven't checked the status
        r.getStatus();
        r.checkResponseContentType("farenheit");

        // But other methods that access the response fail if we haven't checked the status before
        // and it's not 200
        try {
            r.getResponseAsString();
            fail(msg);
        } catch (IOException asExpected) {
        }

        try {
            r.getResponse();
            fail(msg);
        } catch (IOException asExpected) {
        }
    }

    @Test
    public void ignoreNon200Status() throws IOException {
        final InternalRequest r =
                request("/ignore").withRequestMethod("STATUS").execute().checkStatus();

        assertEquals(451, r.getStatus());

        // This doesn't fail as we have called checkStatus() with no
        // arguments, meaning "I don't care"
        r.getResponseAsString();
    }

    @Test
    public void ignoreNon200StatusWithNull() throws IOException {
        final InternalRequest r =
                request("/ignoreAgain").withRequestMethod("STATUS").execute().checkStatus(null);
        assertEquals(451, r.getStatus());

        // This doesn't fail as we have called checkStatus() with
        // null, also meaning "I don't care"
        r.getResponseAsString();
    }

    @Test
    public void nullSelectors() throws IOException {
        final String[] theyAreNull = null;
        assertEquals(
                "M_GET PI_/nothing RPI_EXT_null RPI_SEL_null RPI_P_/nothing RT_null RST_null RRA_RR_attribute CT_null P_{} B_",
                request("/nothing").withSelectors(theyAreNull).execute().getResponseAsString());
    }

    @Test
    public void contentTypeMismatch() throws IOException {
        final InternalRequest req = request("/contentType").execute().checkResponseContentType("CT_null");
        try {
            req.checkResponseContentType("not/this");
            fail("Expecting content type check to fail");
        } catch (IOException asExpected) {
        }
    }

    @Test
    public void responseProvided() throws IOException {
        assertEquals(200, request("/response").execute().getResponse().getStatus());
    }

    @Test(expected = IOException.class)
    public void forgotToExecute() throws IOException {
        request("/response").getResponseAsString();
    }

    @Test
    public void noServletReturns404() throws IOException {
        request("/NOSERVLET").execute().checkStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void checkStatusCodeReturn() throws IOException {
        InternalRequest call = request("/NOSERVLET").execute().checkStatus(HttpServletResponse.SC_NOT_FOUND);
        assertEquals("Unexpected Status Code", HttpServletResponse.SC_NOT_FOUND, call.getStatus());
    }

    @Test
    public void checkMultipleStatusCodeReturn() throws IOException {
        InternalRequest call =
                request("/response").execute().checkStatus(HttpServletResponse.SC_OK, HttpServletResponse.SC_NOT_FOUND);
        assertEquals("Unexpected Status Code", HttpServletResponse.SC_OK, call.getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckNull() {
        request(null);
    }
}
