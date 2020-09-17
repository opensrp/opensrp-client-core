package org.smartregister.view.contract;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 17-09-2020.
 */
public interface IBaseConfiguration {

    public String getLanguage();

    public void setLanguage(String language);
    public String getApplicationName();

    public void setApplicationName(String applicationName);

    public boolean isEnableJsonViews();

    public void setEnableJsonViews(boolean enableJsonViews);
}
