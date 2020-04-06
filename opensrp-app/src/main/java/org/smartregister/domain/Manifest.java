package org.smartregister.domain;

/**
 * Created by cozej4 on 2020-04-06.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class Manifest {
    private String id;

    private String appVersion;

    private String formVersion;

    private String modelVersion;

    private boolean isNew;

    private boolean active;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(String formVersion) {
        this.formVersion = formVersion;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
