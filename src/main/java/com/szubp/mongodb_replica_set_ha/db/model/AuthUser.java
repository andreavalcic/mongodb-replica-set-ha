package com.szubp.mongodb_replica_set_ha.db.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * Standalone MongoDB document - other aggregates reference it via @DBRef.
 * Collection name mirrors the original PostgreSQL table for consistency.
 */
@Document(collection = "auth_users")
public class AuthUser extends BaseDocument implements Serializable {

    @Field("email")
    @Indexed(unique = true)   // replaces @Column(unique = true)
    private String email;

    @Field("fullname")
    private String fullname;

    @Field("kvknumber")
    private String kvknumber;

    public String getEmail()           { return email; }
    public void setEmail(String v)     { email = v; }

    public String getFullname()        { return fullname; }
    public void setFullname(String v)  { fullname = v; }

    public String getKvknumber()       { return kvknumber; }
    public void setKvknumber(String v) { kvknumber = v; }
}
