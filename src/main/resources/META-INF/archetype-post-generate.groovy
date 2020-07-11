@Grab(group="org.codehaus.groovy", module="groovy-xml", version="2.4.16")
import java.util.regex.Pattern
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import groovy.io.FileType
import groovy.util.XmlSlurper

def rootDir = new File(request.getOutputDirectory() + "/" + request.getArtifactId())
def javaPackage = request.getProperties().get("package")
def optionAemVersion = request.getProperties().get("optionAemVersion")
def optionSlingInitialContentBundle = request.getProperties().get("optionSlingInitialContentBundle")
def optionEditableTemplates = request.getProperties().get("optionEditableTemplates")
def optionMultiBundleLayout = request.getProperties().get("optionMultiBundleLayout")
def optionContextAwareConfig = request.getProperties().get("optionContextAwareConfig")
def optionWcmioHandler = request.getProperties().get("optionWcmioHandler")
def optionIntegrationTests = request.getProperties().get("optionIntegrationTests")

def coreBundle = new File(rootDir, "bundles/core")
def clientlibsBundle = new File(rootDir, "bundles/clientlibs")
def confContentPackage = new File(rootDir, "content-packages/conf-content")
def sampleContentPackage = new File(rootDir, "content-packages/sample-content")
def uiAppsPackage = new File(rootDir, "content-packages/ui.apps")
def configDefinition = new File(rootDir, "config-definition")
def frontend = new File(rootDir, "frontend")
def rootPom = new File(rootDir, "pom.xml")
def parentPom = new File(rootDir, "parent/pom.xml")
def tests = new File(rootDir, "tests")

// validate parameters - throw exceptions for invalid combinations
if (optionAemServicePack == "n" && optionAemVersion == "6.4") {
  throw new RuntimeException("For AEM 6.4 optionAemServicePack='y' is required because AEM 6.4 is only supported with latest service pack.")
}
if (optionAemServicePack == "y" && optionAemVersion == "cloud") {
  throw new RuntimeException("For AEM Cloud Service optionAemServicePack='y' is not allowed - there are no service packs for the cloud.")
}
if (optionMultiBundleLayout == "y" && optionSlingInitialContentBundle == "n") {
  throw new RuntimeException("Parameter optionMultiBundleLayout='y' is only supported with optionSlingInitialContentBundle='y'.")
}
if (optionWcmioHandler == "y" && optionContextAwareConfig == "n") {
  throw new RuntimeException("Parameter optionWcmioHandler='y' is only supported with optionContextAwareConfig='y'.")
}
if (optionEditableTemplates == "n" && optionWcmioHandler == "n") {
  throw new RuntimeException("You have to specify either parameter optionEditableTemplates='y' or optionWcmioHandler='y'.")
}
if (!(javaPackage ==~ /^[a-z0-9\.]+$/)) {
  throw new RuntimeException("Java package name is invalid: " + javaPackage)
}
if (optionJavaVersion == "11" && (optionAemVersion == "6.4")) {
  throw new RuntimeException("Java 11 is only supported for AEM 6.5 and higher.")
}
if (optionIntegrationTests == "<" && optionAemVersion == "6.4") {
  throw new RuntimeException("Integration tests not supported for AEM 6.4.")
}

// helper methods
def removeModule(pomFile, module) {
  def pattern = Pattern.compile("\\s*<module>" + Pattern.quote(module) + "</module>", Pattern.MULTILINE)
  def pomContent = pomFile.getText("UTF-8")
  pomContent = pomContent.replaceAll(pattern, "")
  pomFile.newWriter("UTF-8").withWriter { w ->
    w << pomContent
  }
}

// frontend
if (optionFrontend == "y") {
  assert new File(clientlibsBundle, "src").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/clientlibs/${projectName}.3rdparty").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/clientlibs/${projectName}.all").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/clientlibs/${projectName}.app").deleteDir()
}
else {
  assert new File(rootDir, "frontend").deleteDir()
  assert new File(clientlibsBundle, ".gitignore").delete()
  assert new File(uiAppsPackage, ".gitignore").delete()
  // remove frontend module entry from root pom
  removeModule(rootPom, "frontend")
}

// remove files only relevant for wcm.io Handler projects
if (optionWcmioHandler == "n") {
  assert new File(coreBundle, "src/main/java/" + javaPackage.replace('.','/') + "/config").deleteDir()

  assert new File(coreBundle, "src/main/webapp/app-root/templates/admin/redirect").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/templates/admin/redirect.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root/components/admin/page/redirect.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root/components/content/responsiveimage.json").delete()

  assert new File(clientlibsBundle, "src/main/webapp/clientlibs-root/${projectName}.app/css").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/clientlibs/${projectName}.app/css").deleteDir()

  if (optionFrontend == "y") {
    assert new File(frontend, "src/components/customcarousel/customcarousel.scss").delete()
    assert new File(frontend, "src/components/responsiveimage").deleteDir()
  }

  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/core/templates/admin/redirect").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/core/templates/admin/redirect").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/core/components/admin/page/redirect").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/core/components/content/responsiveimage").deleteDir()

  assert new File(configDefinition, "src/main/templates/${projectName}-aem-cms/${projectName}-aem-cms-rewriter-config.json.hbs").delete()
}
else {
  assert new File(coreBundle, "src/main/webapp/app-root/components/admin/page/structureElement").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/components/content/image.json").delete()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/core/components/admin/page/structureElement/structureElement.html").delete()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/core/components/content/image").deleteDir()
}

// refactor project layout when multi bundle layout is switched off
if (optionMultiBundleLayout == "n") {
  // move .gitignore for clientlibs-root
  if (optionFrontend == "y") {
    assert new File(clientlibsBundle, ".gitignore").renameTo(new File(coreBundle, ".gitignore"))
  }
  else {
    // move clientlibs-root
    assert new File(clientlibsBundle, "src/main/webapp/clientlibs-root").renameTo(new File(coreBundle, "src/main/webapp/clientlibs-root"))
  }
  // delete clientlibs bundle
  assert clientlibsBundle.deleteDir()
  // remove bundles/clientlibs module entry from root pom
  removeModule(rootPom, "bundles/clientlibs")
}

// remove parts of sample content when caconfig is not activated
if (optionContextAwareConfig == "n" && optionWcmioHandler == "n" ) {
  assert new File(coreBundle, "src/main/webapp/app-root/templates/admin/configEditor").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/templates/admin/configEditor.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root/templates/admin/structureElement").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/templates/admin/structureElement.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root/templates/admin").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/components/admin/page/configEditor.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root/components/admin/page/structureElement.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root/components/admin/page/structureElement").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/components/admin").deleteDir()

  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/core/templates/admin/configEditor").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/core/templates/admin/structureElement").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/core/templates/admin").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/core/components/admin/page/configEditor").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/core/components/admin/page/structureElement").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/core/components/admin").deleteDir()

  assert new File(sampleContentPackage, "jcr_root/content/${projectName}/en/tools").deleteDir()
  assert new File(sampleContentPackage, "jcr_root/conf").deleteDir()
}

// remove conf-content package if not required
if (optionEditableTemplates == "n") {
  removeModule(rootPom, "content-packages/conf-content")
  confContentPackage.deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/components/global/xfpage.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root/components/global/xfpage").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/core/components/global/xfpage").deleteDir()
  assert new File(sampleContentPackage, "jcr_root/content/experience-fragments").deleteDir()
}
else {
  // set last activated date in conf-content to current date
  confContentPackage.eachFileRecurse(FileType.FILES) { file ->
    if (file.name =~ /\.content\.xml$/) {
      def fileContent = file.getText("UTF-8").replaceAll('\\Q2020-01-01T00:00:00.000+02:00\\E', new Date().format("yyyy-MM-dd'T00:00:00.000'XXX"))
      file.newWriter("UTF-8").withWriter { w ->
        w << fileContent
      }
    }
  }  
}

// prepare project for editable templates
if (optionEditableTemplates == "y") {
  assert new File(coreBundle, "src/main/webapp/app-root/components/page").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/templates/contentpage.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root/templates/contentpage").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/templates/homepage.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root/templates/homepage").deleteDir()

  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/core/components/page").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/core/templates/contentpage").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/core/templates/homepage").deleteDir()
}
else {
  assert new File(confContentPackage, "jcr_root/conf/${projectName}/settings").deleteDir()
}
if (optionEditableTemplates == "y" && optionContextAwareConfig == "n" && optionWcmioHandler == "n") {
  assert new File(coreBundle, "src/main/webapp/app-root/templates").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/core/templates").deleteDir()
}

// sling-initial-content bundle or filevault xml package
if (optionSlingInitialContentBundle == "y") {
  removeModule(rootPom, "content-packages/ui.apps")
  uiAppsPackage.deleteDir()
}
else {
  assert new File(coreBundle, "src/main/webapp").deleteDir()
  if (optionFrontend == "y") {
    assert new File(coreBundle, ".gitignore").delete()
  }
}

if (optionAemVersion == "cloud") {
  // insert latest version of io.wcm.maven.aem-cloud-dependencies available on maven central
  def metadata = new XmlSlurper().parse("https://repo1.maven.org/maven2/io/wcm/maven/io.wcm.maven.aem-cloud-dependencies/maven-metadata.xml")
  def wcmioAemCloudDependenciesLatestVersion = metadata.versioning.latest.toString()
  assert wcmioAemCloudDependenciesLatestVersion != null && wcmioAemCloudDependenciesLatestVersion != ""
  def pomContent = parentPom.getText("UTF-8")
  pomContent = pomContent.replaceAll("WCMIO_AEM_CLOUD_DEPENDENCIES_LATEST_VERSION", wcmioAemCloudDependenciesLatestVersion)
  parentPom.newWriter("UTF-8").withWriter { w ->
    w << pomContent
  }
}
else {
  // remove environments only relevant for AEM Cloud service
  assert new File(configDefinition, "src/main/dev-environments/cloud.yaml").delete()
}

if (optionIntegrationTests == "n") {
  removeModule(rootPom, "tests/integration")
  tests.deleteDir()
}


// convert all line endings to unix-style
rootDir.eachFileRecurse(FileType.FILES) { file ->
  if (file.name =~ /\.(cfg|conf|config|css|dtd|esp|ecma|groovy|hbrs|hbs|htm|html|java|jpage|js|json|jsp|md|mustache|tld|launch|log|php|pl|project|properties|props|py|sass|scss|sh|shtm|shtml|sql|svg|tf|txt|vm|xml|xsd|xsl|xslt|yml|yaml)$/) {
    def fileContent = file.getText("UTF-8").replaceAll('\r\n', '\n')
    file.newWriter("UTF-8").withWriter { w ->
      w << fileContent
    }
  }
}

// remove all empty folders
Closure<Boolean> removeEmptyFolders = {
  def emptyFolders = []
  rootDir.eachFileRecurse(FileType.DIRECTORIES) { file ->
    if (file.isDirectory() && file.list().length == 0) {
      emptyFolders.add(file)
    }
  }
  emptyFolders.each { file ->
    assert file.deleteDir()
  }
  return !emptyFolders.empty
}
while (removeEmptyFolders()) continue

// rename root folder to project name
def newRootDir = new File(request.getOutputDirectory() + "/" + projectName)
Files.move(rootDir.toPath(), newRootDir.toPath(), StandardCopyOption.REPLACE_EXISTING)
assert newRootDir.exists()
