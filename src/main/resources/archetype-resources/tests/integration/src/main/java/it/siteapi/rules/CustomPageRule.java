package ${package}.it.siteapi.rules;

import static ${package}.it.siteapi.Constants.API_ENTRYPOINT_PATH;
import static ${package}.it.siteapi.Constants.CONTENT_TEMPLATE_PATH;

import org.apache.sling.testing.junit.rules.instance.Instance;

import com.adobe.cq.testing.junit.rules.Page;

/**
 * Extends Page Rule to set project-specific paths.
 * Rule is responsible for the page creation and page clean up.
 */
public class CustomPageRule extends Page {

  /**
   * Constructor.
   * @param quickstartRule CQ instance rule
   */
  public CustomPageRule(Instance quickstartRule) {
    super(quickstartRule);
  }

  @Override
  protected String initialParentPath() {
    return API_ENTRYPOINT_PATH;
  }

  @Override
  protected String initialTemplatePath() {
    return CONTENT_TEMPLATE_PATH;
  }

}
