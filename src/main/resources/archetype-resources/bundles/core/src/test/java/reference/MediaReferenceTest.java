#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;

import io.wcm.handler.media.Media;
import io.wcm.handler.media.MediaHandler;
import io.wcm.siteapi.handler.media.MediaDecorator;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.wcm.commons.contenttype.ContentType;

import ${package}.testcontext.AppAemContext;

@ExtendWith(AemContextExtension.class)
class MediaReferenceTest {

  private final AemContext context = AppAemContext.newAemContext();

  private Page page;
  private MediaHandler mediaHandler;
  private MediaDecorator<MediaReference> decorator;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    page = context.currentPage(context.create().page("/content/mypage"));
    mediaHandler = AdaptTo.notNull(page.getContentResource(), MediaHandler.class);
    decorator = context.getService(MediaDecorator.class);
  }

  @Test
  void testMedia() {
    Asset asset = context.create().asset("/content/dam/test.jpg", 10, 10, ContentType.JPEG);
    Media media = mediaHandler.get(asset.getPath()).build();

    MediaReference ref = decorator.apply(media);
    assertNotNull(ref);
    assertEquals("/content/dam/test.jpg/_jcr_content/renditions/original./test.jpg", ref.getUrl());
    assertEquals(asset.getPath(), ref.getPath());
    assertEquals(1, ref.getRenditions().size());
  }

}
