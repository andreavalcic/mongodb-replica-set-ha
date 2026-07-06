package com.szubp.mongodb_replica_set_ha.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of the replication-lag experiment:
 * write to PRIMARY with w:1, then poll SECONDARY until the document appears.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplicationLagResultDto {

    /** ID of the test document written to the primary */
    private String documentId;

    /** Wall-clock timestamp (ms) when the primary acknowledged the write */
    private long writeTimestampMs;

    /**
     * Wall-clock timestamp (ms) when the document first appeared on a secondary.
     * -1 if the document never appeared within the polling budget.
     */
    private long secondaryReadTimestampMs;

    /**
     * Measured replication lag = secondaryReadTimestampMs − writeTimestampMs (ms).
     * -1 if replication did not complete within the polling budget.
     *
     * NOTE: this is an upper-bound estimate. The actual MongoDB oplog propagation
     * may have completed earlier; we detect it only at the next poll interval.
     */
    private long lagMs;

    /** Number of 5 ms polling cycles before the document was visible on secondary */
    private int pollAttempts;

    /** Academic explanation for the observed result */
    private String explanation;
}
