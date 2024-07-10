package org.smartregister.domain.db;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.db.mock.ObsMock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaderchowdhury on 21/11/17.
 */

public class ObsTest extends BaseUnitTest {

    private String fieldType = "filedType";

    private String fieldDataType = "dataType";

    private String fieldCode = "fieldCode";

    private String parentCode = "parentCode";

    private List<Object> values = new ArrayList<>();

    private String comments = "good job";

    private String formSubmissionField = "FSField";

    private List<Object> humanReadableValues = new ArrayList<>();
    ObsMock ob;

    @Before
    public void setUp() {
        ob = new ObsMock(fieldType, fieldDataType, fieldCode, parentCode, values, comments, formSubmissionField, humanReadableValues);
    }

    @Test
    public void assertConstructorNotNull() {
        Assert.assertNotNull(ob);
    }


    public String getFieldType() {
        return ob.getFieldType();
    }

    @Test
    public void setFieldType() {
        ob.setFieldType(fieldType);
        Assert.assertEquals(getFieldType(), fieldType);
    }


    public String getFieldDataType() {
        return ob.getFieldDataType();
    }

    @Test
    public void setFieldDataType() {
        ob.setFieldDataType(fieldDataType);
        Assert.assertEquals(getFieldDataType(), fieldDataType);
    }


    public String getFieldCode() {
        return ob.getFieldCode();
    }

    @Test
    public void setFieldCode() {
        ob.setFieldCode(fieldCode);
        Assert.assertEquals(getFieldCode(), fieldCode);
    }


    public String getParentCode() {
        return ob.getParentCode();
    }

    @Test
    public void setParentCode() {
        ob.setParentCode(parentCode);
        Assert.assertEquals(getParentCode(), parentCode);
    }


    public Object getValue() {
        return ob.getValue();
    }

    @Test
    public void setValue() {
        Object value = new Object();
        ob.setValue(value);
        Assert.assertEquals(getValue(), value);
    }


    public List<Object> getValues() {
        return ob.getValues();
    }

    @Test
    public void setValues() {
        ob.setValues(values);
        Assert.assertEquals(getValues(), values);
    }


    public String getComments() {
        return ob.getComments();
    }

    @Test
    public void setComments() {
        ob.setComments(comments);
        Assert.assertEquals(getComments(), comments);
    }


    public String getFormSubmissionField() {
        return ob.getFormSubmissionField();
    }

    @Test
    public void setFormSubmissionField() {
        ob.setFormSubmissionField(formSubmissionField);
        Assert.assertEquals(getFormSubmissionField(), formSubmissionField);
    }

    @Test
    public void withFieldType() {
        Assert.assertNotNull(ob.withFieldType(fieldType));
    }

    @Test
    public void withFieldDataType() {
        Assert.assertNotNull(ob.withFieldDataType(fieldDataType));
    }

    @Test
    public void withFieldCode() {
        Assert.assertNotNull(ob.withFieldCode(fieldCode));
    }

    @Test
    public void withParentCode() {
        Assert.assertNotNull(ob.withParentCode(parentCode));
    }

    @Test
    public void withValue() {
        Object value = new Object();
        Assert.assertNotNull(ob.withValue(value));
    }

    @Test
    public void withValues() {
        Assert.assertNotNull(ob.withValues(values));
    }

    @Test
    public void addToValueList() {
        Object value = new Object();
        Assert.assertNotNull(ob.addToValueList(value));
    }

    @Test
    public void withComments() {
        Assert.assertNotNull(ob.withComments(comments));
    }

    @Test
    public void withFormSubmissionField() {
        Assert.assertNotNull(ob.withFormSubmissionField(formSubmissionField));
    }

}
