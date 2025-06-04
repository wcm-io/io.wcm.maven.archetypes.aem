#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.reference.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;

import io.wcm.handler.media.Media;
import io.wcm.siteapi.handler.media.MediaDecorator;

import ${package}.reference.MediaReference;

/**
 * Decorates media with project-specific {@link MediaReference}.
 */
@Component(service = MediaDecorator.class)
public class MediaReferenceDecorator implements MediaDecorator<MediaReference> {

  @Override
  public @Nullable MediaReference apply(@NotNull Media media) {
    return new MediaReference(media);
  }

}
