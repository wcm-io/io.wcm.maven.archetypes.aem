#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.reference;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import io.wcm.handler.media.Media;
import io.wcm.handler.media.Rendition;

/**
 * Represents a media reference in Site API.
 */
public class MediaReference {

  private final Media media;

  /**
   * @param media Valid media
   */
  public MediaReference(Media media) {
    this.media = media;
  }

  /**
   * @return Main asset URL
   */
  public @NotNull String getUrl() {
    return media.getUrl();
  }

  /**
   * @return Asset path
   */
  public @NotNull String getPath() {
    return media.getAsset().getPath();
  }

  /**
   * @return Renditions
   */
  public Collection<Rendition> getRenditions() {
    return media.getRenditions();
  }

}
