#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.siteapispec;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.wcm.siteapi.openapi.validator.OpenApiSpec;
import io.wcm.siteapi.openapi.validator.OpenApiSpecVersions;

/**
 * Validates all OAS3 specification at <code>/site-api-spec/site-api*.yaml</code>.
 */
class SpecificationValidationTest {

  private static final OpenApiSpecVersions VERSIONS = new OpenApiSpecVersions();

  public static Stream<String> getAllVersions() {
    return VERSIONS.getAllVersions().stream();
  }

  @ParameterizedTest
  @MethodSource("getAllVersions")
  void validate(String version) {
    OpenApiSpec spec = VERSIONS.get(version);
    assertEquals(version, spec.getVersion());
  }

}
