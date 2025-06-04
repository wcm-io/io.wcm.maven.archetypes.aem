#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.config.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;

import com.day.cq.wcm.api.Page;

#if ( $optionWcmioSiteApi == "y" )
import io.wcm.handler.link.markup.SimpleLinkMarkupBuilder;
#end
import io.wcm.handler.link.spi.LinkHandlerConfig;
#if ( $optionWcmioSiteApi == "y" )
import io.wcm.handler.link.spi.LinkMarkupBuilder;
import io.wcm.handler.link.spi.LinkProcessor;
#end
import io.wcm.handler.link.spi.LinkType;
import io.wcm.handler.link.type.ExternalLinkType;
import io.wcm.handler.link.type.InternalCrossContextLinkType;
import io.wcm.handler.link.type.InternalLinkType;
import io.wcm.handler.link.type.MediaLinkType;
#if ( $optionWcmioSiteApi == "y" )
import io.wcm.siteapi.handler.link.SiteApiLinkMarkupBuilder;
import io.wcm.siteapi.handler.link.SiteApiLinkPreProcessor;
#end
import io.wcm.wcm.commons.util.Template;

import ${package}.config.AppTemplate;

/**
 * Link handler configuration.
 */
@Component(service = LinkHandlerConfig.class)
public class LinkHandlerConfigImpl extends LinkHandlerConfig {

  private static final List<Class<? extends LinkType>> LINK_TYPES = List.of(
      InternalLinkType.class,
      InternalCrossContextLinkType.class,
      ExternalLinkType.class,
      MediaLinkType.class);

#if ( $optionWcmioSiteApi == "y" )
  private static final List<Class<? extends LinkProcessor>> PRE_PROCESSORS = List.of(
      SiteApiLinkPreProcessor.class);

  private static final List<Class<? extends LinkMarkupBuilder>> LINK_MARKUP_BUILDERS = List.of(
      SiteApiLinkMarkupBuilder.class,
      SimpleLinkMarkupBuilder.class);

#end
  @Override
  @SuppressWarnings("squid:S2384") // returned list is immutable
  public @NotNull List<Class<? extends LinkType>> getLinkTypes() {
    return LINK_TYPES;
  }

#if ( $optionWcmioSiteApi == "y" )
  @Override
  @SuppressWarnings("squid:S2384") // returned list is immutable
  public @NotNull List<Class<? extends LinkProcessor>> getPreProcessors() {
    return PRE_PROCESSORS;
  }

  @Override
  @SuppressWarnings("squid:S2384") // returned list is immutable
  public @NotNull List<Class<? extends LinkMarkupBuilder>> getMarkupBuilders() {
    return LINK_MARKUP_BUILDERS;
  }

#end
  @Override
  public boolean isValidLinkTarget(@NotNull Page page) {
    return !Template.is(page, AppTemplate.ADMIN_STRUCTURE_ELEMENT);
  }

  @Override
  public boolean isRedirect(@NotNull Page page) {
    return Template.is(page, AppTemplate.ADMIN_REDIRECT);
  }

  @Override
  public @Nullable String getLinkRootPath(@NotNull Page page, @NotNull String linkTypeId) {
    if (StringUtils.equals(linkTypeId, MediaLinkType.ID)) {
      return MediaHandlerConfigImpl.DAM_ROOT;
    }
    return super.getLinkRootPath(page, linkTypeId);
  }

}
