package com.szubp.mongodb_replica_set_ha.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of one WriteConcern benchmark run.
 * Two of these are returned per call to /write-concern:
 * one for w:1 and one for w:majority.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WriteConcernResultDto {

    /** "w:1" or "w:majority" */
    private String writeConcern;

    /** Average latency per write over sampleSize operations (ms) */
    private long latencyMs;

    /** Number of writes averaged to produce latencyMs */
    private int sampleSize;

    /** Durability trade-off note for academic context */
    private String durabilityNote;
}
