#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.reference.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;

import io.wcm.handler.link.Link;
import io.wcm.siteapi.handler.link.LinkDecorator;

import ${package}.reference.LinkReference;

/**
 * Decorates link with project-specific {@link LinkReference}.
 */
@Component(service = LinkDecorator.class)
public class LinkReferenceDecorator implements LinkDecorator<LinkReference> {

  @Override
  public @Nullable LinkReference apply(@NotNull Link link) {
    return new LinkReference(link);
  }

}
