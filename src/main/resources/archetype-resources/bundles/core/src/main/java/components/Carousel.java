#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
#if ($optionWcmioHandler == 'n')
#set( $imageClass = "Image" )
#else
#set( $imageClass = "Media" )
#end
package ${package}.components;

#if ($optionWcmioHandler == 'y')
import static io.wcm.handler.media.MediaNameConstants.PROP_CSS_CLASS;

#end
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

#if ($optionWcmioHandler == 'n')
import org.apache.commons.lang3.StringUtils;
#end
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
#if ($optionWcmioHandler == 'n')
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
#end
import org.apache.sling.models.annotations.injectorspecific.Self;
#if ($optionWcmioHandler == 'n')
import org.apache.sling.models.factory.ModelFactory;
#end

#if ($optionWcmioHandler == 'n')
import com.adobe.cq.wcm.core.components.models.Image;
#else
import io.wcm.handler.media.Media;
import io.wcm.handler.media.MediaHandler;

import ${package}.config.MediaFormats;
#end

/**
 * Model for the carousel component.
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class Carousel {

  static final String NN_SLIDES = "slides";

  private String id;
  private List<$imageClass> slideImages;

  @Self
  private SlingHttpServletRequest request;
#if ($optionWcmioHandler == 'n')
  @OSGiService
  private ModelFactory modelFactory;
#else
  @Self
  private MediaHandler mediaHandler;
#end

  @PostConstruct
  private void activate() {
    id = buildId();
    slideImages = buildSlideImages();
  }

  private String buildId() {
    // build unique id from component path.
    return "carousel-" + request.getResource().getPath().hashCode();
  }

#if ($optionWcmioHandler == 'n')
  @SuppressWarnings("null")
#end
  private List<$imageClass> buildSlideImages() {
    List<$imageClass> images = new ArrayList<>();

    // get configured media references and convert them to image urls
    Resource slides = request.getResource().getChild(NN_SLIDES);
    if (slides != null) {
      for (Resource slide : slides.getChildren()) {
#if ($optionWcmioHandler == 'n')
        // adapt the resource using the core image component
        // since it can only be adapted from a SlingHttpServletRequest we need to use the modelFactory to wrap the request
        Image img = modelFactory.getModelFromWrappedRequest(request, slide, Image.class);
        if (img != null && StringUtils.isNotBlank(img.getSrc())) {
          images.add(img);
        }
#else
        Media img = mediaHandler.get(slide)
            .mediaFormat(MediaFormats.CONTENT)
            .property(PROP_CSS_CLASS, "d-block w-100")
            .build();
        if (img.isValid()) {
          images.add(img);
        }
#end
      }
    }

    return images;
  }

  /**
   * @return Unique ID of this component that can be used in HTML markup
   */
  public String getId() {
    return id;
  }

  /**
   * @return List of images for each slide
   */
  public List<$imageClass> getSlideImages() {
    return this.slideImages;
  }

}
