import java.util.regex.Pattern

def rootDir = new File(request.getOutputDirectory() + "/" + request.getArtifactId())
def javaPackage = request.getProperties().get("package")
def optionAemVersion = request.getProperties().get("optionAemVersion")
def optionMultiBundleLayout = request.getProperties().get("optionMultiBundleLayout")
def optionContextAwareConfig = request.getProperties().get("optionContextAwareConfig")
def optionWcmioHandler = request.getProperties().get("optionWcmioHandler")

def coreBundle = new File(rootDir, "bundles/core")
def clientlibsBundle = new File(rootDir, "bundles/clientlibs")
def sampleContentPackage = new File(rootDir, "content-packages/sample-content")
def rootPom = new File(rootDir, "pom.xml")

// helper methods
def removeModule(pomFile, module) {
  def pattern = Pattern.compile("\\s*<module>" + Pattern.quote(module) + "</module>", Pattern.MULTILINE)
  def pomContent = pomFile.getText("UTF-8")
  pomContent = pomContent.replaceAll(pattern, "")
  pomFile.newWriter().withWriter { w ->
    w << pomContent
  }
}

// remove files only relevant for wcm.io Handler projects
if (optionWcmioHandler == "n") {
  assert new File(coreBundle, "src/main/java/" + javaPackage.replace('.','/') + "/config").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-config").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/templates/admin/redirect").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/components/admin/page/redirect.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root/components/content/common/contentRichText").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/components/content/stage/stageheader").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root/global/include").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/templates/admin/redirect").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/admin/page/redirect.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/content/common").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/content/page/content").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/content/page/homepage").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/content/stage").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/global/include").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/global/page/html.html").delete()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/global/wcmInit.json").delete()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61/components/structure").deleteDir()
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

// remove parts of sample content when caconfig is not activated
if (optionContextAwareConfig == "n") {
  assert new File(sampleContentPackage, "jcr_root/content/${projectName}/en/tools").deleteDir()
}
