package org.smartregister.domain.db;

import java.util.ArrayList;
import java.util.List;

public class Obs {

    private String fieldType;

    private String fieldDataType;

    private String fieldCode;

    private String parentCode;

    private List<Object> values;

    private String comments;

    private String formSubmissionField;

    private List<Object> humanReadableValues;

    public Obs() {
    }

    public Obs(String fieldType, String fieldDataType, String fieldCode, String parentCode,
               List<Object> values, String comments, String formSubmissionField, List<Object> humanReadableValues) {
        this.setFieldType(fieldType);
        this.fieldDataType = fieldDataType;
        this.fieldCode = fieldCode;
        this.parentCode = parentCode;
        this.values = values;
        this.comments = comments;
        this.formSubmissionField = formSubmissionField;
        this.humanReadableValues = humanReadableValues;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldDataType() {
        return fieldDataType;
    }

    public void setFieldDataType(String fieldDataType) {
        this.fieldDataType = fieldDataType;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public Object getValue() {
        if (values.size() > 1) {
            throw new RuntimeException("Multiset values can not be handled like single valued fields. Use function getValues");
        }
        if (values == null || values.size() == 0) {
            return null;
        }

        return values.get(0);
    }

    public void setValue(Object value) {
        addToValueList(value);
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }


    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFormSubmissionField() {
        return formSubmissionField;
    }

    public void setFormSubmissionField(String formSubmissionField) {
        this.formSubmissionField = formSubmissionField;
    }

    public Object getHumanReadableValue() {
        if (humanReadableValues.size() > 1) {
            throw new RuntimeException("Multiset values can not be handled like single valued fields. Use function getValues");
        }
        if (humanReadableValues == null || humanReadableValues.size() == 0) {
            return null;
        }

        return humanReadableValues.get(0);
    }

    public void setHumanReadableValue(Object humanReadableValue) {
        addToHumanReadableValuesList(humanReadableValue);
    }

    public List<Object> getHumanReadableValues() {
        return humanReadableValues;
    }

    public void setHumanReadableValues(List<Object> humanReadableValues) {
        this.humanReadableValues = humanReadableValues;
    }

    public Obs withFieldType(String fieldType) {
        this.fieldType = fieldType;
        return this;
    }

    public Obs withFieldDataType(String fieldDataType) {
        this.fieldDataType = fieldDataType;
        return this;
    }

    public Obs withFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
        return this;
    }

    public Obs withParentCode(String parentCode) {
        this.parentCode = parentCode;
        return this;
    }

    public Obs withValue(Object value) {
        return addToValueList(value);
    }

    public Obs withValues(List<Object> values) {
        this.values = values;
        return this;
    }

    public Obs addToValueList(Object value) {
        if (values == null) {
            values = new ArrayList<Object>();
        }
        values.add(value);
        return this;
    }

    public Obs addToHumanReadableValuesList(Object humanReadableValue) {
        if (humanReadableValues == null) {
            humanReadableValues = new ArrayList<Object>();
        }
        humanReadableValues.add(humanReadableValue);
        return this;
    }

    public Obs withComments(String comments) {
        this.comments = comments;
        return this;
    }

    public Obs withFormSubmissionField(String formSubmissionField) {
        this.formSubmissionField = formSubmissionField;
        return this;
    }

    public Obs withHumanReadableValue(Object humanReadableValue) {
        return addToHumanReadableValuesList(humanReadableValue);
    }

    public Obs withHumanReadableValues(List<Object> humanReadableValues) {
        this.humanReadableValues = humanReadableValues;
        return this;
    }
}
