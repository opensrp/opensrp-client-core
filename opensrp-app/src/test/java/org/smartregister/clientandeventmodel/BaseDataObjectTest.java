package org.smartregister.clientandeventmodel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.smartregister.BaseUnitTest;
import org.smartregister.clientandeventmodel.mock.BaseDataObjectMock;

import java.util.Date;

/**
 * Created by kaderchowdhury on 22/11/17.
 */

public class BaseDataObjectTest extends BaseUnitTest {

    BaseDataObjectMock baseDataObject;
    User user = new User();
    Date date = new Date(0l);
    @Before
    public void setUp() {
        baseDataObject = new BaseDataObjectMock();
    }
    
    public User getCreator() {
        return baseDataObject.getCreator();
    }

    @Test
    public void setCreator() {
        baseDataObject.setCreator(user);
        Assert.assertEquals(getCreator(),user);
    }

    
    public Date getDateCreated() {
        return baseDataObject.getDateCreated();
    }

    @Test
    public void setDateCreated() {
        baseDataObject.setDateCreated(date);
        Assert.assertEquals(getDateCreated(),date);
    }

    
    public User getEditor() {
        return baseDataObject.getEditor();
    }

    @Test
    public void setEditor() {
        baseDataObject.setEditor(user);
        Assert.assertEquals(getEditor(),user);
    }

    
    public Date getDateEdited() {
        return baseDataObject.getDateEdited();
    }

    @Test
    public void setDateEdited() {
        baseDataObject.setDateEdited(date);
        Assert.assertEquals(getDateEdited(),date);
    }

    
    public Boolean getVoided() {
        return baseDataObject.getVoided();
    }

    @Test
    public void setVoided() {
        baseDataObject.setVoided(Boolean.TRUE);
        Assert.assertEquals(getVoided(),Boolean.TRUE);
    }

    
    public Date getDateVoided() {
        return baseDataObject.getDateVoided();
    }

    @Test
    public void setDateVoided() {
        baseDataObject.setDateVoided(date);
        Assert.assertEquals(getDateVoided(),date);
    }

    
    public User getVoider() {
        return baseDataObject.getVoider();
    }

    @Test
    public void setVoider() {
        baseDataObject.setVoider(user);
        Assert.assertEquals(getVoider(),user);
    }

    
    public String getVoidReason() {
        return baseDataObject.getVoidReason();
    }

    @Test
    public void setVoidReason() {
        baseDataObject.setVoidReason("reason");
        Assert.assertEquals(getVoidReason(),"reason");
    }

    @Test
    public void withCreator() {
        Assert.assertNotNull(baseDataObject.withCreator(user));
    }

    @Test
    public void withDateCreated() {
        Assert.assertNotNull(baseDataObject.withDateCreated(date));
    }

    @Test
    public void withEditor() {
        Assert.assertNotNull(baseDataObject.withEditor(user));
    }

    @Test
    public void withDateEdited() {
        Assert.assertNotNull(baseDataObject.withDateEdited(date));
    }

    @Test
    public void withVoided() {
        Assert.assertNotNull(baseDataObject.withVoided(Boolean.TRUE));
    }

    @Test
    public void withDateVoided() {
        Assert.assertNotNull(baseDataObject.withDateVoided(date));
    }

    @Test
    public void withVoider() {
        Assert.assertNotNull(baseDataObject.withVoider(user));
    }

    @Test
    public void withVoidReason() {
        Assert.assertNotNull(baseDataObject.withVoidReason(""));
    }

    @Test
    public void asserttoString() {
        Assert.assertNotNull(baseDataObject.toString());
    }
}
