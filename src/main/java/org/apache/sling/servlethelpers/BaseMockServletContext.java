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

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

/**
 * Mock base ServletContext implementation.
 */
public abstract class BaseMockServletContext {

    public String getMimeType(final String file) { // NOSONAR
        return "application/octet-stream"; // NOSONAR
    }

    // --- unsupported operations ---

    public Object getAttribute(final String name) {
        throw new UnsupportedOperationException();
    }

    public Enumeration<String> getAttributeNames() {
        throw new UnsupportedOperationException();
    }

    public String getContextPath() {
        throw new UnsupportedOperationException();
    }

    public String getInitParameter(final String name) {
        throw new UnsupportedOperationException();
    }

    public Enumeration<String> getInitParameterNames() {
        throw new UnsupportedOperationException();
    }

    public int getMajorVersion() {
        throw new UnsupportedOperationException();
    }

    public int getMinorVersion() {
        throw new UnsupportedOperationException();
    }

    public String getRealPath(final String pPath) {
        throw new UnsupportedOperationException();
    }

    public URL getResource(final String pPath) {
        throw new UnsupportedOperationException();
    }

    public InputStream getResourceAsStream(final String path) {
        throw new UnsupportedOperationException();
    }

    public Set<String> getResourcePaths(final String path) {
        throw new UnsupportedOperationException();
    }

    public String getServerInfo() {
        throw new UnsupportedOperationException();
    }

    public String getServletContextName() {
        throw new UnsupportedOperationException();
    }

    public void log(final String msg) {
        throw new UnsupportedOperationException();
    }

    public void log(final String msg, final Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    public void removeAttribute(final String name) {
        throw new UnsupportedOperationException();
    }

    public void setAttribute(final String name, final Object object) {
        throw new UnsupportedOperationException();
    }

    public int getEffectiveMajorVersion() {
        throw new UnsupportedOperationException();
    }

    public int getEffectiveMinorVersion() {
        throw new UnsupportedOperationException();
    }

    public boolean setInitParameter(final String name, final String value) {
        throw new UnsupportedOperationException();
    }

    public void addListener(final String pClassName) {
        throw new UnsupportedOperationException();
    }

    public ClassLoader getClassLoader() {
        throw new UnsupportedOperationException();
    }

    public void declareRoles(final String... roleNames) {
        throw new UnsupportedOperationException();
    }

    public String getVirtualServerName() {
        throw new UnsupportedOperationException();
    }

    public int getSessionTimeout() {
        throw new UnsupportedOperationException();
    }

    public void setSessionTimeout(int sessionTimeout) {
        throw new UnsupportedOperationException();
    }

    public String getRequestCharacterEncoding() {
        throw new UnsupportedOperationException();
    }

    public void setRequestCharacterEncoding(String encoding) {
        throw new UnsupportedOperationException();
    }

    public String getResponseCharacterEncoding() {
        throw new UnsupportedOperationException();
    }

    public void setResponseCharacterEncoding(String encoding) {
        throw new UnsupportedOperationException();
    }
}
