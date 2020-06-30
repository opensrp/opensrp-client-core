package org.smartregister.repository;

import android.content.Context;

public class ForeignClientRepository extends ClientRepository {

    public static final String TABLE_NAME = "foreign_clients";

    public ForeignClientRepository(Context context, String[] columns) {
        super(context, TABLE_NAME, columns);
    }
}
