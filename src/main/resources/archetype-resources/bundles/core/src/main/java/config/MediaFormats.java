#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.config;

import static io.wcm.handler.media.format.MediaFormatBuilder.create;

import io.wcm.handler.media.format.MediaFormat;

/**
 * Media formats
 */
public final class MediaFormats {

  private MediaFormats() {
    // constants only
  }

  /**
   * Stage header (1.33)
   */
  public static final MediaFormat CONTENT = create("content")
      .label("Content (1:3.33)")
      .ratio(10, 3)
      .fixedDimension(1000, 300)
      .extensions("gif", "jpg", "jpeg", "png")
      .build();

  /**
   * Download
   */
  public static final MediaFormat DOWNLOAD = create("download")
      .label("Download")
      .extensions("pdf", "zip", "ppt", "pptx", "doc", "docx")
      .download(true)
      .build();

}
