package org.smartregister.domain.db;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.db.mock.BaseDataObjectMock;

/**
 * Created by kaderchowdhury on 20/11/17.
 */

public class BaseDataObjectTest extends BaseUnitTest {

    private BaseDataObjectMock baseDataObject;

    @Before
    public void setUp() {
        baseDataObject = new BaseDataObjectMock();
    }

    public String getCreator() {
        return baseDataObject.getCreator();
    }

    @Test
    public void assertSetCreator() {
        String creator = "creator;";
        baseDataObject.setCreator(creator);
        Assert.assertEquals(getCreator(), creator);
    }


    public DateTime getDateCreated() {
        return baseDataObject.getDateCreated();
    }

    @Test
    public void assertSetDateCreated() {
        DateTime dateCreated = new DateTime(0l);
        baseDataObject.setDateCreated(dateCreated);
        Assert.assertEquals(getDateCreated(), dateCreated);
    }


    public String getEditor() {
        return baseDataObject.getEditor();
    }

    @Test
    public void assertSetEditor() {
        String editor = "editor";
        baseDataObject.setEditor(editor);
        Assert.assertEquals(getEditor(), editor);
    }


    public DateTime getDateEdited() {
        return baseDataObject.getDateEdited();
    }

    @Test
    public void setDateEdited() {
        DateTime dateEdited = new DateTime(0l);
        baseDataObject.setDateEdited(dateEdited);
        Assert.assertEquals(getDateEdited(), dateEdited);
    }


    public Boolean getVoided() {
        return baseDataObject.getVoided();
    }

    @Test
    public void setVoided() {
        Boolean voided = Boolean.TRUE;
        baseDataObject.setVoided(voided);
        Assert.assertEquals(getVoided(), voided);
    }


    public DateTime getDateVoided() {
        return baseDataObject.getDateVoided();
    }

    @Test
    public void setDateVoided() {
        DateTime dateVoided = new DateTime(0l);
        baseDataObject.setDateVoided(dateVoided);
        Assert.assertEquals(getDateVoided(), dateVoided);
    }


    public String getVoider() {
        return baseDataObject.getVoider();
    }

    @Test
    public void assertSetVoider() {
        String voider = "voder";
        baseDataObject.setVoider(voider);
        Assert.assertEquals(getVoider(), voider);
    }


    public String getVoidReason() {
        return baseDataObject.getVoidReason();
    }

    @Test
    public void assertsetVoidReason() {
        String voidReason = "voidreadsom";
        baseDataObject.setVoidReason(voidReason);
        Assert.assertEquals(getVoidReason(), voidReason);
    }


    public long getServerVersion() {
        return baseDataObject.getServerVersion();
    }

    @Test
    public void assertSetServerVersion() {
        long serverVersion = 0l;
        baseDataObject.setServerVersion(serverVersion);
        Assert.assertEquals(getServerVersion(), serverVersion);
    }


    public BaseDataObject withCreator(String creator) {
        return baseDataObject.withCreator(creator);
    }


    public BaseDataObject withDateCreated(DateTime dateCreated) {
        return baseDataObject.withDateCreated(dateCreated);
    }


    public BaseDataObject withEditor(String editor) {
        return baseDataObject.withEditor(editor);
    }


    public BaseDataObject withDateEdited(DateTime dateEdited) {
        return baseDataObject.withDateEdited(dateEdited);
    }


    public BaseDataObject withVoided(Boolean voided) {
        return baseDataObject.withVoided(voided);
    }


    public BaseDataObject withDateVoided(DateTime dateVoided) {
        return baseDataObject.withDateVoided(dateVoided);
    }


    public BaseDataObject withVoider(String voider) {
        return baseDataObject.withVoider(voider);
    }


    public BaseDataObject withVoidReason(String voidReason) {
        return baseDataObject.withVoidReason(voidReason);
    }


    public BaseDataObject withServerVersion(long serverVersion) {
        return baseDataObject.withServerVersion(serverVersion);
    }

    @Test
    public void assertBaseObjectNotNull() {
        Assert.assertNotNull(withCreator(""));
        Assert.assertNotNull(withDateCreated(new DateTime(0l)));
        Assert.assertNotNull(withDateEdited(new DateTime(0l)));
        Assert.assertNotNull(withDateVoided(new DateTime(0l)));
        Assert.assertNotNull(withEditor(""));
        Assert.assertNotNull(withServerVersion(0l));
        Assert.assertNotNull(withVoider(""));
        Assert.assertNotNull(withVoided(Boolean.TRUE));
        Assert.assertNotNull(withVoidReason(""));
    }
}
