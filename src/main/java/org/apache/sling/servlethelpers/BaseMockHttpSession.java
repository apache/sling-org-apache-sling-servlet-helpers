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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.IteratorUtils;

/**
 * Mock HttpSession implementation.
 */
public abstract class BaseMockHttpSession {

    private final Map<String, Object> attributeMap = new HashMap<>();
    private final String sessionID = UUID.randomUUID().toString();
    private final long creationTime = System.currentTimeMillis();
    private boolean invalidated = false;
    private boolean isNew = true;
    private int maxActiveInterval = 1800;

    public Object getAttribute(final String name) {
        checkInvalidatedState();
        return this.attributeMap.get(name);
    }

    public Enumeration<String> getAttributeNames() {
        checkInvalidatedState();
        return IteratorUtils.asEnumeration(this.attributeMap.keySet().iterator());
    }

    public String getId() {
        return this.sessionID;
    }

    public long getCreationTime() {
        checkInvalidatedState();
        return this.creationTime;
    }

    public Object getValue(final String name) {
        checkInvalidatedState();
        return getAttribute(name);
    }

    public String[] getValueNames() {
        checkInvalidatedState();
        return this.attributeMap
                .keySet()
                .toArray(new String[this.attributeMap.keySet().size()]);
    }

    public void putValue(final String name, final Object value) {
        checkInvalidatedState();
        setAttribute(name, value);
    }

    public void removeAttribute(final String name) {
        checkInvalidatedState();
        this.attributeMap.remove(name);
    }

    public void removeValue(final String name) {
        removeAttribute(name);
    }

    public void setAttribute(final String name, final Object value) {
        checkInvalidatedState();
        this.attributeMap.put(name, value);
    }

    public void invalidate() {
        checkInvalidatedState();
        this.invalidated = true;
    }

    private void checkInvalidatedState() {
        if (invalidated) {
            throw new IllegalStateException("Session is already invalidated.");
        }
    }

    public boolean isInvalidated() {
        return invalidated;
    }

    public boolean isNew() {
        checkInvalidatedState();
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public long getLastAccessedTime() {
        checkInvalidatedState();
        return creationTime;
    }

    public int getMaxInactiveInterval() {
        return maxActiveInterval;
    }

    public void setMaxInactiveInterval(final int interval) {
        this.maxActiveInterval = interval;
    }
}
