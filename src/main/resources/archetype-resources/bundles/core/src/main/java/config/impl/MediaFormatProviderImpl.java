#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.config.impl;

import org.osgi.service.component.annotations.Component;

import io.wcm.handler.media.spi.MediaFormatProvider;

import ${package}.config.MediaFormats;

/**
 * Media format provider.
 */
@Component(service = MediaFormatProvider.class)
public class MediaFormatProviderImpl extends MediaFormatProvider {

  /**
   * Constructor
   */
  public MediaFormatProviderImpl() {
    super(MediaFormats.class);
  }

}
