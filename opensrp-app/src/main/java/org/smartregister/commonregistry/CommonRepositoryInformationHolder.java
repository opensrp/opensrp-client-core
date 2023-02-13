package org.smartregister.commonregistry;

/**
 * Created by Raihan Ahmed on 3/16/15.
 */
public class CommonRepositoryInformationHolder {
    private String bindtypename;
    private String[] columnNames;

    public CommonRepositoryInformationHolder(String bindtypename, String[] columnNames) {
        this.bindtypename = bindtypename;
        this.columnNames = columnNames;
    }

    public String getBindtypename() {
        return bindtypename;
    }

    public void setBindtypename(String bindtypename) {
        this.bindtypename = bindtypename;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }
}
