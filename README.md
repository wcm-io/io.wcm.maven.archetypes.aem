<img src="http://wcm.io/images/favicon-16@2x.png"/> wcm.io Maven Archetype
======
[![Build Status](https://travis-ci.org/wcm-io/wcm-io-archetype.png?branch=develop)](https://travis-ci.org/wcm-io/wcm-io-archetype)

Maven Archetype for creating new AEM projects.

Documentation: http://wcm.io/tooling/maven/archetypes/aem/<br/>
Issues: https://wcm-io.atlassian.net/browse/WTOOL<br/>
Wiki: https://wcm-io.atlassian.net/wiki/<br/>
Continuous Integration: https://travis-ci.org/wcm-io/wcm-io-archetype/


## Build from sources

If you want to build wcm.io from sources make sure you have configured all [Maven Repositories](http://wcm.io/maven.html) in your settings.xml.

See [Travis Maven settings.xml](https://github.com/wcm-io/wcm-io-archetype/blob/master/.travis.maven-settings.xml) for an example with a full configuration.

Then you can build using

```
mvn clean install
```
