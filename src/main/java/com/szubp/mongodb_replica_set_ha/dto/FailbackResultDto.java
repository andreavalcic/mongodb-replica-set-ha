package com.szubp.mongodb_replica_set_ha.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of the failback experiment:
 * restart the previously stopped container and wait for it to rejoin
 * the replica set as SECONDARY.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailbackResultDto {

    /**
     * hostname:port of the node that was restarted.
     * e.g. "mongo1:27017"
     */
    private String restartedHost;

    /**
     * Docker container name that was restarted.
     * e.g. "mongo1"
     */
    private String restartedContainer;

    /**
     * Role the node holds after rejoining.
     * Should always be "SECONDARY" - a restarted node never immediately
     * reclaims PRIMARY because the current primary has a higher election term.
     */
    private String rejoinedAs;

    /**
     * Wall-clock time from docker start returning until the node appeared
     * as SECONDARY in rs.status() (ms).
     *
     * This includes:
     *   - container startup time
     *   - mongod startup and journal recovery
     *   - oplog catch-up replication from the current primary
     *   - heartbeat propagation so other members update their view
     */
    private long rejoinTimeMs;

    /** Number of 500 ms polling cycles before the node appeared as SECONDARY */
    private int pollAttempts;

    /** true if the node rejoined as SECONDARY within the polling budget */
    private boolean success;

    /** Academic explanation of what happened */
    private String explanation;
}
