## Maven Archetype for AEM usage

### Basic Usage

Generate a new AEM project using interactive mode by executing:

```
mvn archetype:generate \
 -DarchetypeGroupId=io.wcm.maven.archetypes \
 -DarchetypeArtifactId=io.wcm.maven.archetypes.aem
```

As minimal input parameters you have to enter a value for `groupId` and `projectName` - all other parameters can be derived from these or have sensible default parameters. But in most cases you want to specify more parameters, see next chapters for all available parameters and some examples.


### Archetype parameters

TBD


### Archetype examples

TBD


### Requirements

* Apache Maven 3.3.9 or higher
* AEM 6.1, 6.2 or 6.3 running on your local machine
* Include the [Adobe Public Maven Repository][adobe-public-maven-repo] in your maven settings, see [wcm.io Maven Repositories][wcmio-maven] for details.
* Optional: If you want to install latest service packs you have to upload them manually to your Maven Artifact Manager following [these conventions][aem-binaries-conventions] for naming them.




[adobe-public-maven-repo]: https://repo.adobe.com/nexus/content/groups/public/
[wcmio-maven]: http://wcm.io/maven.html
[aem-binaries-conventions]: [wcmio-maven]