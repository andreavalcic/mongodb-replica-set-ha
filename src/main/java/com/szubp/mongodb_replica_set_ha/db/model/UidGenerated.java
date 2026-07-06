package com.szubp.mongodb_replica_set_ha.db.model;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @deprecated No longer used after MongoDB migration.
 * ID generation is now handled by BaseDocument field initialisation.
 */
@Deprecated
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface UidGenerated {}
