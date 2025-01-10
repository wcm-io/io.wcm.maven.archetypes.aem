About Maven Archetype for AEM
=============================

Maven Archetype for creating new AEM projects.

[![Maven Central](https://img.shields.io/maven-central/v/io.wcm.maven.archetypes/io.wcm.maven.archetypes.aem)](https://repo1.maven.org/maven2/io/wcm/maven/archetypes/io.wcm.maven.archetypes.aem)


### Documentation

* [Usage][usage]
* [Changelog][changelog]


### Overview

The wcm.io Maven Archetype for AEM allows you to set up new Maven projects for developing AEM applications. The archetype is very flexible and can be customized to your needs using several options.

Features:

* Supports AEM 6.5 (with latest service pack) and AEM as a Cloud Service (AEMaaCS)
* Supports Java 11
* Unit Tests based on JUnit 5 and [AEM Mocks][aem-mock]
* Supports both Sling-Initial-Content JSON-style project layout and FileVault package layout
* Based on [CONGA][conga] to manage AEM configuration and package deployment
* Based on [AEM Core WCM Components][aem-core-wcm-components], using Editable Templates is optional
* Optional inclusion of latest service packs
* Optional integration with a [Webpack][webpack]-based Frontend Build
* Optional support for [Sling Context-Aware Configuration][sling-caconfig] and [wcm.io Context-Aware Configuration Extensions][wcmio-caconfig]
* Optional support for [wcm.io Handler][wcmio-handler] infrastructure and [wcm.io WCM Core Components][wcmio-wcm-core-components]
* Optional support for [ACS AEM Commons][acs-aem-commons]

See [Usage][usage] for a detailed documentation.

If you want to setup AEM infrastructure for deployment of this project you can use the [Maven Archetype for AEM Configuration Management][aem-confmgmt-archetype].


### GitHub Repository

Sources: https://github.com/wcm-io/io.wcm.maven.archetypes.aem


### Further Resources

* [adaptTo() 2018 Talk: Maven Archetypes for AEM][adaptto-talk-2018-aem-archetypes]
* [adaptTo() 2017 Talk: Ease Development with Apache Sling File System Resource Provider][adaptto-talk-2017-fsresource] - about the "Sling-Initial-Content JSON-style" project layout and it's benefits


### Alternatives

Alternatively to the wcm.io AEM archetype you can use these tools to set up new AEM projects:

* [Adobe AEM project archetype][adobe-aem-archetype] - Supports only FileVault package layout and the most recent AEM version
* [ACS AEM Lazybones Templates][acs-aem-lazybones] - Supports only FileVault package layout. Configuration is quite flexible regarding AEM versions and ACS AEM Commons usage.



[usage]: usage.html
[changelog]: changes.html
[aem-mock]: https://wcm.io/testing/aem-mock/
[conga]: https://devops.wcm.io/conga
[aem-core-wcm-components]: https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components
[sling-caconfig]: https://sling.apache.org/documentation/bundles/context-aware-configuration/context-aware-configuration.html
[wcmio-caconfig]: https://wcm.io/caconfig/
[wcmio-handler]: https://wcm.io/handler
[wcmio-wcm-core-components]: https://wcm.io/wcm/core-components/
[acs-aem-commons]: https://adobe-consulting-services.github.io/acs-aem-commons/
[adobe-aem-archetype]: https://github.com/Adobe-Marketing-Cloud/aem-project-archetype
[acs-aem-lazybones]: https://github.com/Adobe-Consulting-Services/lazybones-aem-templates
[aem-confmgmt-archetype]: ../aem-confmgmt/
[adaptto-talk-2018-aem-archetypes]: https://adapt.to/2018/en/schedule/maven-archetypes-for-aem.html
[adaptto-talk-2017-fsresource]: https://adapt.to/2017/en/schedule/ease-development-with-apache-sling-file-system-resource-provider.html
[webpack]: https://webpack.js.org/
