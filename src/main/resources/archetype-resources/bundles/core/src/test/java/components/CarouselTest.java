#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.components;

#if ($optionWcmioHandler == 'n')
import static com.day.cq.commons.DownloadResource.PN_REFERENCE;
#else
import static io.wcm.handler.media.MediaNameConstants.PN_MEDIA_REF;
#end
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Collectors;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

#if ($optionWcmioHandler == 'n')
import com.adobe.cq.wcm.core.components.models.Image;
#end
import com.day.cq.wcm.api.Page;
import com.google.common.collect.ImmutableList;

#if ($optionWcmioHandler == 'y')
import io.wcm.handler.media.Media;
#end
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static ${package}.components.Carousel.NN_SLIDES;
import ${package}.testcontext.AppAemContext;

@ExtendWith(AemContextExtension.class)
@SuppressWarnings("null")
class CarouselTest {

  private final AemContext context = AppAemContext.newAemContext();

  private Page page;
  private Resource resource;

  @BeforeEach
  void setUp() {
    page = context.create().page("/content/mypage");
    resource = context.create().resource(page.getContentResource().getPath() + "/myresource");
    context.currentResource(resource);
  }

  @Test
  void testId() {
    Carousel underTest = context.request().adaptTo(Carousel.class);
    assertNotNull(underTest.getId());
  }

  @Test
  void testSlideImageUrls() {
    context.create().asset("/content/dam/slides/slide1.png", 80, 30, "image/png");
    context.create().asset("/content/dam/slides/slide2.png", 80, 30, "image/png");

    context.build().resource(resource.getPath() + "/" + NN_SLIDES)
        .siblingsMode()
        .resource("item1", #if($optionWcmioHandler=='n')PN_REFERENCE#{else}PN_MEDIA_REF#end, "/content/dam/slides/slide1.png")
        .resource("item2", #if($optionWcmioHandler=='n')PN_REFERENCE#{else}PN_MEDIA_REF#end, "/content/dam/slides/slide2.png");

    Carousel underTest = context.request().adaptTo(Carousel.class);
#if ($optionWcmioHandler == 'n')
    assertEquals(ImmutableList.of(
        "/content/mypage/_jcr_content/myresource/slides/item1.img.png",
        "/content/mypage/_jcr_content/myresource/slides/item2.img.png"),
        underTest.getSlideImages().stream()
            .map(Image::getSrc)
            .collect(Collectors.toList()));
#else
    assertEquals(ImmutableList.of(
        "/content/dam/slides/slide1.png/_jcr_content/renditions/original./slide1.png",
        "/content/dam/slides/slide2.png/_jcr_content/renditions/original./slide2.png"),
        underTest.getSlideImages().stream()
            .map(Media::getUrl)
            .collect(Collectors.toList()));
#end
  }

  @Test
  void testEmptySlideImageUrls() {
    Carousel underTest = context.request().adaptTo(Carousel.class);
    assertTrue(underTest.getSlideImages().isEmpty());
  }

}
