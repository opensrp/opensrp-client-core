package org.smartregister.sample.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.smartregister.configuration.ModuleRegisterQueryProviderContract;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.pojo.InnerJoinObject;
import org.smartregister.pojo.QueryTable;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 29-09-2020.
 */
public class RegisterQueryProvider extends ModuleRegisterQueryProviderContract {

    @NonNull
    @Override
    public String getObjectIdsQuery(@Nullable String filters, @Nullable String mainCondition) {
        if (TextUtils.isEmpty(filters)) {
            return "SELECT object_id FROM " +
                    "(SELECT object_id, last_interacted_with FROM ec_client_search WHERE date_removed IS NULL) " +
                    "ORDER BY last_interacted_with DESC";
        } else {
            String sql = "SELECT object_id FROM " +
                    "(SELECT object_id, last_interacted_with FROM ec_client_search WHERE date_removed IS NULL AND phrase MATCH '%s*' ) " +
                    "ORDER BY last_interacted_with DESC";
            sql = sql.replace("%s", filters);
            return sql;
        }
    }

    @NonNull
    @Override
    public String[] countExecuteQueries(@Nullable String filters, @Nullable String mainCondition) {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();

        return new String[] {
                sqb.countQueryFts("ec_client", null, null, filters)
        };
    }

    @NonNull
    @Override
    public String mainSelectWhereIDsIn() {
        QueryTable childTableCol = new QueryTable();
        childTableCol.setTableName("ec_child");
        childTableCol.setColNames(new String[]{
                "first_name",
                "last_name",
                "middle_name",
                "gender",
                "dob",
                "home_address",
                "'Child' AS register_type",
                "relational_id AS relationalid"
        });


        QueryTable clientTableCol = new QueryTable();
        clientTableCol.setTableName("ec_client");
        clientTableCol.setColNames(new String[]{
                "first_name",
                "last_name",
                "'' AS middle_name",
                "gender",
                "dob",
                "'' AS home_address",
                "'OPD' AS register_type",
                "NULL AS mother_first_name",
                "NULL AS mother_last_name",
                "NULL AS mother_middle_name",
                "relationalid"
        });

        InnerJoinObject[] tablesWithInnerJoins = new InnerJoinObject[1];
        InnerJoinObject tableColsInnerJoin = new InnerJoinObject();
        tableColsInnerJoin.setFirstTable(childTableCol);

        QueryTable innerJoinMotherTable = new QueryTable();
        innerJoinMotherTable.setTableName("ec_mother");
        innerJoinMotherTable.setColNames(new String[]{
                "first_name AS mother_first_name",
                "last_name AS mother_last_name",
                "middle_name AS mother_middle_name"
        });
        tableColsInnerJoin.innerJoinOn("ec_child.relational_id = ec_mother.base_entity_id");
        tableColsInnerJoin.innerJoinTable(innerJoinMotherTable);
        tablesWithInnerJoins[0] = tableColsInnerJoin;

        return "SELECT id AS _id, * FROM ec_client WHERE id IN (%s)";
    }

    @Override
    public String mainSelectWhereIdsIn(@NonNull InnerJoinObject[] tableColsInnerJoins, @NonNull QueryTable[] tableCols) {
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < tableColsInnerJoins.length; i++) {
            InnerJoinObject tableColInnerJoin = tableColsInnerJoins[i];

            OpdRegisterQueryBuilder countQueryBuilder = new OpdRegisterQueryBuilder();
            countQueryBuilder.SelectInitiateMainTable(tableColInnerJoin);
            countQueryBuilder.mainCondition(tableColInnerJoin.getMainCondition());

            String idCol = "_id";
            if (countQueryBuilder.getSelectquery().contains("JOIN")) {
                idCol = tableColInnerJoin.getFirstTable().getTableName() + ".id";
            }

            if (countQueryBuilder.getSelectquery().contains("WHERE")) {
                countQueryBuilder.addCondition(" AND ");

            } else {
                countQueryBuilder.addCondition(" WHERE ");
            }
            countQueryBuilder.addCondition("%s IN (%s)");
            countQueryBuilder.setSelectquery(countQueryBuilder.getSelectquery().replaceFirst("%s", idCol));

            if (i != 0) {
                query.append(" UNION ALL ");
            }

            query.append(countQueryBuilder.getSelectquery());
        }


        for (int i = 0; i < tableCols.length; i++) {
            QueryTable tableCol = tableCols[i];

            OpdRegisterQueryBuilder countQueryBuilder = new OpdRegisterQueryBuilder();
            //countQueryBuilder.SelectInitiateMainTable(tableCol.getTableName(), tableCol.getColNames());
            countQueryBuilder.mainCondition(tableCol.getMainCondition());

            String idCol = "_id";
            if (countQueryBuilder.getSelectquery().contains("JOIN")) {
                idCol = tableCol.getTableName() + ".id";
            }

            if (countQueryBuilder.getSelectquery().contains("WHERE")) {
                countQueryBuilder.addCondition(" AND ");

            } else {
                countQueryBuilder.addCondition(" WHERE ");
            }
            countQueryBuilder.addCondition("%s IN (%s)");
            countQueryBuilder.setSelectquery(countQueryBuilder.getSelectquery().replaceFirst("%s", idCol));

            if (query.length() != 0 || i != 0) {
                query.append(" UNION ALL ");
            }

            query.append(countQueryBuilder.getSelectquery());
        }

        return query.toString();
    }

    @Override
    public String mainSelect(@NonNull InnerJoinObject[] tableColsInnerJoins, @NonNull QueryTable[] tableCols) {
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < tableColsInnerJoins.length; i++) {
            InnerJoinObject tableColInnerJoin = tableColsInnerJoins[i];

            OpdRegisterQueryBuilder countQueryBuilder = new OpdRegisterQueryBuilder();
            countQueryBuilder.SelectInitiateMainTable(tableColInnerJoin);
            countQueryBuilder.mainCondition(tableColInnerJoin.getMainCondition());

            if (i != 0) {
                query.append(" UNION ALL ");
            }

            query.append(countQueryBuilder.getSelectquery());
        }

        for (int i = 0; i < tableCols.length; i++) {
            QueryTable tableCol = tableCols[i];

            OpdRegisterQueryBuilder countQueryBuilder = new OpdRegisterQueryBuilder();
            //countQueryBuilder.SelectInitiateMainTable(tableCol.getTableName(), tableCol.getColNames());
            countQueryBuilder.mainCondition(tableCol.getMainCondition());

            if (query.length() != 0 || i != 0) {
                query.append(" UNION ALL ");
            }

            query.append(countQueryBuilder.getSelectquery());
        }

        return query.toString();
    }
}
