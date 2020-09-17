package org.smartregister.view.contract;

import java.util.Map;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 17-09-2020.
 */
public interface IView {


    void setParent(String parent);

    String getIdentifier();

    void setIdentifier(String identifier);

    String getType();

    void setType(String type);

    String getOrientation();

    void setOrientation(String orientation);

    boolean isVisible();

    void setVisible(boolean visible);

    String getLabel();

    void setLabel(String label);

    IResidence getResidence();

    void setResidence(IResidence residence);

    Map<String, Object> getMetadata();

    void setMetadata(Map<String, Object> metadata);
}
