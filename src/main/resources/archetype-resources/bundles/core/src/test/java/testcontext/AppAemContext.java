#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.testcontext;

import static com.adobe.cq.wcm.core.components.testing.mock.ContextPlugins.CORE_COMPONENTS;
#if ( $optionContextAwareConfig == "y" )
import static io.wcm.testing.mock.wcmio.caconfig.ContextPlugins.WCMIO_CACONFIG;
#end
#if ( $optionWcmioHandler == "y" )
import static io.wcm.testing.mock.wcmio.handler.ContextPlugins.WCMIO_HANDLER;
import static io.wcm.testing.mock.wcmio.sling.ContextPlugins.WCMIO_SLING;
import static io.wcm.testing.mock.wcmio.wcm.ContextPlugins.WCMIO_WCM;
#end
#if ( $optionContextAwareConfig == "y" )
import static org.apache.sling.testing.mock.caconfig.ContextPlugins.CACONFIG;
#end

import java.io.IOException;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.jetbrains.annotations.NotNull;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextCallback;
#if ( $optionContextAwareConfig == "y" )
import io.wcm.testing.mock.wcmio.caconfig.MockCAConfig;
#end

#if ( $optionWcmioHandler == "y" )
import ${package}.config.AppTemplate;
import ${package}.config.impl.LinkHandlerConfigImpl;
import ${package}.config.impl.MediaFormatProviderImpl;
import ${package}.config.impl.MediaHandlerConfigImpl;

#end
/**
 * Sets up {@link AemContext} for unit tests in this application.
 */
public final class AppAemContext {

  private AppAemContext() {
    // static methods only
  }

  /**
   * @return {@link AemContext}
   */
  public static AemContext newAemContext() {
    return newAemContextBuilder().build();
  }

  /**
   * @return {@link AemContextBuilder}
   */
  public static AemContextBuilder newAemContextBuilder() {
    return newAemContextBuilder(ResourceResolverType.RESOURCERESOLVER_MOCK);
  }

  /**
   * @return {@link AemContextBuilder}
   */
  public static AemContextBuilder newAemContextBuilder(@NotNull ResourceResolverType resourceResolverType) {
    return new AemContextBuilder(resourceResolverType)
        .plugin(CORE_COMPONENTS#if($optionContextAwareConfig=="y"), CACONFIG, WCMIO_CACONFIG#{end}#if($optionWcmioHandler=="y"), WCMIO_SLING, WCMIO_WCM, WCMIO_HANDLER#{end})
        .afterSetUp(SETUP_CALLBACK);
  }

  /**
   * Custom set up rules required in all unit tests.
   */
  private static final AemContextCallback SETUP_CALLBACK = new AemContextCallback() {
    @Override
    public void execute(@NotNull AemContext context) throws PersistenceException, IOException {

#if ( $optionContextAwareConfig == "y" || $optionWcmioHandler == "y" )
      // context path strategy
#if ( $optionWcmioHandler == "y" )
      MockCAConfig.contextPathStrategyRootTemplate(context, AppTemplate.HOMEPAGE.getTemplatePath());
#else
      MockCAConfig.contextPathStrategyRootTemplate(context, "/apps/${projectName}/core/templates/homepage");
#end

#end
#if ( $optionWcmioHandler == "y" )
      // setup handler
      context.registerInjectActivateService(LinkHandlerConfigImpl.class);
      context.registerInjectActivateService(MediaHandlerConfigImpl.class);
      context.registerInjectActivateService(MediaFormatProviderImpl.class);

#end
    }
  };

}
