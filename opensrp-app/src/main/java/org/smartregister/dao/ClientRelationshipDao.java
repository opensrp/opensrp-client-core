package org.smartregister.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import org.smartregister.domain.ClientRelationship;

import java.util.List;

/**
 * Created by samuelgithengi on 7/9/20.
 */
@Dao
public interface ClientRelationshipDao {

    @Query("SELECT * FROM client_relationship WHERE relationship=:relationship AND relational_id=:relationalId")
    List<ClientRelationship> findClientRelationships(String relationship, String relationalId);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ClientRelationship... clientRelationships);

}
