package org.smartregister.domain;

/**
 * Created by cozej4 on 2020-04-06.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class Manifest {
    private String id;

    private String app_version;

    private String form_version;

    private String model_version;

    private boolean is_new;

    private boolean active;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getForm_version() {
        return form_version;
    }

    public void setForm_version(String form_version) {
        this.form_version = form_version;
    }

    public String getModel_version() {
        return model_version;
    }

    public void setModel_version(String model_version) {
        this.model_version = model_version;
    }

    public boolean isIs_new() {
        return is_new;
    }

    public void setIs_new(boolean is_new) {
        this.is_new = is_new;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
