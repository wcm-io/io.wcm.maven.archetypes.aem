#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.testcontext;

import static io.wcm.testing.mock.wcmio.caconfig.ContextPlugins.WCMIO_CACONFIG;
import static io.wcm.testing.mock.wcmio.handler.ContextPlugins.WCMIO_HANDLER;
import static io.wcm.testing.mock.wcmio.sling.ContextPlugins.WCMIO_SLING;
import static org.apache.sling.testing.mock.caconfig.ContextPlugins.CACONFIG;

import java.io.IOException;

import org.apache.sling.api.resource.PersistenceException;

import io.wcm.handler.media.spi.MediaFormatProvider;
import io.wcm.testing.mock.aem.junit.AemContext;
import io.wcm.testing.mock.aem.junit.AemContextBuilder;
import io.wcm.testing.mock.aem.junit.AemContextCallback;
import io.wcm.testing.mock.wcmio.caconfig.MockCAConfig;

import ${package}.config.AppTemplate;
import ${package}.config.impl.LinkHandlerConfigImpl;
import ${package}.config.impl.MediaFormatProviderImpl;

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
        .plugin(WCMIO_SLING, WCMIO_CACONFIG, WCMIO_HANDLER)
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
      MockCAConfig.contextPathStrategyRootTemplate(context, AppTemplate.EDITORIAL_HOMEPAGE.getTemplatePath());

      // setup handler
      context.registerInjectActivateService(new LinkHandlerConfigImpl());
      context.registerService(MediaFormatProvider.class, new MediaFormatProviderImpl());

      // register sling models
      context.addModelsForPackage("${package}");

    }
  };

}
