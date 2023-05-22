package org.smartregister.clientandeventmodel;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.BaseUnitTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubFormDataTest  extends BaseUnitTest {

    @Test
    public void testConstructorNotNull1() {
        List<Map<String, String>> instanceList = new ArrayList<>();
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("key", "value");
        instanceList.add(dataMap);
        Assert.assertNotNull(new SubFormData("name", instanceList));

    }

    @Test
    public void testFieldsNotNull() {
        List<FormField> formFields = new ArrayList<>();
        FormField field = new FormField();
        formFields.add(field);
        SubFormData data = new SubFormData();
        data.setFields(formFields);
        Assert.assertNotNull(data);
        Assert.assertNotNull(data.fields());

    }
}
