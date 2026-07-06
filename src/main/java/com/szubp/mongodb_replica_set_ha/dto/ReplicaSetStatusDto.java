package com.szubp.mongodb_replica_set_ha.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Replica set topology snapshot equivalent to rs.status() in mongosh.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplicaSetStatusDto {

    /** Replica set name (e.g. "rs0") */
    private String setName;

    /** State of the node that served this request ("PRIMARY" / "SECONDARY") */
    private String myState;

    /** Current Raft election term - increments on every primary election */
    private int term;

    /** All replica-set members with their individual status and lag */
    private List<NodeStatusDto> members;

    /** Hostname of the current primary */
    private String primaryHost;

    /**
     * Maximum replication lag across all secondary members (ms).
     * Zero means secondaries are fully caught up.
     */
    private long maxReplicationLagMs;
}
