#if ( $optionSlingInitialContentBundle == "y" )
#set( $serializationFormat = "json" )
#if ($optionMultiBundleLayout=="y")
#set( $clientlibDestPath = "bundles/clientlibs/src/main/webapp/clientlibs-root" )
#else
#set( $clientlibDestPath = "bundles/core/src/main/webapp/clientlibs-root" )
#end
#else
#set( $serializationFormat = "xml" )
#set( $clientlibDestPath = "content-packages/ui.apps/jcr_root/etc/clientlibs/${projectName}" )
#end
module.exports = {
  // default working directory (can be changed per 'cwd' in every asset option)
  context: __dirname,

  // path to the clientlib root folder (output)
  clientLibRoot: "../${clientlibDestPath}",

  // define all clientlib options here as array... (multiple clientlibs)
  libs: [
    {
      name: "${projectName}.app",
      serializationFormat: "${serializationFormat}",
      assets: {
        js: ["dist/static/js/app.js", "dist/static/js/app.js.map"],
        css: ["dist/static/styles/app.css", "dist/static/styles/app.css.map"],
        resources: {
          cwd: "./public/",
          flatten: false,
          files: ["**/*.*"]
        }
      }
    },
    {
      name: "${projectName}.all",
      serializationFormat: "${serializationFormat}",
      embed: ["core.wcm.components.image.v1", "${projectName}.app"],
      jsProcessor: ["default:none", "min:gcc;compilationLevel=whitespace"],
      longCacheKey: "${project.version}-${buildNumber}",
      assets: {
        js: [],
        css: []
      }
    }
  ]
};
