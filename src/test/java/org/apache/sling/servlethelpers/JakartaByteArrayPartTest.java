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

import org.junit.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JakartaByteArrayPartTest {

    private static final String TEST_CONTENT = "test input";
    private static final String PART_NAME = "test_part_name";

    @Test
    public void buildPart() throws IOException {
        JakartaByteArrayPart part = JakartaByteArrayPart.builder()
                .withName(PART_NAME)
                .withContent(TEST_CONTENT.getBytes(UTF_8))
                .build();

        assertThat(part).as("part").isNotNull();
        assertThat(part.getName()).as("part name").isEqualTo(PART_NAME);
        assertThat(part.getInputStream()).as("part contents").hasContent(TEST_CONTENT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyBuilderFails() {
        JakartaByteArrayPart.builder().build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingNameFails() {
        JakartaByteArrayPart.builder().withContent(TEST_CONTENT.getBytes(UTF_8)).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyNameFails() {
        JakartaByteArrayPart.builder()
                .withName("")
                .withContent(TEST_CONTENT.getBytes(UTF_8))
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingContentFails() {
        JakartaByteArrayPart.builder().withName("test").build();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.JakartaByteArrayPart#getName()}.
     */
    @Test
    public void testGetName() {
        assertEquals(
                "part",
                JakartaByteArrayPart.builder()
                        .withName("part")
                        .withContent(TEST_CONTENT.getBytes(UTF_8))
                        .build()
                        .getName());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.JakartaByteArrayPart#getContentType()}.
     */
    @Test
    public void testGetContentType() {
        assertNull(JakartaByteArrayPart.builder()
                .withName("part")
                .withContent(TEST_CONTENT.getBytes(UTF_8))
                .build()
                .getContentType());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.JakartaByteArrayPart#getSubmittedFileName()}.
     */
    @Test
    public void testGetSubmittedFileName() {
        assertEquals(
                "part",
                JakartaByteArrayPart.builder()
                        .withName("part")
                        .withContent(TEST_CONTENT.getBytes(UTF_8))
                        .build()
                        .getSubmittedFileName());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.JakartaByteArrayPart#getSize()}.
     */
    @Test
    public void testGetSize() {
        assertEquals(
                10,
                JakartaByteArrayPart.builder()
                        .withName("part")
                        .withContent(TEST_CONTENT.getBytes(UTF_8))
                        .build()
                        .getSize());
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.JakartaByteArrayPart#write(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testWrite() throws IOException {
        JakartaByteArrayPart.builder()
                .withName("part")
                .withContent(TEST_CONTENT.getBytes(UTF_8))
                .build()
                .write("filename");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.JakartaByteArrayPart#delete()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testDelete() throws IOException {
        JakartaByteArrayPart.builder()
                .withName("part")
                .withContent(TEST_CONTENT.getBytes(UTF_8))
                .build()
                .delete();
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.JakartaByteArrayPart#getHeader(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetHeader() {
        JakartaByteArrayPart.builder()
                .withName("part")
                .withContent(TEST_CONTENT.getBytes(UTF_8))
                .build()
                .getHeader("name1");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.JakartaByteArrayPart#getHeaders(java.lang.String)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetHeaders() {
        JakartaByteArrayPart.builder()
                .withName("part")
                .withContent(TEST_CONTENT.getBytes(UTF_8))
                .build()
                .getHeaders("name");
    }

    /**
     * Test method for {@link org.apache.sling.servlethelpers.JakartaByteArrayPart#getHeaderNames()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetHeaderNames() {
        JakartaByteArrayPart.builder()
                .withName("part")
                .withContent(TEST_CONTENT.getBytes(UTF_8))
                .build()
                .getHeaderNames();
    }
}
