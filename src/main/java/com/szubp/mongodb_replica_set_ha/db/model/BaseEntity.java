package com.szubp.mongodb_replica_set_ha.db.model;

/**
 * @deprecated Replaced by {@link BaseDocument} after migration to MongoDB.
 * Retained as a compile stub to avoid breaking any residual references.
 */
@Deprecated
public abstract class BaseEntity {
    private String id;
    public String getId()        { return id; }
    public void setId(String id) { this.id = id; }
}
