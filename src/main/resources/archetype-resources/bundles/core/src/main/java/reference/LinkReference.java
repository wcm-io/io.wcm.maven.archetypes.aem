#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.reference;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.day.cq.wcm.api.Page;

import io.wcm.handler.link.Link;

/**
 * Represents a link in Site API.
 */
public class LinkReference {

  private final Link link;

  /**
   * @param link Valid link
   */
  public LinkReference(Link link) {
    this.link = link;
  }

  /**
   * @return Link type
   */
  public @NotNull String getType() {
    return link.getLinkType().getId();
  }

  /**
   * @return Link URL
   */
  public @NotNull String getUrl() {
    return link.getUrl();
  }

  /**
   * @return Link window target
   */
  public @Nullable String getTarget() {
    return link.getLinkRequest().getLinkArgs().getWindowTarget();
  }

  /**
   * @return Page path (for internal links)
   */
  public @Nullable String getPath() {
    Page page = link.getTargetPage();
    if (page != null) {
      return page.getPath();
    }
    else {
      return null;
    }
  }

}
