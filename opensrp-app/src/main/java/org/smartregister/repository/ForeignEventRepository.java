package org.smartregister.repository;

import android.content.Context;

public class ForeignEventRepository extends EventRepository {
    public static final String TABLE_NAME = "foreign_events";

    public ForeignEventRepository(Context context, String[] columns) {
        super(context, TABLE_NAME, columns);
    }
}
