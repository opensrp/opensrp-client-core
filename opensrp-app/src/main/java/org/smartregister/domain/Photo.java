package org.smartregister.domain;

import java.io.Serializable;

/**
 * Created by keyman on 20/02/2017.
 */
public class Photo implements Serializable {

    private String filePath;
    private int resourceId;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }
}
