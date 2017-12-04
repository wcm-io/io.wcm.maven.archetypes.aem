#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.testcontext;

import static io.wcm.testing.mock.wcmio.caconfig.ContextPlugins.WCMIO_CACONFIG;
#if ( $optionWcmioHandler == "y" )
import static io.wcm.testing.mock.wcmio.handler.ContextPlugins.WCMIO_HANDLER;
import static io.wcm.testing.mock.wcmio.sling.ContextPlugins.WCMIO_SLING;
#end
import static org.apache.sling.testing.mock.caconfig.ContextPlugins.CACONFIG;

import java.io.IOException;

import org.apache.sling.api.resource.PersistenceException;

#if ( $optionWcmioHandler == "y" )
import io.wcm.handler.media.spi.MediaFormatProvider;
#end
import io.wcm.testing.mock.aem.junit.AemContext;
import io.wcm.testing.mock.aem.junit.AemContextBuilder;
import io.wcm.testing.mock.aem.junit.AemContextCallback;
import io.wcm.testing.mock.wcmio.caconfig.MockCAConfig;

#if ( $optionWcmioHandler == "y" )
import ${package}.config.AppTemplate;
import ${package}.config.impl.LinkHandlerConfigImpl;
import ${package}.config.impl.MediaFormatProviderImpl;

#end
/**
 * Sets up {@link AemContext} for unit tests in this application.
 */
public final class AppAemContext {

  private AppAemContext() {
    // static methods only
  }

  public static AemContext newAemContext() {
    return new AemContextBuilder()
        .plugin(CACONFIG)
#if ( $optionWcmioHandler == "y" )
        .plugin(WCMIO_SLING, WCMIO_CACONFIG, WCMIO_HANDLER)
#else
        .plugin(WCMIO_CACONFIG)
#end
        .afterSetUp(SETUP_CALLBACK)
        .build();
  }

  /**
   * Custom set up rules required in all unit tests.
   */
  private static final AemContextCallback SETUP_CALLBACK = new AemContextCallback() {
    @Override
    public void execute(AemContext context) throws PersistenceException, IOException {

      // context path strategy
#if ( $optionWcmioHandler == "y" )
      MockCAConfig.contextPathStrategyRootTemplate(context, AppTemplate.EDITORIAL_HOMEPAGE.getTemplatePath());
#else
      MockCAConfig.contextPathStrategyRootTemplate(context, "/apps/${projectName}#if($optionMultiBundleLayout=='y')/core#end/templates/content/homepage");
#end

#if ( $optionWcmioHandler == "y" )
      // setup handler
      context.registerInjectActivateService(new LinkHandlerConfigImpl());
      context.registerService(MediaFormatProvider.class, new MediaFormatProviderImpl());

#end
      // register sling models
      context.addModelsForPackage("${package}");

    }
  };

}
