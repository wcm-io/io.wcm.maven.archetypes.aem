#if ( $optionSlingInitialContentBundle == "y" )
#set( $serializationFormat = "json" )
#if ($optionMultiBundleLayout=="y")
#set( $clientlibDestPath = "bundles/clientlibs/src/main/webapp/clientlibs-root" )
#else
#set( $clientlibDestPath = "bundles/core/src/main/webapp/clientlibs-root" )
#end
#else
#set( $serializationFormat = "xml" )
#set( $clientlibDestPath = "content-packages/ui.apps/jcr_root/apps/${projectName}/clientlibs" )
#end
export default {
  // default working directory (can be changed per 'cwd' in every asset option)
  context: import.meta.dirname,

  // path to the clientlib root folder (output)
  clientLibRoot: "../${clientlibDestPath}",

  // define all clientlib options here as array... (multiple clientlibs)
  libs: [
    {
      name: "${projectName}.app",
      serializationFormat: "${serializationFormat}",
      allowProxy: true,
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
      embed: [
        "core.wcm.components.commons.datalayer.v1",
        "core.wcm.components.commons.site.container",
#if ( $optionWcmioHandler != "y" )
        "core.wcm.components.image.v2",
#end
        "core.wcm.components.carousel.v1",
        "core.wcm.components.tabs.v1",
        "core.wcm.components.accordion.v1",
        "${projectName}.app"
      ],
      jsProcessor: ["default:none", "min:gcc;compilationLevel=whitespace;languageIn=ECMASCRIPT_2018;languageOut=ECMASCRIPT_2018"],
      cssProcessor: ["default:none", "min:none"],
#if ( $optionAemVersion != "cloud" )
      longCacheKey: "${project.version}-${buildNumber}",
#end
      allowProxy: true,
      assets: {
        js: [],
        css: []
      }
    }
  ]
};
