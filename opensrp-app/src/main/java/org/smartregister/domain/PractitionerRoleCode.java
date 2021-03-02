package org.smartregister.domain;

import java.io.Serializable;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 01-03-2021.
 */

public class PractitionerRoleCode implements Serializable {

    private static final long serialVersionUID = 5814439241291810987L;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}