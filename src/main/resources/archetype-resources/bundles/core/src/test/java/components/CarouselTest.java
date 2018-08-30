#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.components;

import static com.day.cq.commons.DownloadResource.PN_REFERENCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Collectors;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.Image;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.ImmutableList;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static ${package}.components.Carousel.NN_SLIDES;
import ${package}.testcontext.AppAemContext;

@ExtendWith(AemContextExtension.class)
@SuppressWarnings("null")
public class CarouselTest {

  private final AemContext context = AppAemContext.newAemContext();

  private Page page;
  private Resource resource;

  @BeforeEach
  public void setUp() {
    page = context.create().page("/content/mypage");
    resource = context.create().resource(page.getContentResource().getPath() + "/myresource");
    context.currentResource(resource);
  }

  @Test
  public void testId() {
    Carousel underTest = context.request().adaptTo(Carousel.class);
    assertNotNull(underTest.getId());
  }

  @Test
  public void testSlideImageUrls() {
    context.create().asset("/content/dam/slides/slide1.png", 10, 10, "image/png");
    context.create().asset("/content/dam/slides/slide2.png", 10, 10, "image/png");

    context.build().resource(resource.getPath() + "/" + NN_SLIDES)
        .siblingsMode()
        .resource("item1", PN_REFERENCE, "/content/dam/slides/slide1.png")
        .resource("item2", PN_REFERENCE, "/content/dam/slides/slide2.png");

    Carousel underTest = context.request().adaptTo(Carousel.class);
    assertEquals(ImmutableList.of(
        "/content/mypage/_jcr_content/myresource/slides/item1.img.png",
        "/content/mypage/_jcr_content/myresource/slides/item2.img.png"),
        underTest.getSlideImages().stream()
            .map(Image::getSrc)
            .collect(Collectors.toList()));
  }

  @Test
  public void testEmptySlideImageUrls() {
    Carousel underTest = context.request().adaptTo(Carousel.class);
    assertTrue(underTest.getSlideImages().isEmpty());
  }

}
