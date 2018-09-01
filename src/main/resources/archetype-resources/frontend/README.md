#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
${projectName} Frontend
=======================

$symbol_pound$symbol_pound Installation

```
npm install
```

$symbol_pound$symbol_pound Run local Development Server

```
npm run server
```

$symbol_pound$symbol_pound Run Build Tasks

Without compression:

```
npm run build:dev
```

With compression:

```
npm run build:prod
```

Build client libraries in AEM project:

```
npm run build:clientlibs
```

#if ( $optionSlingInitialContentBundle == "y" )
#if ($optionMultiBundleLayout=="y")
#set( $clientlibDestPath = "bundles/clientlibs/src/main/webapp/clientlibs-root" )
#else
#set( $clientlibDestPath = "bundles/core/src/main/webapp/clientlibs-root" )
#end
#else
#set( $clientlibDestPath = "content-packages/ui.apps/jcr_root/etc/clientlibs/${projectName}" )
#end
This task generates the client libraries in folder `${clientlibDestPath}`.
