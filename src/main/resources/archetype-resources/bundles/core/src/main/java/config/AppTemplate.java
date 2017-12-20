#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.config;

import io.wcm.wcm.commons.util.Template;
import io.wcm.wcm.commons.util.TemplatePathInfo;

/**
 * List of templates with special handling in code.
 */
public enum AppTemplate implements TemplatePathInfo {

  /**
   * Structure element
   */
  ADMIN_STRUCTURE_ELEMENT("/apps/${projectName}#if($optionMultiBundleLayout=='y')/core#end/templates/admin/structureElement"),

  /**
   * Redirect
   */
  ADMIN_REDIRECT("/apps/${projectName}#if($optionMultiBundleLayout=='y')/core#end/templates/admin/redirect"),

  /**
   * Content
   */
  EDITORIAL_CONTENT("/apps/${projectName}#if($optionMultiBundleLayout=='y')/core#end/templates/content/content"),

  /**
   * Homepage
   */
  EDITORIAL_HOMEPAGE("/apps/${projectName}#if($optionMultiBundleLayout=='y')/core#end/templates/content/homepage");

  private final String templatePath;
  private final String resourceType;

  AppTemplate(String templatePath) {
    this.templatePath = templatePath;
    this.resourceType = Template.getResourceTypeFromTemplatePath(templatePath);
  }

  AppTemplate(String templatePath, String resourceType) {
    this.templatePath = templatePath;
    this.resourceType = resourceType;
  }

  /**
   * Template path
   * @return Path
   */
  @Override
  public String getTemplatePath() {
    return templatePath;
  }

  /**
   * Resource type
   * @return Path
   */
  @Override
  public String getResourceType() {
    return resourceType;
  }

}
