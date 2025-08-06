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
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 */
public class ResponseBodySupportTest {

    private ResponseBodySupport responseBodySupport = new ResponseBodySupport();

    /**
     * Test method for {@link org.apache.sling.servlethelpers.ResponseBodySupport#reset()}.
     */
    @Test
    public void testReset() {
        String charset = StandardCharsets.UTF_8.name();
        responseBodySupport.getWriter(charset).append("Hello");
        assertEquals("Hello", responseBodySupport.getOutputAsString(charset));
        responseBodySupport.reset();
        assertEquals("", responseBodySupport.getOutputAsString(charset));
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.ResponseBodySupport#getOutputStream()}.
     */
    @Test
    public void testGetOutputStream() {
        ServletOutputStream outputStream1 = responseBodySupport.getOutputStream();
        assertNotNull(outputStream1);
        ServletOutputStream outputStream2 = responseBodySupport.getOutputStream();
        assertSame("Expected the same output stream", outputStream1, outputStream2);
    }

    @Test
    public void testGetOutputStreamIsReady() {
        ServletOutputStream outputStream1 = responseBodySupport.getOutputStream();
        assertNotNull(outputStream1);
        assertTrue(outputStream1.isReady());
    }

    @Test
    public void testGetOutputStreamSetWriteListener() {
        ServletOutputStream outputStream1 = responseBodySupport.getOutputStream();
        assertNotNull(outputStream1);
        assertThrows(UnsupportedOperationException.class, () -> {
            outputStream1.setWriteListener(Mockito.mock(WriteListener.class));
        });
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.ResponseBodySupport#getWriter(java.lang.String)}.
     */
    @Test
    public void testGetWriter() {
        String charset = StandardCharsets.UTF_8.name();
        PrintWriter writer1 = responseBodySupport.getWriter(charset);
        assertNotNull(writer1);
        PrintWriter writer2 = responseBodySupport.getWriter(charset);
        assertSame("Expected the same writer", writer2, writer1);
    }

    @Test
    public void testGetWriterWithInvalidCharset() {
        boolean encodingExceptionCaught = false;
        try {
            responseBodySupport.getWriter("invalid");
        } catch (RuntimeException e) {
            // expected
            assertTrue(e.getMessage().startsWith("Unsupported encoding:"));
            encodingExceptionCaught = true;
        }
        if (!encodingExceptionCaught) {
            fail("Expected Unsupported encoding exception");
        }
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.ResponseBodySupport#getOutput()}.
     */
    @Test
    public void testGetOutput() {
        String charset = StandardCharsets.UTF_8.name();
        responseBodySupport.getWriter(charset).append("Hello");
        assertArrayEquals("Hello".getBytes(), responseBodySupport.getOutput());
    }

    @Test
    public void testGetOutputWithIgnoredIOException() throws IOException {
        ServletOutputStream outputStream = responseBodySupport.getOutputStream();
        ServletOutputStream mockOutputStream = Mockito.spy(outputStream);
        Mockito.doThrow(IOException.class).when(mockOutputStream).flush();
        ReflectionTools.setFieldWithReflection(responseBodySupport, "servletOutputStream", mockOutputStream);
        outputStream.print("Hello");
        assertArrayEquals("Hello".getBytes(), responseBodySupport.getOutput());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.ResponseBodySupport#getOutputAsString(java.lang.String)}.
     */
    @Test
    public void testGetOutputAsString() {
        String charset = StandardCharsets.UTF_8.name();
        responseBodySupport.getWriter(charset).append("Hello");
        assertEquals("Hello", responseBodySupport.getOutputAsString(charset));
    }

    @Test
    public void testGetOutputAsStringWithInvalidCharset() {
        String charset = StandardCharsets.UTF_8.name();
        responseBodySupport.getWriter(charset).append("Hello");
        boolean encodingExceptionCaught = false;
        try {
            assertEquals("Hello", responseBodySupport.getOutputAsString("invalid"));
        } catch (RuntimeException e) {
            // expected
            assertTrue(e.getMessage().startsWith("Unsupported encoding:"));
            encodingExceptionCaught = true;
        }
        if (!encodingExceptionCaught) {
            fail("Expected Unsupported encoding exception");
        }
    }
}
