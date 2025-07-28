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

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.engine.SlingRequestProcessor;
import org.apache.sling.servlethelpers.internalrequests.JakartaSlingInternalRequest;
import org.apache.sling.testing.paxexam.TestSupport;
import org.junit.Before;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.options.ModifiableCompositeOption;
import org.ops4j.pax.exam.options.extra.VMOption;

import static org.apache.sling.testing.paxexam.SlingOptions.paxLoggingApi;
import static org.apache.sling.testing.paxexam.SlingOptions.slingQuickstartOakTar;
import static org.apache.sling.testing.paxexam.SlingOptions.versionResolver;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
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

    @SuppressWarnings("deprecation")
    @Configuration
    public Option[] configuration() {
        // SLING-12858 - newer version of sling.api and dependencies
        //   may remove at a later date if the superclass includes these versions or later
        versionResolver.setVersionFromProject("org.apache.sling", "org.apache.sling.api");
        versionResolver.setVersionFromProject("org.apache.sling", "org.apache.sling.engine");
        versionResolver.setVersionFromProject("org.apache.commons", "commons-collections4");
        versionResolver.setVersionFromProject("org.apache.commons", "commons-lang3");
        versionResolver.setVersion("org.apache.felix", "org.apache.felix.http.servlet-api", "6.1.0");
        versionResolver.setVersion("org.apache.sling", "org.apache.sling.resourceresolver", "2.0.0");
        versionResolver.setVersion("org.apache.sling", "org.apache.sling.auth.core", "2.0.0");
        versionResolver.setVersion("commons-fileupload", "commons-fileupload", "1.6.0");
        versionResolver.setVersion("org.apache.sling", "org.apache.sling.scripting.spi", "2.0.0");
        versionResolver.setVersion("org.apache.sling", "org.apache.sling.scripting.core", "3.0.0");
        versionResolver.setVersion("org.apache.sling", "org.apache.sling.servlets.resolver", "3.0.0");

        return options(composite(
                super.baseConfiguration(),
                vmOption(System.getProperty("pax.vm.options")),
                optionalRemoteDebug(),
                slingQuickstart(),
                paxLoggingApi(), // newer version to provide the 2.x version of slf4j
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("INFO"),
                junitBundles(),
                // SLING-12858 - begin extra bundles for sling api 3.x
                mavenBundle()
                        .groupId("org.apache.felix")
                        .artifactId("org.apache.felix.http.wrappers")
                        .version("6.1.0"),
                mavenBundle()
                        .groupId("org.apache.sling")
                        .artifactId("org.apache.sling.commons.johnzon")
                        .version("2.0.0"),
                // end extra bundles for sling api 3.x
                testBundle("bundle.filename"),
                buildBundleWithBnd(TestServlet.class, JakartaTestServlet.class),
                newConfiguration("org.apache.sling.jcr.base.internal.LoginAdminWhitelist")
                        .put("whitelist.bundles.regexp", "^PAXEXAM.*$")
                        .asOption()));
    }

    /**
     * Optionally configure remote debugging on the port supplied by the "debugPort"
     * system property.
     */
    protected ModifiableCompositeOption optionalRemoteDebug() {
        VMOption option = null;
        String property = System.getProperty("debugPort");
        if (property != null) {
            option = vmOption(String.format("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=%s", property));
        }
        return composite(option);
    }

    @SuppressWarnings("deprecation")
    @Before
    public void setup() throws Exception {
        adminResourceResolver = rrFactory.getAdministrativeResourceResolver(null);
        rootResource = adminResourceResolver.getResource("/");
        assertNotNull("Expecting root resource", rootResource);

        // Wait for Sling to be ready
        // detecting services would be more elegant...but this is very reliable
        final String path = "/";
        waitForCondition(path, p -> {
            try {
                return new org.apache.sling.servlethelpers.internalrequests.SlingInternalRequest(
                                adminResourceResolver, slingRequestProcessor, p)
                        .withExtension("json")
                        .execute()
                        .checkStatus()
                        .getStatus();
            } catch (IOException e) {
                return -1;
            }
        });
        waitForCondition(path, p -> {
            try {
                return new JakartaSlingInternalRequest(adminResourceResolver, slingRequestProcessor, p)
                        .withExtension("json")
                        .execute()
                        .checkStatus()
                        .getStatus();
            } catch (IOException e) {
                return -1;
            }
        });
    }

    protected void waitForCondition(String path, Function<String, Integer> checkFn) throws InterruptedException {
        final Instant endTime = Instant.now().plus(Duration.ofSeconds(STARTUP_WAIT_SECONDS));
        final int expectedStatus = 200;
        final List<Integer> statuses = new ArrayList<>();
        boolean ok = false;
        while (Instant.now().isBefore(endTime)) {
            final int status = checkFn.apply(path);
            statuses.add(status);
            if (status == expectedStatus) {
                ok = true;
                break;
            }
            Thread.sleep(250); // NOSONAR
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
