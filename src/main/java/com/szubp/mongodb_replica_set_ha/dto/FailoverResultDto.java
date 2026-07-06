package com.szubp.mongodb_replica_set_ha.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of the failover experiment:
 * kill the current PRIMARY, wait for Raft election, measure election time.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailoverResultDto {

    /**
     * hostname:port of the primary that was stopped.
     * e.g. "mongo1:27017"
     */
    private String previousPrimary;

    /**
     * Docker container name that was stopped.
     * e.g. "mongo1"
     */
    private String stoppedContainer;

    /**
     * hostname:port of the node that won the election.
     * "none (timed out)" if no new primary appeared within 30 s.
     */
    private String newPrimary;

    /**
     * Wall-clock time from docker stop returning until the new primary
     * was first observed in rs.status() (ms).
     *
     * This includes:
     *   - time for the other nodes to detect the primary is gone
     *     (electionTimeoutMillis, default 10 s)
     *   - time for a candidate to win a Raft election
     *   - time for the Java driver to discover the new topology
     */
    private long electionTimeMs;

    /** Number of 500 ms polling cycles before the new primary was detected */
    private int pollAttempts;

    /** true if a new primary was elected within the polling budget */
    private boolean success;

    /** Academic explanation of what happened */
    private String explanation;
}
