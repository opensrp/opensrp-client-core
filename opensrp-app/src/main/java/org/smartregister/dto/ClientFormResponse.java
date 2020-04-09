package org.smartregister.dto;

import org.json.JSONObject;

public class ClientFormDTO {
    private Long id;
    private JSONObject json;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }
}