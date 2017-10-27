=== Web Drivers ===

The following properties can be specified to control web drivers.

==> web.driver # valid values are chrome, firefox, htmlunit, phantom, remote_chrome and remote_firefox

Indicates the web driver to be used for Selenium tests.

The property is optional, and has the following precedence -

1) A system property (eg. -Dweb.driver=chrome)
2) A property injected via Spring
3) Not supplied (in which case the the value will default to 'firefox')

The remote_* drivers are for use with Selenium Grid which should be configured separately.

==> web.batch.admin.remote.hub (eg. http://localhost:4444/wd/hub)

Specifies the address of the Selenium Grid hub - only to be used when testing with Selenium Grid

The property is optional, and has the following precedence -

1) A property injected via Spring
2) Not supplied (in which case the the value will default to an empty string)

Upgrade to Selenium version 2.53.0 -

1) Latest version provides supports to higher Firefox version (upto version -> 47.0)
2) Please note that this version of selenium does not bundle HtmlUnit driver and to provide support for htmlUnit driver, maven dependency
    of htmlunit-driver (version -> 2.23.1) is added in the pom.xml
3) Post consuming latest cbs-web-acceptance-common jar in case you face issue stating "java.lang.ClassNotFoundException:org.w3c.dom.ElementTraversal"
    please added below xml-api dependency support in your pom.xml
            <dependency>
                <groupId>xml-apis</groupId>
                <artifactId>xml-apis</artifactId>
                <version>1.4.01</version>
            </dependency>
3) Post consuming latest cbs-web-acceptance-common jar in case you face issue stating "java.lang.ClassNotFoundException:org.org.eclipse.jetty.websocket>Session"
    please added below xml-api dependency support in your pom.xml
        <dependency>
            <groupId>org.eclipse.jetty.websocket</groupId>
            <artifactId>websocket-api</artifactId>
            <version>9.2.15.v20160210</version>
            <scope>provided</scope>
        </dependency>