package org.smartregister.clientandeventmodel.populateform;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.BaseUnitTest;
import org.smartregister.clientandeventmodel.populateform.Model;

public class ModelTest extends BaseUnitTest {


    @Test
    public void testConstructorNotNull() {
        Model model = new Model("","","","");
        Assert.assertNotNull(model);
    }

}
