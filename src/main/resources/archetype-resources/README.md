#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
${projectName}
==============

This is an AEM project set up with the [wcm.io Maven Archetype for AEM][wcmio-maven-archetype-aem].


$symbol_pound$symbol_pound$symbol_pound Build and deploy

To build the application run

```
mvn clean install
```

To build and deploy the application to your local AEM instance use these scripts:

* `build-deploy.sh` - Build and deploy to author instances
* `build-deploy-publish.sh` - Build and deploy to publish instances

The first deployment may take a while until all updated OSGi bundles are installed.

After deployment you can open the sample content page in your browser: 

* Author: http://localhost:${aemAuthorPort}/editor.html/content/${projectName}/en.html
* Publish: http://localhost:${aemPublishPort}/content/${projectName}/en.html


$symbol_pound$symbol_pound$symbol_pound System requirements

* JDK 1.8
* Apache Maven 3.3.9 or higher
* AEM ${aemVersion} author instance running on port ${aemAuthorPort}
* Optional: AEM ${aemVersion} publish instance running on port ${aemPublishPort}
* Include the [Adobe Public Maven Repository][adobe-public-maven-repo] in your maven settings, see [wcm.io Maven Repositories][wcmio-maven] for details.
* To obtain the latest service packs via Maven you have to upload them manually to your Maven Artifact Manager following [these conventions][aem-binaries-conventions] for naming them.

It is recommended to set up the local AEM instances with `nosamplecontent` run mode.


[wcmio-maven-archetype-aem]: http://wcm.io/tooling/maven/archetypes/aem/
[adobe-public-maven-repo]: https://repo.adobe.com/nexus/content/groups/public/
[wcmio-maven]: http://wcm.io/maven.html
