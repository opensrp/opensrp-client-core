package org.smartregister.view.contract;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 17-09-2020.
 */
public interface IBaseConfiguration {

    String getLanguage();

    void setLanguage(String language);

    String getApplicationName();

    void setApplicationName(String applicationName);

    boolean isEnableJsonViews();

    void setEnableJsonViews(boolean enableJsonViews);
}
