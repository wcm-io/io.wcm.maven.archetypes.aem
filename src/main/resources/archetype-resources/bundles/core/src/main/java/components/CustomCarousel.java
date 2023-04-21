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
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

#if ($optionWcmioHandler == 'n')
import org.apache.commons.lang3.StringUtils;
#end
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
#if ($optionWcmioHandler == 'n')
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
#end
import org.apache.sling.models.annotations.injectorspecific.Self;
#if ($optionWcmioHandler == 'n')
import org.apache.sling.models.factory.ModelFactory;
#end
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
#if ($optionWcmioHandler == 'n')
import com.adobe.cq.wcm.core.components.models.Image;
#else

import io.wcm.handler.media.Media;
import io.wcm.handler.media.MediaHandler;
#end

/**
 * Model for the custom carousel component.
 * <p>
 * Please note: There is already is a pre-built "Carousel" Core Component which does basically the same
 * as this component with a much more sophisticated edit mode support. Use it, instead of this demo component!
 * This demo component is only an example for a custom standalone component.
 * </p>
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = { CustomCarousel.class, ComponentExporter.class },
    resourceType = CustomCarousel.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class CustomCarousel implements ComponentExporter {

  static final String RESOURCE_TYPE = "${projectName}/core/components/content/customcarousel";
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
    return "customcarousel-" + request.getResource().getPath().hashCode();
  }

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
    return Collections.unmodifiableList(this.slideImages);
  }

  @Override
  public @NotNull String getExportedType() {
    return RESOURCE_TYPE;
  }

}
