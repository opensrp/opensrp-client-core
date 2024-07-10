package org.smartregister.domain.db.mock;

import org.joda.time.DateTime;
import org.smartregister.domain.db.BaseDataObject;

/**
 * Created by kaderchowdhury on 20/11/17.
 */

public class BaseDataObjectMock extends BaseDataObject {
    public BaseDataObjectMock() {
        super();
    }

    @Override
    public String getCreator() {
        return super.getCreator();
    }

    @Override
    public void setCreator(String creator) {
        super.setCreator(creator);
    }

    @Override
    public DateTime getDateCreated() {
        return super.getDateCreated();
    }

    @Override
    public void setDateCreated(DateTime dateCreated) {
        super.setDateCreated(dateCreated);
    }

    @Override
    public String getEditor() {
        return super.getEditor();
    }

    @Override
    public void setEditor(String editor) {
        super.setEditor(editor);
    }

    @Override
    public DateTime getDateEdited() {
        return super.getDateEdited();
    }

    @Override
    public void setDateEdited(DateTime dateEdited) {
        super.setDateEdited(dateEdited);
    }

    @Override
    public Boolean getVoided() {
        return super.getVoided();
    }

    @Override
    public void setVoided(Boolean voided) {
        super.setVoided(voided);
    }

    @Override
    public DateTime getDateVoided() {
        return super.getDateVoided();
    }

    @Override
    public void setDateVoided(DateTime dateVoided) {
        super.setDateVoided(dateVoided);
    }

    @Override
    public String getVoider() {
        return super.getVoider();
    }

    @Override
    public void setVoider(String voider) {
        super.setVoider(voider);
    }

    @Override
    public String getVoidReason() {
        return super.getVoidReason();
    }

    @Override
    public void setVoidReason(String voidReason) {
        super.setVoidReason(voidReason);
    }

    @Override
    public long getServerVersion() {
        return super.getServerVersion();
    }

    @Override
    public void setServerVersion(long serverVersion) {
        super.setServerVersion(serverVersion);
    }

    @Override
    public BaseDataObject withCreator(String creator) {
        return super.withCreator(creator);
    }

    @Override
    public BaseDataObject withDateCreated(DateTime dateCreated) {
        return super.withDateCreated(dateCreated);
    }

    @Override
    public BaseDataObject withEditor(String editor) {
        return super.withEditor(editor);
    }

    @Override
    public BaseDataObject withDateEdited(DateTime dateEdited) {
        return super.withDateEdited(dateEdited);
    }

    @Override
    public BaseDataObject withVoided(Boolean voided) {
        return super.withVoided(voided);
    }

    @Override
    public BaseDataObject withDateVoided(DateTime dateVoided) {
        return super.withDateVoided(dateVoided);
    }

    @Override
    public BaseDataObject withVoider(String voider) {
        return super.withVoider(voider);
    }

    @Override
    public BaseDataObject withVoidReason(String voidReason) {
        return super.withVoidReason(voidReason);
    }

    @Override
    public BaseDataObject withServerVersion(long serverVersion) {
        return super.withServerVersion(serverVersion);
    }
}
