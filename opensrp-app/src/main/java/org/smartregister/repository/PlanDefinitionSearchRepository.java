package org.smartregister.repository;

import android.content.ContentValues;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.domain.PlanDefinition;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by samuelgithengi on 5/7/19.
 */
public class PlanDefinitionSearchRepository extends BaseRepository {

    private static final String TAG = "PlanDefinitionSearch";

    private static final String ID = "_id";
    private static final String PLAN_ID = "plan_id";
    private static final String JURISDICTION_ID = "jurisdiction_id";
    private static final String NAME = "name";
    private static final String STATUS = "status";
    private static final String START = "start";
    private static final String END = "start";

    private static final String ACTIVE = "active";

    private static final String PLAN_DEFINITION_SEARCH_TABLE = "plan_definition_search";

    private PlanDefinitionRepository planDefinitionRepository;

    private static final String CREATE_PLAN_DEFINITION_TABLE =
            "CREATE TABLE " + PLAN_DEFINITION_SEARCH_TABLE + " (" +
                    PLAN_ID + " VARCHAR NOT ," +
                    JURISDICTION_ID + " VARCHAR NOT NULL," +
                    NAME + " VARCHAR NOT NULL," +
                    STATUS + " VARCHAR NOT NULL," +
                    START + " INTEGER NOT NULL," +
                    END + " INTEGER NOT NULL ) ";

    private static final String CREATE_PLAN_DEFINITION_PK = " ALTER TABLE "
            + PLAN_DEFINITION_SEARCH_TABLE +
            "ADD CONSTRAINT PK_PLAN_ID PRIMARY KEY (" + PLAN_ID + "," + JURISDICTION_ID + ")";

    public PlanDefinitionSearchRepository(Repository repository) {
        super(repository);
    }


    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_PLAN_DEFINITION_TABLE);
        database.execSQL(CREATE_PLAN_DEFINITION_PK);
    }


    public void addOrUpdate(PlanDefinition planDefinition, String jurisdiction) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, planDefinition.getIdentifier());
        contentValues.put(JURISDICTION_ID, jurisdiction);
        contentValues.put(NAME, planDefinition.getName());
        contentValues.put(STATUS, planDefinition.getStatus());
        contentValues.put(START, planDefinition.getEffectivePeriod().getStart().toDate().getTime());
        contentValues.put(END, planDefinition.getEffectivePeriod().getEnd().toDate().getTime());
        contentValues.put(JURISDICTION_ID, jurisdiction);
    }

    public Set<PlanDefinition> findByJurisdiction(String jurisdiction) {

        Set<String> planIds = new HashSet<>();
        Cursor cursor = null;
        try {
            String query = String.format("SELECT %s FROM %s " +
                    "WHERE %s=? AND %s=? AND ? BETWEEN %s AND %s ", PLAN_ID, PLAN_DEFINITION_SEARCH_TABLE, JURISDICTION_ID, STATUS, START, END);
            cursor = getReadableDatabase().rawQuery(query, new String[]{jurisdiction, ACTIVE});
            if (cursor.moveToNext()) {
                planIds.add(cursor.getString(0));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return getPlanDefinitionRepository().findPlanDefinitionByIds(planIds);
    }

    public void setPlanDefinitionRepository(PlanDefinitionRepository planDefinitionRepository) {
        this.planDefinitionRepository = planDefinitionRepository;

    }

    private PlanDefinitionRepository getPlanDefinitionRepository() {
        if (planDefinitionRepository == null) {
            planDefinitionRepository = new PlanDefinitionRepository(getRepository());
        }
        return planDefinitionRepository;
    }
}
