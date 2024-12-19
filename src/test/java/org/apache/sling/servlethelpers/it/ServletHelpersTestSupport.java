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
package org.apache.sling.servlethelpers.it;

import javax.inject.Inject;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.engine.SlingRequestProcessor;
import org.apache.sling.servlethelpers.internalrequests.SlingInternalRequest;
import org.apache.sling.testing.paxexam.TestSupport;
import org.junit.Before;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;

import static org.apache.sling.testing.paxexam.SlingOptions.logback;
import static org.apache.sling.testing.paxexam.SlingOptions.slingQuickstartOakTar;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.vmOption;
import static org.ops4j.pax.exam.cm.ConfigurationAdminOptions.newConfiguration;

public abstract class ServletHelpersTestSupport extends TestSupport {

    private static final int STARTUP_WAIT_SECONDS = 30;
    protected Resource rootResource;
    protected ResourceResolver adminResourceResolver;

    @Inject
    protected ResourceResolverFactory rrFactory;

    @Inject
    protected SlingRequestProcessor slingRequestProcessor;

    @Configuration
    public Option[] configuration() {
        return options(composite(
                super.baseConfiguration(),
                vmOption(System.getProperty("pax.vm.options")),
                slingQuickstart(),
                logback(),
                junitBundles(),
                testBundle("bundle.filename"),
                buildBundleWithBnd(TestServlet.class),
                newConfiguration("org.apache.sling.jcr.base.internal.LoginAdminWhitelist")
                        .put("whitelist.bundles.regexp", "^PAXEXAM.*$")
                        .asOption()));
    }

    @Before
    public void setup() throws Exception {
        adminResourceResolver = rrFactory.getAdministrativeResourceResolver(null);
        rootResource = adminResourceResolver.getResource("/");
        assertNotNull("Expecting root resource", rootResource);

        // Wait for Sling to be ready
        // detecting services would be more elegant...but this is very reliable
        final Instant endTime = Instant.now().plus(Duration.ofSeconds(STARTUP_WAIT_SECONDS));
        final int expectedStatus = 200;
        final String path = "/";
        final List<Integer> statuses = new ArrayList<>();
        boolean ok = false;
        while (Instant.now().isBefore(endTime)) {
            final int status = new SlingInternalRequest(adminResourceResolver, slingRequestProcessor, path)
                    .withExtension("json")
                    .execute()
                    .checkStatus()
                    .getStatus();
            statuses.add(status);
            if (status == expectedStatus) {
                ok = true;
                break;
            }
            Thread.sleep(250);
        }

        if (!ok) {
            fail("Did not get a " + expectedStatus + " status at " + path + " got " + statuses);
        }
    }

    protected Option slingQuickstart() {
        final String workingDirectory = workingDirectory();
        final int httpPort = findFreePort();
        return composite(slingQuickstartOakTar(workingDirectory, httpPort));
    }
}
