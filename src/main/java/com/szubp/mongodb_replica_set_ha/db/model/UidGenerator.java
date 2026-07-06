package com.szubp.mongodb_replica_set_ha.db.model;

/**
 * @deprecated No longer used after MongoDB migration.
 * ID generation is now handled by BaseDocument field initialisation.
 */
@Deprecated
public class UidGenerator {
    public static String generate() {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 23);
    }
}
