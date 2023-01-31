package org.smartregister.clientandeventmodel.populateform;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.BaseUnitTest;
import org.smartregister.clientandeventmodel.populateform.Model;

public class ModelTest extends BaseUnitTest {

    private Model model;

    @Before
    public void setUp() {
        model = new Model("tag","openMRSEntity","openMRSEntityId","OpenMRSEntityParent");
    }

    @Test
    public void testConstructorNotNull() {
        Assert.assertNotNull(model);
    }

    @Test
    public void testFieldMembers() {
        Assert.assertEquals("tag", model.getTag());
        Assert.assertEquals("openMRSEntity", model.getOpenMRSEntity());
        Assert.assertEquals("openMRSEntityId", model.getOpenMRSEntityId());
        Assert.assertEquals("OpenMRSEntityParent", model.getOpenMRSEntityParent());
    }

}