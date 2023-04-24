#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.day.cq.wcm.api.Page;

import io.wcm.handler.link.Link;
import io.wcm.handler.link.LinkHandler;
import io.wcm.handler.link.type.ExternalLinkType;
import io.wcm.handler.link.type.InternalLinkType;
import io.wcm.siteapi.handler.link.LinkDecorator;
import io.wcm.sling.commons.adapter.AdaptTo;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import ${package}.testcontext.AppAemContext;

@ExtendWith(AemContextExtension.class)
class LinkReferenceTest {

  private final AemContext context = AppAemContext.newAemContext();

  private Page page;
  private LinkHandler linkHandler;
  private LinkDecorator<LinkReference> decorator;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    page = context.currentPage(context.create().page("/content/mypage"));
    linkHandler = AdaptTo.notNull(page.getContentResource(), LinkHandler.class);
    decorator = context.getService(LinkDecorator.class);
  }

  @Test
  void testInternalLink() {
    Link link = linkHandler.get(page).build();

    LinkReference ref = decorator.apply(link);
    assertNotNull(ref);
    assertEquals(InternalLinkType.ID, ref.getType());
    assertEquals("/content/mypage.html", ref.getUrl());
    assertEquals("/content/mypage", ref.getPath());
    assertNull(ref.getTarget());
  }

  @Test
  void testExternalLink() {
    Link link = linkHandler.get("https://myhost")
        .windowTarget("_blank")
        .build();

    LinkReference ref = decorator.apply(link);
    assertNotNull(ref);
    assertEquals(ExternalLinkType.ID, ref.getType());
    assertEquals("https://myhost", ref.getUrl());
    assertNull(ref.getPath());
    assertEquals("_blank", ref.getTarget());
  }

}
