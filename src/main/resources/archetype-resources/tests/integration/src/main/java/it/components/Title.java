package ${package}.it.components;

import com.adobe.cq.testing.client.ComponentClient;
import com.adobe.cq.testing.client.components.AbstractComponent;

/**
 * Integration test wrapper for title component.
 */
public class Title extends AbstractComponent {

  /**
   * @param client Client
   * @param pagePath Page path
   * @param location Location
   * @param nameHint Name hint
   */
  public Title(ComponentClient client, String pagePath, String location, String nameHint) {
    super(client, pagePath, location, nameHint);
  }

  @Override
  public String getResourceType() {
    return "${projectName}/core/components/content/title";
  }

}
