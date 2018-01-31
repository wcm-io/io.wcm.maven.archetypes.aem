import java.util.regex.Pattern
import groovy.io.FileType

def rootDir = new File(request.getOutputDirectory() + "/" + request.getArtifactId())
def javaPackage = request.getProperties().get("package")
def optionAemVersion = request.getProperties().get("optionAemVersion")
def optionSlingInitialContentBundle = request.getProperties().get("optionSlingInitialContentBundle")
def optionEditableTemplates = request.getProperties().get("optionEditableTemplates")
def optionMultiBundleLayout = request.getProperties().get("optionMultiBundleLayout")
def optionContextAwareConfig = request.getProperties().get("optionContextAwareConfig")
def optionWcmioHandler = request.getProperties().get("optionWcmioHandler")

def coreBundle = new File(rootDir, "bundles/core")
def clientlibsBundle = new File(rootDir, "bundles/clientlibs")
def confContentPackage = new File(rootDir, "content-packages/conf-content")
def sampleContentPackage = new File(rootDir, "content-packages/sample-content")
def uiAppsPackage = new File(rootDir, "content-packages/ui.apps")
def configDefinition = new File(rootDir, "config-definition")
def rootPom = new File(rootDir, "pom.xml")

// validate parameters - throw exceptions for invalid combinations
if (optionEditableTemplates == "y" && (optionAemVersion == "6.1" || optionAemVersion == "6.2")) {
  throw new RuntimeException("Parameter optionEditableTemplates='y' is only supported for optionAemVersion='6.3' and up.")
}
if (optionSlingInitialContentBundle == "n" && (optionAemVersion == "6.1" || optionAemVersion == "6.2")) {
  throw new RuntimeException("Parameter optionSlingInitialContentBundle='n' is only supported for optionAemVersion='6.3' and up.")
}
if (optionMultiBundleLayout == "y" && optionSlingInitialContentBundle == "n") {
  throw new RuntimeException("Parameter optionMultiBundleLayout='y' is only supported with optionSlingInitialContentBundle='y'.")
}
if (optionWcmioHandler == "y" && optionContextAwareConfig == "n") {
  throw new RuntimeException("Parameter optionWcmioHandler='y' is only supported with optionContextAwareConfig='y'.")
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

// remove files only relevant for wcm.io Handler projects
if (optionWcmioHandler == "n") {
  assert new File(coreBundle, "src/main/java/" + javaPackage.replace('.','/') + "/config").deleteDir()
  
  assert new File(coreBundle, "src/main/webapp/app-config").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/templates/admin/redirect").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/templates/admin/redirect.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root/components/admin/page/redirect.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root/components/content/common/contentRichText").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/components/content/stage/stageheader").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/components/global/include").deleteDir()

  assert new File(coreBundle, "src/main/webapp/app-root-aem61/templates/admin/redirect").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/templates/admin/redirect.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/admin/page/redirect.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/content/common").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/content/page/content").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/content/page/homepage").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/content/stage").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/global/include").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/global/page/html.html").delete()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/global/wcmInit.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/structure").deleteDir()

  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/config").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/templates/admin/redirect").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/components/admin/page/redirect").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/components/content/common/contentRichText/contentRichText.html").delete()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/components/content/common/contentRichText/_cq_editConfig.xml").delete()   
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/components/content/stage/stageheader/stageheader.html").delete()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/components/content/stage/stageheader/_cq_editConfig.xml").delete()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/components/content/stage/stageheader/_cq_dialog").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/components/global/include").deleteDir()

  assert new File(configDefinition, "src/main/templates/${projectName}-aem-cms/${projectName}-aem-cms-author-systemusers.json.hbs").delete()
}
else {
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/global/page/body.html").delete()
}

// refactor project layout when multi bundle layout is switched off
if (optionMultiBundleLayout == "n") {
  // move clientlibs-root
  assert new File(clientlibsBundle, "src/main/webapp/clientlibs-root").renameTo(new File(coreBundle, "src/main/webapp/clientlibs-root"))
  // delete clientlibs bundle
  assert clientlibsBundle.deleteDir()
  // remove bundles/clientlibs module entry from root pom
  removeModule(rootPom, "bundles/clientlibs")
  // move rewriter config to app-root/config
  if (optionWcmioHandler == "y") {
    assert new File(coreBundle, "src/main/webapp/app-root/config").mkdir()
    assert new File(coreBundle, "src/main/webapp/app-config/rewriter").renameTo(new File(coreBundle, "src/main/webapp/app-root/config/rewriter"))
    assert new File(coreBundle, "src/main/webapp/app-config").deleteDir()
  }
}

// remove parts of sample content when caconfig is not activated
if (optionContextAwareConfig == "n" && optionWcmioHandler == "n" ) {
  assert new File(coreBundle, "src/main/webapp/app-root/templates/admin/configEditor").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/templates/admin/configEditor.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root/templates/admin/structureElement").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/templates/admin/structureElement.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root/components/admin/page/configEditor.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root/components/admin/page/structureElement.json").delete()

  assert new File(coreBundle, "src/main/webapp/app-root-aem61/templates/admin/configEditor").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/templates/admin/configEditor.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/templates/admin/structureElement").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/templates/admin/structureElement.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/admin/page/configEditor.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/admin/page/structureElement.json").delete()

  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/templates/admin/configEditor").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/templates/admin/structureElement").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/components/admin/page/configEditor").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/components/admin/page/structureElement").deleteDir()

  assert new File(sampleContentPackage, "jcr_root/content/${projectName}/en/tools").deleteDir()
}

// use webapp for AEM 6.3+ or AEM 6.1/2
if (optionAemVersion == "6.1" || optionAemVersion == "6.2") {
  if (optionMultiBundleLayout == "n" && optionWcmioHandler == "y") {
    assert new File(coreBundle, "src/main/webapp/app-root/config").renameTo(new File(coreBundle, "src/main/webapp/app-root-aem61/config"))
  }
  assert new File(coreBundle, "src/main/webapp/app-root").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61").renameTo(new File(coreBundle, "src/main/webapp/app-root"))
}
else {
  assert new File(coreBundle, "src/main/webapp/app-root-aem61").deleteDir()
}

// remove conf-content package if not required
if (optionContextAwareConfig == "n" && optionEditableTemplates == "n") {
  removeModule(rootPom, "content-packages/conf-content")
  confContentPackage.deleteDir()
}

// prepare project for editable templates
if (optionEditableTemplates == "y") {
  assert new File(coreBundle, "src/main/webapp/app-root/components/content/page").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/components/global/page/body.html").delete()
  assert new File(coreBundle, "src/main/webapp/app-root/templates").deleteDir()

  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/components/content/page").deleteDir()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/components/global/page/body.html").delete()
  assert new File(uiAppsPackage, "jcr_root/apps/${projectName}/templates").deleteDir()
}
else {
  assert new File(confContentPackage, "jcr_root/conf/${projectName}/settings").deleteDir()
}

// sling-initial-content bundle or filevault xml package
if (optionSlingInitialContentBundle == "y") {
  removeModule(rootPom, "content-packages/ui.apps")
  uiAppsPackage.deleteDir()
}
else {
  assert new File(coreBundle, "src/main/webapp").deleteDir()
}

// convert all line endings to unix-style
rootDir.eachFileRecurse(FileType.FILES) { file ->
  if (file.name =~ /\.(java|html|txt|json|xml|css|js|yaml|hbs|sh)$/) {
    def fileContent = file.getText("UTF-8").replaceAll('\r\n', '\n')
    file.newWriter("UTF-8").withWriter { w ->
      w << fileContent
    }
  }
}
