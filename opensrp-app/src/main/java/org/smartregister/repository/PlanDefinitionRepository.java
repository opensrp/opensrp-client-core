package org.smartregister.repository;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.DateTypeConverter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by samuelgithengi on 5/7/19.
 */
public class PlanDefinitionRepository extends BaseRepository {

    public static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd HH:mm:ss.SSSZ"))
            .registerTypeAdapter(LocalDate.class, new DateTypeConverter())
            .disableHtmlEscaping()
            .create();

    protected static final String ID = "_id";
    protected static final String JSON = "json";

    private static final String PLAN_DEFINITION_TABLE = "plan_definition";
    private static final String TAG = PlanDefinitionRepository.class.getName();

    private PlanDefinitionSearchRepository searchRepository;

    private static final String CREATE_PLAN_DEFINITION_TABLE =
            "CREATE TABLE " + PLAN_DEFINITION_TABLE + " (" +
                    ID + " VARCHAR NOT NULL PRIMARY KEY," +
                    JSON + " VARCHAR NOT NULL)";


    public PlanDefinitionRepository(Repository repository) {
        super(repository);
        searchRepository = new PlanDefinitionSearchRepository(repository);
        searchRepository.setPlanDefinitionRepository(this);
    }

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_PLAN_DEFINITION_TABLE);
    }

    public void addOrUpdate(PlanDefinition planDefinition) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, planDefinition.getIdentifier());
        contentValues.put(JSON, gson.toJson(planDefinition));
        getWritableDatabase().replace(PLAN_DEFINITION_TABLE, null, contentValues);
        for (PlanDefinition.Jurisdiction jurisdiction : planDefinition.getJurisdiction()) {
            searchRepository.addOrUpdate(planDefinition, jurisdiction.getCode());
        }

    }

    public PlanDefinition findPlanDefinitionById(String identifier) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("SELECT  " + JSON + " FROM " + PLAN_DEFINITION_TABLE +
                    " WHERE " + ID + " =?", new String[]{identifier});
            if (cursor.moveToFirst()) {
                return gson.fromJson(cursor.getString(0), PlanDefinition.class);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public Set<PlanDefinition> findPlanDefinitionByIds(Set<String> identifiers) {
        Cursor cursor = null;
        Set<PlanDefinition> planDefinitions = new HashSet<>();
        try {
            String query = String.format("SELECT %s  FROM %s  WHERE %s IN (%s)", JSON, PLAN_DEFINITION_TABLE, ID,
                    TextUtils.join(",", Collections.nCopies(identifiers.size(), "?")));
            cursor = getReadableDatabase().rawQuery(query, identifiers.toArray(new String[0]));
            while (cursor.moveToNext()) {
                planDefinitions.add(gson.fromJson(cursor.getString(0), PlanDefinition.class));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return planDefinitions;
    }


    public Set<PlanDefinition> findAllPlanDefinitions() {
        Cursor cursor = null;
        Set<PlanDefinition> planDefinitions = new HashSet<>();
        try {
            String query = String.format("SELECT %s  FROM %s", JSON, PLAN_DEFINITION_TABLE);
            cursor = getReadableDatabase().rawQuery(query, null);
            while (cursor.moveToNext()) {
                planDefinitions.add(gson.fromJson(cursor.getString(0), PlanDefinition.class));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return planDefinitions;
    }

    public Set<String> findAllPlanDefinitionIds() {
        Cursor cursor = null;
        Set<String> ids = new HashSet<>();
        try {
            String query = String.format("SELECT %s  FROM %s", ID, PLAN_DEFINITION_TABLE);
            cursor = getReadableDatabase().rawQuery(query, null);
            while (cursor.moveToNext()) {
                ids.add(cursor.getString(0));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return ids;
    }

}
