@Grab(group="org.codehaus.groovy", module="groovy-xml", version="3.0.22")
@Grab(group="net.lingala.zip4j", module="zip4j", version="2.11.5")
import java.util.regex.Pattern
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import groovy.io.FileType
import groovy.util.XmlSlurper
import net.lingala.zip4j.ZipFile

def rootDir = new File(request.getOutputDirectory() + "/" + request.getArtifactId())
def javaPackage = request.getProperties().get("package")
def optionAemVersion = request.getProperties().get("optionAemVersion")
def optionAemServicePack = request.getProperties().get("optionAemServicePack")
def optionAemServicePackAPI = request.getProperties().get("optionAemServicePackAPI")
def optionSlingInitialContentBundle = request.getProperties().get("optionSlingInitialContentBundle")
def optionEditableTemplates = request.getProperties().get("optionEditableTemplates")
def optionMultiBundleLayout = request.getProperties().get("optionMultiBundleLayout")
def optionContextAwareConfig = request.getProperties().get("optionContextAwareConfig")
def optionWcmioHandler = request.getProperties().get("optionWcmioHandler")
def optionWcmioConga = request.getProperties().get("optionWcmioConga")
def optionIntegrationTests = request.getProperties().get("optionIntegrationTests")

def coreBundle = new File(rootDir, "bundles/core")
def clientlibsBundle = new File(rootDir, "bundles/clientlibs")
def completeContentPackage = new File(rootDir, "content-packages/complete")
def osgiConfigContentPackage = new File(rootDir, "content-packages/osgi-config")
def rewriterConfigContentPackage = new File(rootDir, "content-packages/rewriter-config")
def confContentPackage = new File(rootDir, "content-packages/conf-content")
def sampleContentPackage = new File(rootDir, "content-packages/sample-content")
def uiAppsPackage = new File(rootDir, "content-packages/ui.apps")
def configDefinition = new File(rootDir, "config-definition")
def allContentPackage = new File(rootDir, "all")
def dispatcher = new File(rootDir, "dispatcher")
def frontend = new File(rootDir, "frontend")
def rootPom = new File(rootDir, "pom.xml")
def parentPom = new File(rootDir, "parent/pom.xml")
def tests = new File(rootDir, "tests")

// validate parameters - throw exceptions for invalid combinations
if ((optionAemServicePack=="y" || optionAemServicePackAPI=="y") && optionAemVersion == "cloud") {
  throw new RuntimeException("For AEMaaCS optionAemServicePack='y' or optionAemServicePackAPI='y' is not allowed - there are no service packs for the cloud.")
}
if (optionAemServicePackAPI != "y" && optionAemVersion != "cloud") {
  throw new RuntimeException("For AEM 6.5 optionAemServicePackAPI='y' is mandatory. Also set optionAemServicePack='y' or deploy the service pack manually.")
}
if (optionMultiBundleLayout == "y" && optionSlingInitialContentBundle == "n") {
  throw new RuntimeException("Parameter optionMultiBundleLayout='y' is only supported with optionSlingInitialContentBundle='y'.")
}
if (optionWcmioHandler == "y" && optionContextAwareConfig == "n") {
  throw new RuntimeException("Parameter optionWcmioHandler='y' is only supported with optionContextAwareConfig='y'.")
}
if (optionWcmioConga == "n" && optionAemVersion != "cloud") {
  throw new RuntimeException("Parameter optionWcmioConga='n' is only supported with optionAemVersion='cloud'.")
}
if (optionEditableTemplates == "n" && optionWcmioHandler == "n") {
  throw new RuntimeException("You have to specify either parameter optionEditableTemplates='y' or optionWcmioHandler='y'.")
}
if (!(javaPackage ==~ /^[a-z0-9\.]+$/)) {
  throw new RuntimeException("Java package name is invalid: " + javaPackage)
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

// parent: set build timestamp to current date
def parentPomContent = parentPom.getText("UTF-8")
parentPomContent = parentPomContent.replaceAll('\\Q2020-01-01T00:00:00Z\\E', new Date().format("yyyy-MM-dd'T00:00:00Z'"))
parentPom.newWriter("UTF-8").withWriter { w ->
  w << parentPomContent
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
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/core/components/admin/page/structureElement/structureElement.html").delete()
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
  [confContentPackage,sampleContentPackage].each { packageFolder ->
    packageFolder.eachFileRecurse(FileType.FILES) { file ->
      if (file.name =~ /(\.content|pom)\.xml$/) {
        def fileContent = file.getText("UTF-8").replaceAll('\\Q2020-01-01T00:00:00.000+02:00\\E', new Date().format("yyyy-MM-dd'T00:00:00.000'XXX"))
        file.newWriter("UTF-8").withWriter { w ->
          w << fileContent
        }
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

  // delete complete package - deployment is done via "all" package which includes the application bundles
  assert completeContentPackage.deleteDir()
  removeModule(rootPom, "content-packages/complete")
}
else {
  // remove environments only relevant for AEMaaCS
  assert new File(configDefinition, "src/main/environments/cloud.yaml").delete()
}

if (optionIntegrationTests == "n") {
  removeModule(rootPom, "tests/integration")
  tests.deleteDir()
}

if (optionWcmioConga == "y") {
  removeModule(rootPom, "content-packages/osgi-config")
  osgiConfigContentPackage.deleteDir()
  removeModule(rootPom, "content-packages/rewriter-config")
  rewriterConfigContentPackage.deleteDir()
  removeModule(rootPom, "all")
  allContentPackage.deleteDir()
  removeModule(rootPom, "dispatcher")
  dispatcher.deleteDir()
}
else {
  /*
   * For AEM projects without CONGA, we do not maintain redundant definitions for OSGi and dispatcher configurations
   * in this archetype. Instead, we run CONGA once during the generation of the project, copy over the generated
   * configuration files to the respective maven modules and delete the CONGA module afterwards. With this, CONGA
   * is only used once during project generation, but the generated projects is no longer using CONGA.
   */
  println ""
  println "Running CONGA once to generate OSGi and dispatcher configurations..."
  println ""

  // execute CONGA via maven
  def mavenCall = "mvn -f $rootDir/config-definition -Dconga.environments=cloud generate-resources"
  def isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows")
  def execCommand = isWindows ? ["cmd.exe", "/c", mavenCall] : ["/bin/sh", "-c", mavenCall]
  def proc = execCommand.execute()
  def pool = Executors.newFixedThreadPool(2)
  def stdoutFuture = pool.submit({ -> proc.inputStream.text} as Callable<String>)
  def stderrFuture = pool.submit({ -> proc.errorStream.text} as Callable<String>)
  proc.waitFor()
  def exitValue = proc.exitValue()
  if (exitValue != 0) {
      System.err.println(stderrFuture.get())
      throw new RuntimeException("$execCommand returned $exitValue")
  }
  println(stdoutFuture.get())

  // unzip osgi-config
  def congaAemCmsConfigZip = new File(configDefinition, "target/configuration/cloud/aem-author/packages/${projectName}-aem-cms-config.zip")
  new ZipFile(congaAemCmsConfigZip).extractAll(osgiConfigContentPackage.toPath().toString())
  new File(osgiConfigContentPackage, "META-INF/vault/definition").deleteDir()
  assert new File(osgiConfigContentPackage, "META-INF/vault/config.xml").delete()
  assert new File(osgiConfigContentPackage, "META-INF/vault/properties.xml").delete()
  assert new File(osgiConfigContentPackage, "META-INF/vault/settings.xml").delete()

  if (optionWcmioHandler == "y") {
    // unzip rewriter config
    def congaAemCmsRewriterConfigZip = new File(configDefinition, "target/configuration/cloud/aem-author/packages/${projectName}-aem-cms-rewriter-config.zip")
    new ZipFile(congaAemCmsRewriterConfigZip).extractAll(rewriterConfigContentPackage.toPath().toString())
    new File(rewriterConfigContentPackage, "META-INF/vault/definition").deleteDir()
    assert new File(rewriterConfigContentPackage, "META-INF/vault/config.xml").delete()
    assert new File(rewriterConfigContentPackage, "META-INF/vault/properties.xml").delete()
    assert new File(rewriterConfigContentPackage, "META-INF/vault/settings.xml").delete()
  }
  else {
    removeModule(rootPom, "content-packages/rewriter-config")
    rewriterConfigContentPackage.deleteDir()
  }

  // unzip dispatcher config
  def congaDispatcherZip = new File(configDefinition, "target/cloud.aem-dispatcher.dispatcher-config.zip")
  new ZipFile(congaDispatcherZip).extractAll(new File(dispatcher, "src").toPath().toString())

  removeModule(rootPom, "config-definition")
  configDefinition.deleteDir()

  println "Configuration from CONGA generated, CONGA is removed from the project."
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
try {
  def newRootDir = new File(request.getOutputDirectory() + "/" + projectName)
  Files.move(rootDir.toPath(), newRootDir.toPath(), StandardCopyOption.REPLACE_EXISTING)
  assert newRootDir.exists()

  println ""
  println "Your new AEM project is ready at ${newRootDir.toPath()}"
  println ""
}
catch (Exception ex) {
  throw new RuntimeException("Failed to rename root folder to project name: " + ex.getMessage())
}
