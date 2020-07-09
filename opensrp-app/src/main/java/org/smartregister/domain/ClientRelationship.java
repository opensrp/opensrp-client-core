package org.smartregister.domain;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by samuelgithengi on 7/9/20.
 */
@Entity(tableName = "client_relationship", indices = {@Index(value = {"base_entity_id", "relationship"})})
@Getter
@Setter
@Builder
public class ClientRelationship {

    @ColumnInfo(name = "base_entity_id")
    private String baseEntityId;

    @ColumnInfo(name = "relationship")
    private String relationship;

    @ColumnInfo(name = "relational_id")
    private String relationalId;

}
