package ${package}.it.siteapi;

import java.util.Collections;
import java.util.List;

import io.wcm.siteapi.openapi.validator.OpenApiSpecVersions;

/**
 * Constants for test execution.
 */
public final class Constants {

  /**
   * API site root entrypoint path.
   */
  @SuppressWarnings("java:S1075") // content path
  public static final String API_ENTRYPOINT_PATH = "/content/${projectName}/en";

  /**
   * Content template to be used in page replication test.
   */
  @SuppressWarnings("java:S1075") // content path
  public static final String CONTENT_TEMPLATE_PATH = "/conf/${projectName}/settings/wcm/templates/contentpage";

  /**
   * Resource types of components containing rich text.
   */
  public static final List<String> RICHTEXT_COMPONENT_RESOURCE_TYPES = Collections.unmodifiableList(List.of(
      "${projectName}/core/components/content/text"
  ));

  /**
   * Site API selector.
   */
  public static final String SELECTOR = "site";

  /**
   * Site API extension.
   */
  public static final String EXTENSION = "api";

  /**
   * Detects all OAS3 specification present in classpath.
   */
  public static final OpenApiSpecVersions SPEC_VERSIONS = new OpenApiSpecVersions();

  private Constants() {
    // constants only
  }

}
