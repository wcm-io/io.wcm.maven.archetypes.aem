#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
${projectName}
${projectName.replaceAll('.','=')}

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
* AEM ${optionAemVersion} author instance running on port ${aemAuthorPort}
* Optional: AEM ${optionAemVersion} publish instance running on port ${aemPublishPort}
* Include the [Adobe Public Maven Repository][adobe-public-maven-repo] in your maven settings, see [wcm.io Maven Repositories][wcmio-maven] for details.
* To obtain the latest service packs via Maven you have to upload them manually to your Maven Artifact Manager following [these conventions][aem-binaries-conventions] for naming them.

It is recommended to set up the local AEM instances with `nosamplecontent` run mode.


$symbol_pound$symbol_pound$symbol_pound Project overview

Modules of this project:

* [parent](parent/): Parent POM with dependency management for the whole project. All 3rdparty artifact versions used in the project are defined here.
#if ( $optionFrontend == "y" )
* [frontend](frontend/): Frontend build project with webpack
#end
#if ( $optionSlingInitialContentBundle == "y" )
* [bundles/core](bundles/core/): OSGi bundle containing:
  * Java classes (e.g. Sling Models, Servlets, business logic)
#if ( $optionEditableTemplates == "y" )
  * AEM components with their scripts and dialog definitions (included as `Sling-Initial-Content`)
#else
  * AEM templates and components with their scripts and dialog definitions (included as `Sling-Initial-Content`)
#end
  * i18n
#if ( $optionMultiBundleLayout == "n" )
  * HTML client libraries with JavaScript and CSS
#else
* [bundles/clientlibs](bundles/clientlibs/): OSGi bundle containing the HTML client libraries with JavaScript and CSS
#end
#else
* [bundles/core](bundles/core/): OSGi bundle with Java classes (e.g. Sling Models, Servlets, business logic).
* [content-packages/ui.apps](content-packages/ui.apps/): AEM content package containing:
#if ( $optionEditableTemplates == "y" )
  * AEM components with their scripts and dialog definitions
#else
  * AEM templates and components with their scripts and dialog definitions
#end
  * HTML client libraries with JavaScript and CSS
  * i18n
#end
* [content-packages/complete](content-packages/complete/): AEM content package containing all OSGi bundles of the application and their dependencies
#if ( $optionEditableTemplates == "y" )
* [content-packages/conf-content](content-packages/conf-content/): AEM content package with editable templates stored at `/conf`
#elseif ( $optionContextAwareConfig == "y" )
* [content-packages/conf-content](content-packages/conf-content/): AEM content package with root folders for `/conf`
#end
* [content-packages/sample-content](content-packages/sample-content/): AEM content package containing sample content (for development and test purposes)
* [config-definition](config-definition/): Defines the CONGA roles and templates for this application. Also contains a `development` CONGA environment for deploying to local development instances.


[wcmio-maven-archetype-aem]: http://wcm.io/tooling/maven/archetypes/aem/
[adobe-public-maven-repo]: https://repo.adobe.com/nexus/content/groups/public/
[wcmio-maven]: http://wcm.io/maven.html
