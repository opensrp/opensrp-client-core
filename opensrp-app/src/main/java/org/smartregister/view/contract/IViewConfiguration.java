package org.smartregister.view.contract;

import java.util.List;
import java.util.Map;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 17-09-2020.
 */
public interface IViewConfiguration {


    String getIdentifier();

    void setIdentifier(String identifier);

    IBaseConfiguration getMetadata();

    void setMetadata(IBaseConfiguration metadata);

    List<IView> getViews();

    void setViews(List<IView> views);

    Map<String, String> getLabels();

    void setLabels(Map<String, String> labels);

    Map getJsonView();

    void setJsonView(Map jsonView);
}
