package ${package}.it.tests;

import static ${package}.it.rules.Templates.CONTENTPAGE_TEMPLATE_PATH;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.SlingHttpResponse;
import org.apache.sling.testing.clients.util.poller.Polling;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.client.ComponentClient;
import com.adobe.cq.testing.client.ReplicationClient;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;
import com.adobe.cq.testing.junit.rules.CQRule;

import ${package}.it.components.Title;
import ${package}.it.rules.SiteRule;

/**
 * Sample integration test that creates a content page with a component on it on author and publish.
 */
public class CreateContentPageIT {

  private static final Logger log = LoggerFactory.getLogger(CreateContentPageIT.class);

  /**
   * Represents author and publish service. Hostname and port are read from system properties.
   */
  @ClassRule
#if ( $optionAemVersion == "cloud" )
  public static final CQAuthorPublishClassRule CQ_AUTHOR_PUBLISH_CLASS_RULE = new CQAuthorPublishClassRule(false, true);
#else
  public static final CQAuthorPublishClassRule CQ_AUTHOR_PUBLISH_CLASS_RULE = new CQAuthorPublishClassRule();
#end

  /**
   * Decorates the test and adds additional functionality on top of it, like session stickyness,
   * test filtering and identification of the test on the remote service.
   */
  @Rule
  public CQRule cqBaseRule = new CQRule(CQ_AUTHOR_PUBLISH_CLASS_RULE.authorRule, CQ_AUTHOR_PUBLISH_CLASS_RULE.publishRule);

  private static CQClient authorClient;
  private static CQClient publishClient;

  /**
   * Create two CQClient instances bound to the admin user on both the author and publish service.
   */
  @BeforeClass
  @SuppressWarnings("null")
  public static void beforeClass() {
    authorClient = CQ_AUTHOR_PUBLISH_CLASS_RULE.authorRule.getAdminClient(ReplicationClient.class);
    publishClient = CQ_AUTHOR_PUBLISH_CLASS_RULE.publishRule.getClient(CQClient.class, null, null);
  }


  /**
   * This rules creates a site with a root page using the project's homepage template.
   */
  @Rule
  public SiteRule site = new SiteRule(CQ_AUTHOR_PUBLISH_CLASS_RULE.authorRule);

  /**
   * Create a content page with a component.
   */
  @Test
  @SuppressWarnings({
      "null",
      "AEM Rules:AEM-2" // no access to com.day.cq.commons.jcr.JcrConstants in integration tests
  })
  public void testCreateContentPage() throws ClientException, InterruptedException, IOException {

    log.info("Create content page below {}", site.getRootPath());
    String contentPagePath;
    try (SlingHttpResponse response = authorClient.createPage("my-content", "My Content", site.getRootPath(),
        CONTENTPAGE_TEMPLATE_PATH)) {
      contentPagePath = response.getSlingPath();
    }

    log.info("Create title component in {}", contentPagePath);
    ComponentClient componentClient = authorClient.adaptTo(ComponentClient.class);
    componentClient.setDefaultComponentRelativeLocation("/jcr:content/root/content/*");
    Title title = componentClient.addComponent(Title.class, contentPagePath);

    log.info("Set custom title for {}", title.getComponentPath());
    String titleString = "Current time: " + DateFormat.getDateTimeInstance().format(new Date());
    title.setProperty("jcr:title", titleString);
    title.save();

    log.info("Activate page {}", contentPagePath);
    ReplicationClient replicationClient = authorClient.adaptTo(ReplicationClient.class);
    replicationClient.activate(contentPagePath);

    log.info("Assert page on publish {}", contentPagePath);
    assertHtmlPageExistsWithTimeout(publishClient, contentPagePath, 20000, 1000);

    log.info("Assert custom title is set on publish {}", contentPagePath);
    String url = publishClient.getUrl(contentPagePath + ".html").toString();
    try (SlingHttpResponse response = publishClient.doGet(url, 200)) {
      response.checkContentContains(titleString);
    }
  }

  static void assertHtmlPageExistsWithTimeout(final CQClient client, final String path,
      final long timeout, final long delay) throws InterruptedException {
    Polling pageExistsPolling = new Polling() {
      @Override
      public Boolean call() {
        // Get HTML page content
        String url = client.getUrl(path + ".html").toString();
        try {
          client.doGet(url, 200);
        }
        catch (ClientException ex) {
          return false;
        }
        return true;
      }
    };
    try {
      pageExistsPolling.poll(timeout, delay);
    }
    catch (TimeoutException e) {
#if ( $optionAemVersion == "cloud" )
      if (pageExistsPolling.getLastException() != null) {
        log.error("HTML page existence check timed out. Last Exception: ", pageExistsPolling.getLastException());
      }
#end
      Assert.fail("Timeout reached while waiting for HTML page " + path);
    }
  }

}
