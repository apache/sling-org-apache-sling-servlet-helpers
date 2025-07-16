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

import java.util.Collection;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.sling.api.adapter.SlingAdaptable;

/**
 * Mock Sling HttpServletResponse implementation.
 */
public class BaseMockSlingHttpServletResponse extends SlingAdaptable {

    static final String CHARSET_SEPARATOR = ";charset=";

    private String contentType;
    private String characterEncoding;
    protected int contentLength;
    protected int status;
    private String statusMessage;
    private int bufferSize = 1024 * 8;
    private boolean isCommitted;
    private Locale locale = Locale.US;
    protected final HeaderSupport headerSupport = new HeaderSupport();

    public String getContentType() {
        if (this.contentType == null) {
            return null;
        } else {
            return this.contentType
                    + (StringUtils.isNotBlank(characterEncoding) ? CHARSET_SEPARATOR + characterEncoding : "");
        }
    }

    public void setContentType(String type) {
        this.contentType = type;
        if (Strings.CS.contains(this.contentType, CHARSET_SEPARATOR)) {
            this.characterEncoding = StringUtils.substringAfter(this.contentType, CHARSET_SEPARATOR);
            this.contentType = StringUtils.substringBefore(this.contentType, CHARSET_SEPARATOR);
        }
    }

    public void setCharacterEncoding(String charset) {
        this.characterEncoding = charset;
    }

    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public void setContentLength(int len) {
        this.contentLength = len;
    }

    public int getContentLength() {
        return this.contentLength;
    }

    public void setStatus(int sc, String sm) {
        setStatus(sc);
        this.statusMessage = sm;
    }

    public void setStatus(int sc) {
        this.status = sc;
    }

    public int getStatus() {
        return this.status;
    }

    public void sendError(int sc, String msg) {
        setStatus(sc);
        this.statusMessage = msg;
    }

    public void sendError(int sc) {
        setStatus(sc);
    }

    public void addHeader(String name, String value) {
        headerSupport.addHeader(name, value);
    }

    public void addIntHeader(String name, int value) {
        headerSupport.addIntHeader(name, value);
    }

    public void addDateHeader(String name, long date) {
        headerSupport.addDateHeader(name, date);
    }

    public void setHeader(String name, String value) {
        headerSupport.setHeader(name, value);
    }

    public void setIntHeader(String name, int value) {
        headerSupport.setIntHeader(name, value);
    }

    public void setDateHeader(String name, long date) {
        headerSupport.setDateHeader(name, date);
    }

    public boolean containsHeader(String name) {
        return headerSupport.containsHeader(name);
    }

    public String getHeader(String name) {
        return headerSupport.getHeader(name);
    }

    public Collection<String> getHeaders(String name) {
        return headerSupport.getHeaders(name);
    }

    public Collection<String> getHeaderNames() {
        return headerSupport.getHeaderNames();
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public void setBufferSize(int size) {
        this.bufferSize = size;
    }

    public void flushBuffer() {
        isCommitted = true;
    }

    public boolean isCommitted() {
        return isCommitted;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale loc) {
        this.locale = loc;
    }

    /**
     * @return status message that was set using {@link #setStatus(int, String)} or {@link #sendError(int, String)}
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    @Override
    public <T> T adaptTo(Class<T> type) {
        return AdaptableUtil.adaptToWithoutCaching(this, type);
    }

    // --- unsupported operations ---
    public String encodeRedirectURL(String url) {
        throw new UnsupportedOperationException();
    }

    public String encodeURL(String url) {
        throw new UnsupportedOperationException();
    }

    public void setContentLengthLong(long len) {
        throw new UnsupportedOperationException();
    }
}
