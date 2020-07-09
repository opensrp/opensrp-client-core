package org.smartregister.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import org.smartregister.domain.ClientRelationship;

/**
 * Created by samuelgithengi on 7/9/20.
 */
@Database(entities = {ClientRelationship.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ClientRelationshipDao clientRelationshipDao();
}