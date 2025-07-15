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

import javax.servlet.http.Cookie;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manages cookies for request and response.
 *
 * @deprecated Use {@link JakartaCookieSupport} instead.
 */
@Deprecated(since = "2.0.0")
class CookieSupport {

    private Map<String, Cookie> cookies = new LinkedHashMap<>();

    public void addCookie(Cookie cookie) {
        cookies.put(cookie.getName(), cookie);
    }

    public Cookie getCookie(String name) {
        return cookies.get(name);
    }

    public Cookie[] getCookies() {
        if (cookies.isEmpty()) {
            return null; // NOSONAR
        } else {
            return cookies.values().toArray(new Cookie[cookies.size()]);
        }
    }

    public void reset() {
        cookies.clear();
    }
}
