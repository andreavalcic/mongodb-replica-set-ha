package com.szubp.mongodb_replica_set_ha.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of one read operation with a specific ReadPreference.
 * Two of these are returned per call to /read-preference:
 * one for PRIMARY and one for SECONDARY.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadPreferenceResultDto {

    /** "PRIMARY" or "SECONDARY" */
    private String readPreference;

    /** Wall-clock latency of the read operation (ms) */
    private long latencyMs;

    /** Which type of node handled the read */
    private String nodeDescription;

    /** The document returned (JSON string), or null if not found */
    private Object result;

    /** Academic explanation of the trade-off */
    private String explanation;
}
