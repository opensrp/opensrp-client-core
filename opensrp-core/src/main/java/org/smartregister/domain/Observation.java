package org.smartregister.domain;

/**
 * Created by ndegwamartin on 25/03/2021.
 */
public class Observation {
    private String key;
    private String value;
    private TYPE type;

    public Observation(String key, String value, TYPE type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    // OpenMRS types expected by client core AllConstants.DATE , AllConstants.TEXT
    public enum TYPE {
        DATE,
        TEXT
    }
}
