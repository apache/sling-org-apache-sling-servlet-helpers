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

public class ByteArrayPartTest {

    private static final String TEST_CONTENT = "test input";
    private static final String PART_NAME = "test_part_name";

    @Test
    public void buildPart() throws IOException {
        ByteArrayPart part = ByteArrayPart.builder()
                .withName(PART_NAME)
                .withContent(TEST_CONTENT.getBytes(UTF_8))
                .build();

        assertThat(part).as("part").isNotNull();
        assertThat(part.getName()).as("part name").isEqualTo(PART_NAME);
        assertThat(part.getInputStream()).as("part contents").hasContent(TEST_CONTENT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyBuilderFails() {
        ByteArrayPart.builder().build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingNameFails() {
        ByteArrayPart.builder().withContent(TEST_CONTENT.getBytes(UTF_8)).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyNameFails() {
        ByteArrayPart.builder()
                .withName("")
                .withContent(TEST_CONTENT.getBytes(UTF_8))
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingContentFails() {
        ByteArrayPart.builder().withName("test").build();
    }
}
