package com.szubp.mongodb_replica_set_ha.db.model;

import org.springframework.data.annotation.Id;

/**
 * Base class for all MongoDB document entities.
 *
 * Replaces JPA's BaseEntity + UidGenerator. The ID is a 23-character
 * UUID-derived string, initialized eagerly on construction so every
 * new instance already has an ID before it is persisted - no Hibernate
 * lifecycle hook required.
 */
public abstract class BaseDocument {

    @Id
    private String id = java.util.UUID.randomUUID()
            .toString()
            .replace("-", "")
            .substring(0, 23);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
