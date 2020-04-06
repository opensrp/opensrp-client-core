package org.smartregister.repository;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Manifest;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by cozej4 on 2020-04-06.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class ManifestRepository extends BaseRepository {

    protected static final String ID = "id";
    protected static final String APP_VERSION = "app_version";
    protected static final String FORM_VERSION = "form_version";
    protected static final String MODEL_VERSION = "model_version";
    protected static final String IDENTIFIERS = "identifiers";
    protected static final String IS_NEW = "is_new";
    protected static final String ACTIVE = "active";


    protected static final String MANIFEST_TABLE = "Manifest";

    protected static final String[] COLUMNS = new String[]{ID, APP_VERSION, FORM_VERSION, MODEL_VERSION, IDENTIFIERS, IS_NEW, ACTIVE};
    private static final String CREATE_MANIFEST_TABLE =
            "CREATE TABLE " + MANIFEST_TABLE + " (" +
                    ID + " VARCHAR NOT NULL PRIMARY KEY," +
                    APP_VERSION + " VARCHAR , " +
                    FORM_VERSION + " VARCHAR , " +
                    MODEL_VERSION + " VARCHAR , " +
                    IDENTIFIERS + " VARCHAR , " +
                    IS_NEW + " VARCHAR , " +
                    ACTIVE + " VARCHAR NOT NULL ) ";

    private static final String CREATE_LOCATION_NAME_INDEX = "CREATE INDEX "
            + MANIFEST_TABLE + "_" + APP_VERSION + "_ind ON " + MANIFEST_TABLE + "(" + APP_VERSION + ")";

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_MANIFEST_TABLE);
        database.execSQL(CREATE_LOCATION_NAME_INDEX);
    }

    protected String getManifestTableName() {
        return MANIFEST_TABLE;
    }

    public void addOrUpdate(Manifest manifest) {
        if (StringUtils.isBlank(manifest.getId()))
            throw new IllegalArgumentException("id not provided");
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, manifest.getId());
        contentValues.put(APP_VERSION, manifest.getAppVersion());
        contentValues.put(FORM_VERSION, manifest.getFormVersion());
        contentValues.put(MODEL_VERSION, manifest.getModelVersion());
        contentValues.put(IDENTIFIERS, new Gson().toJson(manifest.getIdentifiers()));
        contentValues.put(IS_NEW, manifest.isNew());
        contentValues.put(ACTIVE, manifest.isActive());
        getWritableDatabase().replace(getManifestTableName(), null, contentValues);
    }

    protected Manifest readCursor(Cursor cursor) {
        Manifest manifest = new Manifest();
        manifest.setId(cursor.getString(cursor.getColumnIndex(ID)));
        manifest.setAppVersion(cursor.getString(cursor.getColumnIndex(APP_VERSION)));
        manifest.setFormVersion(cursor.getString(cursor.getColumnIndex(FORM_VERSION)));
        manifest.setModelVersion(cursor.getString(cursor.getColumnIndex(MODEL_VERSION)));
        manifest.setNew(cursor.getInt(cursor.getColumnIndex(IS_NEW)) == 1);
        manifest.setActive(cursor.getInt(cursor.getColumnIndex(ACTIVE)) == 1);
        manifest.setIdentifiers(new Gson().fromJson(cursor.getString(cursor.getColumnIndex(IDENTIFIERS)), new TypeToken<List<String>>() {
        }.getType()));

        return manifest;
    }

    /**
     * Get a list of Manifest for the passed appVersion
     *
     * @param appVersion of a the client form
     * @return a list of Manifest for the passed appVersion
     */
    public List<Manifest> getManifestByAppVersion(String appVersion) {
        List<Manifest> manifests = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + getManifestTableName() +
                " WHERE " + APP_VERSION + " =?", new String[]{appVersion})) {
            while (cursor.moveToNext()) {
                manifests.add(readCursor(cursor));
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return manifests;

    }
}
