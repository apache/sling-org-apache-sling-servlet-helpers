[<img src="https://sling.apache.org/res/logos/sling.png"/>](https://sling.apache.org)

 [![Build Status](https://builds.apache.org/buildStatus/icon?job=Sling/sling-org-apache-sling-servlet-helpers/master)](https://builds.apache.org/job/Sling/job/sling-org-apache-sling-servlet-helpers/job/master) [![Test Status](https://img.shields.io/jenkins/t/https/builds.apache.org/job/Sling/job/sling-org-apache-sling-servlet-helpers/job/master.svg)](https://builds.apache.org/job/Sling/job/sling-org-apache-sling-servlet-helpers/job/master/test_results_analyzer/) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.sling/org.apache.sling.servlet-helpers/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.apache.sling%22%20a%3A%22org.apache.sling.servlet-helpers%22) [![JavaDocs](https://www.javadoc.io/badge/org.apache.sling/org.apache.sling.servlet-helpers.svg)](https://www.javadoc.io/doc/org.apache.sling/org.apache.sling.servlet-helpers) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Apache Sling Servlet Helpers

This module is part of the [Apache Sling](https://sling.apache.org) project.

It provides mock implementations of the `SlingHttpServletRequest`, `SlingHttpServletRepsonse` and related classes.

These helpers can be used for **testing**, like the [Sling Mocks](https://sling.apache.org/documentation/development/sling-mock.html) do.

They are also useful for **internal requests using the `SlingRequestProcessor` service**, like in the
[GraphQL Core](https://github.com/apache/sling-org-apache-sling-graphql-core/) module which uses
that technique to retrieve GraphQL schemas using the powerful Sling request processing mechanisms.

## How about a fluent API?

Internal requests using the `SlingRequestProcessor` service currently require a lot of boilerplate
code, which we could remove by creating a fluent interface that manages the required request and response
objects under the hood.

Maybe something like:

    InternalRequest
      .get(SlingRequestProcessor, ResourceResolver)
      .forPath("/yocats")
      .withSelectors("farenheit", "451")
      .withExtension("json")
      .execute()
      .checkStatus(200)
      .checkContentType("application/json")
      .getContentAsStream();
      
This is just an idea so far...patches welcome, and maybe such an API doesn't belong in this module, but 
would just use its helpers.
