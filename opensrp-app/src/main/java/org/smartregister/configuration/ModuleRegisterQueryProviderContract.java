package org.smartregister.configuration;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.pojo.InnerJoinObject;
import org.smartregister.pojo.QueryTable;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 23-09-2020
 */

public abstract class ModuleRegisterQueryProviderContract {

    /**
     * Return query to be used to select object_ids from the search table so that these objects_ids
     * are later used to retrieve the actual rows from the normal(non-FTS) table
     *
     * @param filters This is the search phrase entered in the search box
     * @return
     */
    @NonNull
    public abstract String getObjectIdsQuery(@Nullable String filters, @Nullable String mainCondition);

    /**
     * Return query(s) to be used to perform the total count of register clients eg. If OPD combines records
     * in multiple tables then you can provide multiple queries with the result having a single row+column
     * and the counts will be summed up. Kindly try to use the search tables wherever possible.
     *
     * @param filters This is the search phrase entered in the search box
     * @return
     */
    @NonNull
    public abstract String[] countExecuteQueries(@Nullable String filters, @Nullable String mainCondition);

    /**
     * Return query to be used to retrieve the client details. This query should have a "WHERE base_entity_id IN (%s)" clause where
     * the comma-separated  base-entity-ids for the clients will be inserted into the query and later
     * executed
     *
     * @return
     */
    @NonNull
    public abstract String mainSelectWhereIDsIn();

    /**
     * Generates a query that is returned in {@link #mainSelectWhereIDsIn()} in case you are using
     * an inner join and UNION from another table
     *
     * @param tableColsInnerJoins
     * @param tableCols
     * @return
     */
    public abstract String mainSelectWhereIdsIn(@NonNull InnerJoinObject[] tableColsInnerJoins, @NonNull QueryTable[] tableCols);

    public abstract String mainSelect(@NonNull InnerJoinObject[] tableColsInnerJoins, @NonNull QueryTable[] tableCols);
}
