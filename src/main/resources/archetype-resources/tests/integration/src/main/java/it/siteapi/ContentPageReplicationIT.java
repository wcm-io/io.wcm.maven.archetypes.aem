package ${package}.it.siteapi;

import static ${package}.it.siteapi.Constants.API_ENTRYPOINT_PATH;
import static ${package}.it.siteapi.Constants.EXTENSION;
import static ${package}.it.siteapi.Constants.SELECTOR;
import static ${package}.it.siteapi.Constants.SPEC_VERSIONS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.util.poller.Polling;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.client.ReplicationClient;
import com.adobe.cq.testing.junit.assertion.CQAssert;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;
import com.adobe.cq.testing.junit.rules.CQRule;
import com.adobe.cq.testing.junit.rules.Page;
import com.jayway.jsonpath.JsonPath;

import io.wcm.siteapi.integrationtestsupport.IntegrationTestContext;
import io.wcm.siteapi.integrationtestsupport.IntegrationTestContextBuilder;
import io.wcm.siteapi.integrationtestsupport.linkextractor.ContentInternalLinks;
import io.wcm.siteapi.openapi.validator.ContentValidationException;

import ${package}.it.siteapi.rules.CustomPageRule;

/**
 * Integration test that handles new page creation and replication.
 */
@RunWith(Parameterized.class)
public class ContentPageReplicationIT {

  private static final Logger log = LoggerFactory.getLogger(ContentPageReplicationIT.class);

  private static final long TIMEOUT = MINUTES.toMillis(12);
  private static final long DEFAULT_DELAY = SECONDS.toMillis(3);

  private static final String PROCESSOR_CONTENT = "content";
  private static final String PROCESSOR_NAVIGATION = "navigation";

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
  public ContentPageReplicationIT(String apiVersion) {
    log.info("-------- Page Replication Site API {} --------", apiVersion);
    this.apiVersion = apiVersion;
  }

  private final String apiVersion;

  /**
   * The CQAuthorPublishClassRule represents an author and a publish services.
   * Force basic authentication (=no auth in this case) for publish instance.
   */
  @ClassRule
  #if ( $optionAemVersion == "cloud" )
  public static final CQAuthorPublishClassRule CQ_AUTHOR_PUBLISH_CLASS_RULE = new CQAuthorPublishClassRule(false, true);
  #else
  public static final CQAuthorPublishClassRule CQ_AUTHOR_PUBLISH_CLASS_RULE = new CQAuthorPublishClassRule(false);
  #end

  /**
   * CQRule.
   */
  @Rule
  public CQRule cqBaseRule = new CQRule(CQ_AUTHOR_PUBLISH_CLASS_RULE.authorRule, CQ_AUTHOR_PUBLISH_CLASS_RULE.publishRule);

  private static ReplicationClient authorClient;
  private static CQClient publishClient;

  /**
   * Page rule creates a temporary page and removes it at the end of test execution.
   */
  @Rule
  public Page root = new CustomPageRule(CQ_AUTHOR_PUBLISH_CLASS_RULE.authorRule);

  /**
   * Set up author and publish clients.
   */
  @BeforeClass
  @SuppressWarnings("null")
  public static void beforeClass() {
    authorClient = CQ_AUTHOR_PUBLISH_CLASS_RULE.authorRule.getAdminClient(ReplicationClient.class);
    publishClient = CQ_AUTHOR_PUBLISH_CLASS_RULE.publishRule.getClient(CQClient.class, null, null);
  }

  /**
   * Creates a temporary page, activates and deactivates it and validates responses on publish.
   * @throws InterruptedException Test interrupted
   * @throws ClientException Client exception
   * @throws ContentValidationException Content validation exception
   */
  @Test
  public void replicateAndCheckOnPublish() throws InterruptedException, ClientException, ContentValidationException {
    IntegrationTestContext context = new IntegrationTestContextBuilder()
        .publishUrl(publishClient.getUrl().toString())
        .selector(SELECTOR)
        .apiVersion(apiVersion)
        .extension(EXTENSION)
        .build();

    String contentPageUrl = context.buildSiteApiUrl(root.getPath(), PROCESSOR_CONTENT);
    String navigationPageUrl = context.buildSiteApiUrl(API_ENTRYPOINT_PATH, PROCESSOR_NAVIGATION);

    // page rule creates a new, temporary page
    CQAssert.assertCQPageExistsWithTimeout(authorClient, root.getPath(), TIMEOUT, DEFAULT_DELAY);

    // activate page to publish and wait until it can be requested there
    authorClient.activate(root.getPath(), HttpStatus.SC_OK);
    waitForPageOnPublish(context, contentPageUrl, HttpStatus.SC_OK);

    // validate JSON of published page on publish
    final String contentJson = context.getHttpClient().getBody(contentPageUrl);
    context.getValidator(PROCESSOR_CONTENT).validate(contentJson);

    // ensure published page is part of navigation JSON
    final String navigationJson = context.getHttpClient().getBody(navigationPageUrl);
    context.getValidator(PROCESSOR_NAVIGATION).validate(navigationJson);

    Set<String> navigationLinks = new ContentInternalLinks().getLinks(JsonPath.parse(navigationJson))
        .collect(Collectors.toSet());
    assertTrue("Replication of page failed, " + contentPageUrl + " not found in response of " + navigationPageUrl,
        navigationLinks.contains(contentPageUrl));

    // deactivate page and wait until it's not longer requestable on publish
    authorClient.deactivate(root.getPath(), HttpStatus.SC_OK);
    waitForPageOnPublish(context, contentPageUrl, HttpStatus.SC_NOT_FOUND);
  }

  private void waitForPageOnPublish(@NotNull IntegrationTestContext context,
      @NotNull String url, int status) throws InterruptedException {
    log.info("Await HTTP {} for {}", status, url);
    try {
      new Polling(() -> {
        return context.getHttpClient().get(url).statusCode() == status;
      }).poll(TIMEOUT, DEFAULT_DELAY);
    }
    catch (TimeoutException e) {
      Assert.fail("Assertion failed because timeout reached out for: " + url);
    }
  }

}
