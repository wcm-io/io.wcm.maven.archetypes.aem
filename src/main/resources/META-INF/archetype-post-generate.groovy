import java.util.regex.Pattern

def rootDir = new File(request.getOutputDirectory() + "/" + request.getArtifactId())
def javaPackage = request.getProperties().get("package")
def optionAemVersion = request.getProperties().get("optionAemVersion")
def optionMultiBundleLayout = request.getProperties().get("optionMultiBundleLayout")
def optionWcmioHandler = request.getProperties().get("optionWcmioHandler")

def coreBundle = new File(rootDir, "bundles/core")
def clientlibsBundle = new File(rootDir, "bundles/clientlibs")
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

// refactor project layout when multi bundle layout is switched off
if (optionMultiBundleLayout == "n") {
  // move clientlibs-root
  assert new File(clientlibsBundle, "src/main/webapp/clientlibs-root").renameTo(new File(coreBundle, "src/main/webapp/clientlibs-root"))
  // delete clientlibs bundle
  assert clientlibsBundle.deleteDir()
  // remove bundles/clientlibs module entry from root pom
  removeModule(rootPom, "bundles/clientlibs")
  // move rewriter config to app-root/config
  assert new File(coreBundle, "src/main/webapp/app-root/config").mkdir()
  assert new File(coreBundle, "src/main/webapp/app-config/rewriter").renameTo(new File(coreBundle, "src/main/webapp/app-root/config/rewriter"))
  assert new File(coreBundle, "src/main/webapp/app-config").deleteDir()
}

// use webapp for AEM 6.3+ or AEM 6.1/2
if (optionAemVersion == "6.1" || optionAemVersion == "6.2") {
  if (optionMultiBundleLayout == "n") {
    assert new File(coreBundle, "src/main/webapp/app-root/config").renameTo(new File(coreBundle, "src/main/webapp/app-root-aem61/config"))
  }
  assert new File(coreBundle, "src/main/webapp/app-root").deleteDir()
  assert new File(coreBundle, "src/main/webapp/app-root-aem61").renameTo(new File(coreBundle, "src/main/webapp/app-root"))
}
else {
  assert new File(coreBundle, "src/main/webapp/app-root-aem61").deleteDir()
}

// remove files only relevant for wcm.io Handler projects
if (optionWcmioHandler == "n") {
  assert new File(coreBundle, "src/main/java/" + javaPackage.replace('.','/') + "/config").deleteDir()
}
