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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.sling.api.request.RequestParameter;

/**
 * Mock implementation of {@link RequestParameter}.
 */
class MockRequestParameter implements RequestParameter {

    private String name;
    private String encoding = "UTF-8";
    private String value;
    private String contentType;
    private boolean isFormField;
    private String filename;

    private byte[] content;

    public MockRequestParameter(String name, String value) {
        this.name = name;
        this.value = value;
        this.content = null;
        this.contentType = "text/plain";
        this.isFormField = true;
        this.filename = null;
    }
    
    public MockRequestParameter(String name, byte[] content, String contentType) {
        this.name = name;
        this.value = null;
        this.content = content;
        this.contentType = contentType;
        this.isFormField = false;
        this.filename = null;
    }
    
    public MockRequestParameter(String name, byte[] content, String contentType, String filename) {
        this.name = name;
        this.value = null;
        this.content = content;
        this.contentType = contentType;
        this.isFormField = false;
        this.filename = filename;
    }
    
    void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public byte[] get() {
        if (this.content == null && this.value != null) {
            try {
            	this.content = getString().getBytes(getEncoding());
            } catch (Exception e) {
                // UnsupportedEncodingException, IllegalArgumentException
            	this.content = getString().getBytes();
            }
        }
        return this.content;
    }

    public String getContentType() {
        // text/plain for www-form-encoded parameters
        return this.contentType;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.get());
    }

    public String getFileName() {
        return this.filename;
    }

    public long getSize() {
        return this.get().length;
    }

    public String getString() {
        return this.value == null && this.content != null? new String(this.content) : this.value;
    }

    public String getString(String encoding) throws UnsupportedEncodingException {
        return new String(this.get(), encoding);
    }

    public boolean isFormField() {
        return this.isFormField;
    }

    public String toString() {
        return this.getString();
    }

}
