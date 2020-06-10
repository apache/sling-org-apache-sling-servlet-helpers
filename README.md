[<img src="https://sling.apache.org/res/logos/sling.png"/>](https://sling.apache.org)

 [![Build Status](https://builds.apache.org/buildStatus/icon?job=Sling/sling-org-apache-sling-servlet-helpers/master)](https://builds.apache.org/job/Sling/job/sling-org-apache-sling-servlet-helpers/job/master) [![Test Status](https://img.shields.io/jenkins/t/https/builds.apache.org/job/Sling/job/sling-org-apache-sling-servlet-helpers/job/master.svg)](https://builds.apache.org/job/Sling/job/sling-org-apache-sling-servlet-helpers/job/master/test_results_analyzer/) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.sling/org.apache.sling.servlet-helpers/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.apache.sling%22%20a%3A%22org.apache.sling.servlet-helpers%22) [![JavaDocs](https://www.javadoc.io/badge/org.apache.sling/org.apache.sling.servlet-helpers.svg)](https://www.javadoc.io/doc/org.apache.sling/org.apache.sling.servlet-helpers) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Apache Sling Servlet Helpers

This module is part of the [Apache Sling](https://sling.apache.org) project.

It provides helper mock implementations of the `SlingHttpServletRequest`, `SlingHttpServletRepsonse` and related classes, along
with helpers for internal Sling requests described below.

These helpers can be used for **testing**, like the [Sling Mocks](https://sling.apache.org/documentation/development/sling-mock.html) do.

They are also useful for **executing internal requests**, like in the
[GraphQL Core](https://github.com/apache/sling-org-apache-sling-graphql-core/) module which uses
that technique to retrieve GraphQL schemas using the powerful Sling request processing mechanisms.

## InternalRequest helpers

The `InternalRequest` class uses either a `SlingRequestProcessor` to execute internal requests using
the full Sling request processing pipeline, or a `ServletResolver` to resolve and call a Servlet or Script
directly. 

The direct mode is more efficient but less faithful to the way HTTP requests are processed, as it bypasses
all Servlet Filters, in particular.

In both cases, the standard Sling Servlet/Script resolution mechanism is used, which can be useful to execute
scripts that are resolved based on the current resource type, for non-HTTP operations. Inventing HTTP method
names for this is fine and allows for reusing this powerful resolution mechanism in other contexts.

Here's an example using this `InternalRequest` helper - see the test code for more.

    OutputStream os = InternalRequest
      .servletRequest(resourceResolver, servletResolver, "/some/path")
      .withResourceType("website/article/news")
      .withResourceSuperType("website/article")
      .withSelectors("print", "a4")
      .withExtension("pdf")
      .execute()
      .checkStatus(200)
      .checkResponseContentType("application/pdf")
      .getResponse()
      .getOutputStream()
