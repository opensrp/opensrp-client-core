package org.smartregister.domain.form;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.BaseUnitTest;

public class FormFieldTest extends BaseUnitTest {

    @Test
    public void testEqualsShouldReturnTrueForIdenticalObjects() {
        FormField formField1 = new FormField("name", "value", "source");
        FormField formField2 = new FormField("name", "value", "source");
        Assert.assertTrue(formField1.equals(formField2));
    }

    @Test
    public void testHashCodeNotNull() {
        FormField formField = new FormField("name", "value", "source");
        Assert.assertNotNull(formField.hashCode());
    }

    @Test
    public void testToStringShouldReturnCorrectData() {
        FormField formField = new FormField("name", "value", "source");
        Assert.assertNotNull(formField.toString());
    }

    @Test
    public void testSettersShouldSetCorrectValue() {
        FormField formField = new FormField("n", "v", "s");
        formField.setName("name");
        formField.setValue("value");
        formField.setSource("source");
        Assert.assertEquals("name", formField.getName());
        Assert.assertEquals("value", formField.value());
        Assert.assertEquals("source", formField.source());

    }

}
