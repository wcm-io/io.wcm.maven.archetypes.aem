#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.config.impl;

import org.osgi.service.component.annotations.Component;

import io.wcm.handler.media.spi.MediaHandlerConfig;

/**
 * Media handler configuration.
 */
@Component(service = MediaHandlerConfig.class)
public class MediaHandlerConfigImpl extends MediaHandlerConfig {

  @Override
  public boolean useAdobeStandardNames() {
    // use standard names for asset references as used by the core components
    return true;
  }

}
