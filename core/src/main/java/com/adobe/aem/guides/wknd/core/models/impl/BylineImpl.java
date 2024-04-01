package com.adobe.aem.guides.wknd.core.models.impl;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.factory.ModelFactory;
import com.adobe.aem.guides.wknd.core.models.Byline;
import com.adobe.cq.wcm.core.components.models.Image;

@Model(
        adaptables = {SlingHttpServletRequest.class},
        adapters = {Byline.class},
        resourceType = {BylineImpl.RESOURCE_TYPE},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)

/*  
  The @Model annotation registers BylineImpl as a Sling Model when it is deployed to AEM.
  The adaptables parameter specifies that this model can be adapted by the request.
  The adapters parameter allows the implementation class to be registered under the Byline interface. This allows the HTL script to call the Sling Model via the interface (instead of the implementation directly). More details about adapters can be found here.
  The resourceType points to the Byline component resource type (created earlier) and helps to resolve the correct model if there are multiple implementations. More details about associating a model class with a resource type can be found here.
*/

public class BylineImpl implements Byline {

    protected static final String RESOURCE_TYPE = "wknd/components/byline";

    @Self
    private SlingHttpServletRequest request;

    @OSGiService
    private ModelFactory modelFactory;

    @ValueMapValue
    private String name;

    @ValueMapValue
    private List<String> occupations;

    private Image image;

    @PostConstruct
    private void init() {
        image = modelFactory.getModelFromWrappedRequest(request,
                                                        request.getResource(),
                                                        Image.class);
    }

    private Image getImage() {
      return image;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getOccupations() {
        if (occupations != null) {
          Collections.sort(occupations);
          return new ArrayList<String>(occupations);
        } else {
          return Collections.emptyList();
        }
    }

    @Override
    public boolean isEmpty() {
      final Image componentImage = getImage();

      if (StringUtils.isBlank(name)) {
        // Name is missing, but required
        return true;
      } else if (occupations == null || occupations.isEmpty()) {
        // At least one occupation is required
        return true;
      } else if (componentImage == null || StringUtils.isBlank(componentImage.getSrc())) {
        // A valid image is required
        return true;
      } else {
        // Everything is populated, so this component is not considered empty
        return false;
      }
    }
}
