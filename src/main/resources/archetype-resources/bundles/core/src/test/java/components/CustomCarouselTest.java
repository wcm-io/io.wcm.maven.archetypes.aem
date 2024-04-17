#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.components;

#if ($optionWcmioHandler == 'n')
import static com.day.cq.commons.DownloadResource.PN_REFERENCE;
#else
import static io.wcm.handler.media.MediaNameConstants.PN_MEDIA_REF_STANDARD;
#end
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

#if ($optionWcmioHandler == 'n')
import com.adobe.cq.wcm.core.components.models.Image;
#end
import com.day.cq.wcm.api.Page;

#if ($optionWcmioHandler == 'y')
import io.wcm.handler.media.Media;
import io.wcm.sling.commons.adapter.AdaptTo;
#end
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static ${package}.components.CustomCarousel.NN_SLIDES;
import ${package}.testcontext.AppAemContext;

@ExtendWith(AemContextExtension.class)
class CustomCarouselTest {

  private final AemContext context = AppAemContext.newAemContext();

  private Page page;
  private Resource resource;

  @BeforeEach
  void setUp() {
    page = context.create().page("/content/mypage");
    resource = context.create().resource(page, "myresource");
    context.currentResource(resource);
  }

  @Test
  void testId() {
#if ($optionWcmioHandler == 'y')
    CustomCarousel underTest = AdaptTo.notNull(context.request(), CustomCarousel.class);
#else
    CustomCarousel underTest = context.request().adaptTo(CustomCarousel.class);
#end
    assertNotNull(underTest.getId());
  }

  @Test
  void testSlideImageUrls() {
    context.create().asset("/content/dam/slides/slide1.png", 1200, 450, "image/png");
    context.create().asset("/content/dam/slides/slide2.png", 1200, 450, "image/png");

    context.build().resource(resource.getPath() + "/" + NN_SLIDES)
        .siblingsMode()
        .resource("item1", #if($optionWcmioHandler=='n')PN_REFERENCE#{else}PN_MEDIA_REF_STANDARD#end, "/content/dam/slides/slide1.png")
        .resource("item2", #if($optionWcmioHandler=='n')PN_REFERENCE#{else}PN_MEDIA_REF_STANDARD#end, "/content/dam/slides/slide2.png");

#if ($optionWcmioHandler == 'y')
    CustomCarousel underTest = AdaptTo.notNull(context.request(), CustomCarousel.class);
#else
    CustomCarousel underTest = context.request().adaptTo(CustomCarousel.class);
#end
#if ($optionWcmioHandler == 'n')
    assertEquals(List.of(
        "/content/mypage/_jcr_content/myresource/slides/item1.coreimg.png",
        "/content/mypage/_jcr_content/myresource/slides/item2.coreimg.png"),
        underTest.getSlideImages().stream()
            .map(Image::getSrc)
            .collect(Collectors.toList()));
#else
    assertEquals(List.of(
        "/content/dam/slides/slide1.png/_jcr_content/renditions/original./slide1.png",
        "/content/dam/slides/slide2.png/_jcr_content/renditions/original./slide2.png"),
        underTest.getSlideImages().stream()
            .map(Media::getUrl)
            .collect(Collectors.toList()));
#end
  }

  @Test
  void testEmptySlideImageUrls() {
#if ($optionWcmioHandler == 'y')
    CustomCarousel underTest = AdaptTo.notNull(context.request(), CustomCarousel.class);
#else
    CustomCarousel underTest = context.request().adaptTo(CustomCarousel.class);
#end
    assertTrue(underTest.getSlideImages().isEmpty());
  }

}
