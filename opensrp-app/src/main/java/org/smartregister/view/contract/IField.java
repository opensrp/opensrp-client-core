package org.smartregister.view.contract;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 17-09-2020.
 */
public interface IField {


    String getDisplayName();

    void setDisplayName(String displayName);

    String getDbAlias();

    void setDbAlias(String dbAlias);
}
