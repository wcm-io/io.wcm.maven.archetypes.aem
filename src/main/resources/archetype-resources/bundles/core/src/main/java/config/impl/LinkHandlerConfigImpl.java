#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.config.impl;

import org.osgi.service.component.annotations.Component;

import com.day.cq.wcm.api.Page;

import io.wcm.handler.link.spi.LinkHandlerConfig;
import io.wcm.wcm.commons.util.Template;

import ${package}.config.AppTemplate;

/**
 * Link handler configuration.
 */
@Component(service = LinkHandlerConfig.class)
public class LinkHandlerConfigImpl extends LinkHandlerConfig {

  @Override
  public boolean isValidLinkTarget(Page page) {
    return !Template.is(page, AppTemplate.ADMIN_STRUCTURE_ELEMENT);
  }

  @Override
  public boolean isRedirect(Page page) {
    return Template.is(page, AppTemplate.ADMIN_REDIRECT);
  }

}
