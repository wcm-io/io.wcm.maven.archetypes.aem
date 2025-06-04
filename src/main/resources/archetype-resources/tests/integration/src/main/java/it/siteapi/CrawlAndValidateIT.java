package ${package}.it.siteapi;

import static ${package}.it.siteapi.Constants.API_ENTRYPOINT_PATH;
import static ${package}.it.siteapi.Constants.EXTENSION;
import static ${package}.it.siteapi.Constants.RICHTEXT_COMPONENT_RESOURCE_TYPES;
import static ${package}.it.siteapi.Constants.SELECTOR;
import static ${package}.it.siteapi.Constants.SPEC_VERSIONS;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;

import io.wcm.siteapi.integrationtestsupport.IntegrationTestContext;
import io.wcm.siteapi.integrationtestsupport.IntegrationTestContextBuilder;
import io.wcm.siteapi.integrationtestsupport.crawler.Crawler;
import io.wcm.siteapi.integrationtestsupport.linkextractor.ContentInternalLinks;
import io.wcm.siteapi.integrationtestsupport.linkextractor.IndexLinks;
import io.wcm.siteapi.integrationtestsupport.linkextractor.LinkExtractor;
import io.wcm.siteapi.integrationtestsupport.linkextractor.RichTextInternalLinks;

/**
 * Integration test that crawls recursively through all links starting from the API index
 * and validates all responses against the JSON schemas of the OAS3 spec.
 */
@RunWith(Parameterized.class)
public class CrawlAndValidateIT {

  private static final Logger log = LoggerFactory.getLogger(CrawlAndValidateIT.class);

  /**
   * AEM testing clients class rule.
   * Force basic authentication (= no auth in this case) as this is only used for publish.
   */
  @ClassRule
  public static final CQAuthorPublishClassRule CQ_AUTHOR_PUBLISH_CLASS_RULE = new CQAuthorPublishClassRule(true);

  private final String apiVersion;
  private IntegrationTestContext context;

  /**
   * @return All defined API versions
   */
  @Parameterized.Parameters
  public static Collection<String> data() {
    return SPEC_VERSIONS.getAllVersions();
  }

  /**
   * @param apiVersion API version
   */
  public CrawlAndValidateIT(String apiVersion) {
    log.info("-------- Crawl Site API {} --------", apiVersion);
    this.apiVersion = apiVersion;
  }

  /**
   * Setup test.
   */
  @Before
  @SuppressWarnings("null")
  public void setUp() {
    CQClient cqClient = CQ_AUTHOR_PUBLISH_CLASS_RULE.publishRule.getClient(CQClient.class, null, null);
    context = new IntegrationTestContextBuilder()
        .publishUrl(cqClient.getUrl().toString())
        .selector(SELECTOR)
        .apiVersion(apiVersion)
        .extension(EXTENSION)
        .build();
  }

  /**
   * Crawls through all content of the given API endpoint.
   */
  @Test
  public void crawlAll() {
    // prepare crawler with link extractors supporting the project's Site API
    Crawler crawler = new Crawler(context, List.<LinkExtractor>of(
        new IndexLinks(),
        new ContentInternalLinks(),
        new RichTextInternalLinks(RICHTEXT_COMPONENT_RESOURCE_TYPES)));

    // Crawl API recursively
    String indexUrl = context.buildSiteApiUrl(API_ENTRYPOINT_PATH, "index");
    crawler.start(indexUrl);

    log.info("Visited {} URLs: {}", apiVersion, crawler.numberOfVisits());

    // Report failures
    int failureCount = crawler.numberOfFailedVisits();
    if (failureCount > 0 && log.isErrorEnabled()) {
      log.error("Failed {} visits: {}\n{}\n", apiVersion, failureCount, StringUtils.join(crawler.failedVisitUrls(), "\n"));
    }
    assertEquals("Failed visits", 0, failureCount);
  }

}
