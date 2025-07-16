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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Simple Part implementation backed by an in-memory byte array
 */
public abstract class BaseByteArrayPart {

    private final byte[] content;
    private final String name;

    BaseByteArrayPart(byte[] content, String name) {
        if (content == null) throw new IllegalArgumentException("content may not be null");

        if (name == null || name.isEmpty()) throw new IllegalArgumentException("name may not be null or empty");

        this.content = content;
        this.name = name;
    }

    public InputStream getInputStream() throws IOException { // NOSONAR
        return new ByteArrayInputStream(content);
    }

    public String getContentType() {
        return null;
    }

    public String getName() {
        return name;
    }

    public String getSubmittedFileName() {
        return getName();
    }

    public long getSize() {
        return content.length;
    }

    public void write(String fileName) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void delete() throws IOException {
        throw new UnsupportedOperationException();
    }

    public String getHeader(String name) {
        throw new UnsupportedOperationException();
    }

    public Collection<String> getHeaders(String name) {
        throw new UnsupportedOperationException();
    }

    public Collection<String> getHeaderNames() {
        throw new UnsupportedOperationException();
    }
}
