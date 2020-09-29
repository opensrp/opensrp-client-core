package org.smartregister.sample.configuration;

import android.support.annotation.NonNull;

import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.pojo.InnerJoinObject;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-16
 */

public class OpdRegisterQueryBuilder extends SmartRegisterQueryBuilder {

    public String SelectInitiateMainTableCounts(String tableName) {
        String selectQuery = "SELECT COUNT(*) as sub_count";
        selectQuery = selectQuery + " FROM " + tableName;

        setSelectquery(selectQuery);
        return selectQuery;
    }

    public String SelectInitiateMainTable(@NonNull InnerJoinObject tableColsInnerJoin) {
        String selectQuery = String.format("Select %s.id as _id", tableColsInnerJoin.getFirstTable().getTableName());

        String[] columns = tableColsInnerJoin.getFirstTable().getColNames();
        String tableName = tableColsInnerJoin.getFirstTable().getTableName();
        for (String column : columns) {
            if (!column.contains("'")) {
                selectQuery += String.format(", %s.%s", tableName, column);
            } else {
                selectQuery += String.format(", %s", column);
            }
        }

        columns = tableColsInnerJoin.getSecondTable().getColNames();
        tableName = tableColsInnerJoin.getSecondTable().getTableName();
        for (String column : columns) {
            if (!column.contains("'")) {
                selectQuery += String.format(", %s.%s", tableName, column);
            } else {
                selectQuery += String.format(", %s", column);
            }
        }

        selectQuery += String.format(" FROM %s INNER JOIN %s ON %s", tableColsInnerJoin.getFirstTable().getTableName()
                , tableColsInnerJoin.getSecondTable().getTableName(), tableColsInnerJoin.getInnerJoinClause());

        setSelectquery(selectQuery);
        return selectQuery;
    }
}
