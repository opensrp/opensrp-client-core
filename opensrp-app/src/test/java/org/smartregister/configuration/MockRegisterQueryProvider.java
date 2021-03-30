package org.smartregister.configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.pojo.InnerJoinObject;
import org.smartregister.pojo.QueryTable;

public class MockRegisterQueryProvider extends ModuleRegisterQueryProviderContract{
    @NonNull
    @Override
    public String getObjectIdsQuery(@Nullable String filters, @Nullable String mainCondition) {
        return "";
    }

    @NonNull
    @Override
    public String[] countExecuteQueries(@Nullable String filters, @Nullable String mainCondition) {
        return new String[0];
    }

    @NonNull
    @Override
    public String mainSelectWhereIDsIn() {
        return "";
    }

    @Override
    public String mainSelectWhereIdsIn(@NonNull InnerJoinObject[] tableColsInnerJoins, @NonNull QueryTable[] tableCols) {
        return null;
    }

    @Override
    public String mainSelect(@NonNull InnerJoinObject[] tableColsInnerJoins, @NonNull QueryTable[] tableCols) {
        return null;
    }
}
