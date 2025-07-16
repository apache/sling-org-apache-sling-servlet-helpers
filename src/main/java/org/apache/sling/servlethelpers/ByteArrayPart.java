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

import javax.servlet.http.Part;

/**
 * Simple Part implementation backed by an in-memory byte array
 *
 * @deprecated Use {@link JakartaByteArrayPart} instead.
 */
@Deprecated(since = "2.0.0")
public class ByteArrayPart extends BaseByteArrayPart implements Part {

    /**
     * Returns a Builder instance used to create a ByteArrayPart
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    private ByteArrayPart(byte[] content, String name) {
        super(content, name);
    }

    public static class Builder extends BaseBuilder<ByteArrayPart> {

        public ByteArrayPart build() {
            return new ByteArrayPart(content, name);
        }

    }
}
