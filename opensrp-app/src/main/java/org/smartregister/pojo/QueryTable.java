package org.smartregister.pojo;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 2020-09-23
 */

public class QueryTable {

    private String tableName;
    private String[] colNames;
    private String mainCondition = "";

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String[] getColNames() {
        return colNames;
    }

    public void setColNames(String[] colNames) {
        this.colNames = colNames;
    }

    public String getMainCondition() {
        return mainCondition;
    }

    public void setMainCondition(String mainCondition) {
        this.mainCondition = mainCondition;
    }
}
