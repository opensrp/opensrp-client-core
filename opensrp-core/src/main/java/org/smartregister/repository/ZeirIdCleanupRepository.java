package org.smartregister.repository;

import net.sqlcipher.Cursor;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class ZeirIdCleanupRepository extends BaseRepository {

    private static final String BASE_ENTITY_ID = "baseEntityId";

    private static final String DUPLICATES_SQL =
            "WITH duplicates AS ( " +
                    "  WITH clients AS ( " +
                    "    SELECT baseEntityId, COALESCE(json_extract(json, '$.identifiers.ZEIR_ID'), json_extract(json, '$.identifiers.M_ZEIR_ID')) zeir_id " +
                    "    FROM client " +
                    "  ) " +
                    "  SELECT b.* FROM (SELECT baseEntityId, zeir_id FROM clients GROUP BY zeir_id HAVING count(zeir_id) > 1) a " +
                    "  INNER JOIN clients b ON a.zeir_id=b.zeir_id " +
                    "  UNION " +
                    "  SELECT * FROM clients WHERE zeir_id IS NULL " +
                    ") " +
                    "SELECT baseEntityId, zeir_id, lag(zeir_id) over(order by zeir_id) AS prev_zeir_id FROM duplicates";

    public Map<String, String> getClientsWithDuplicateZeirIds() {
        Map<String, String> duplicates = new HashMap<>();

        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery(DUPLICATES_SQL, new String[]{});

            while (cursor.moveToNext()) {
                String baseEntityId = cursor.getString(cursor.getColumnIndex(BASE_ENTITY_ID));
                String zeirId = cursor.getString(cursor.getColumnIndex("zeir_id"));

                duplicates.put(baseEntityId, zeirId);

                String prevZeirId = null;
                try {
                    prevZeirId = cursor.getString(cursor.getColumnIndex("prev_zeir_id"));
                } catch (NullPointerException e) {
                    Timber.e(e, "null prev_zeir_id");
                }

                if (StringUtils.isNotEmpty(prevZeirId) && (prevZeirId.equals(zeirId))) {
                    duplicates.put(baseEntityId, prevZeirId);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        return duplicates;
    }
}
