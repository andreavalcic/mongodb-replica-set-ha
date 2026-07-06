package com.szubp.mongodb_replica_set_ha.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Represents a single member of the replica set as reported by replSetGetStatus.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeStatusDto {

    /** Member index in the replica set config (0, 1, 2 …) */
    private int memberId;

    /** hostname:port of the member */
    private String host;

    /**
     * Replica-set state code:
     *   1 = PRIMARY, 2 = SECONDARY, 7 = ARBITER,
     *   6 = UNKNOWN, 8 = DOWN, etc.
     */
    private int state;

    /** Human-readable state: "PRIMARY", "SECONDARY", "ARBITER", … */
    private String stateStr;

    /** 1.0 = healthy, 0.0 = unreachable */
    private double health;

    /** Timestamp of the last oplog entry applied by this node */
    private Date optimeDate;

    /**
     * Replication lag vs the current primary (milliseconds).
     * 0 for the primary itself.
     * Calculated as: primaryOptimeDate - thisNodeOptimeDate.
     */
    private long replicationLagMs;
}
