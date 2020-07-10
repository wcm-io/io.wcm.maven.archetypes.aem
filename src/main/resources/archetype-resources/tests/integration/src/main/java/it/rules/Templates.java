package ${package}.it.rules;

/**
 * Template paths.
 */
public final class Templates {

  private Templates() {
    // constants only
  }

  /**
   * Path of the homepage template.
   */
  public static final String HOMEPAGE_TEMPLATE_PATH = "#if($optionEditableTemplates=='y')/conf/${projectName}/settings/wcm/templates/homepage#{else}/apps/${projectName}/core/templates/homepage#end";

  /**
   * Path of the contentpage template.
   */
  public static final String CONTENTPAGE_TEMPLATE_PATH = "#if($optionEditableTemplates=='y')/conf/${projectName}/settings/wcm/templates/contentpage#{else}/apps/${projectName}/core/templates/contentpage#end";

}
