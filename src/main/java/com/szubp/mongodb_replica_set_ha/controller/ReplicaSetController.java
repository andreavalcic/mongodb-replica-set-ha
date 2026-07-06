package com.szubp.mongodb_replica_set_ha.controller;

import com.szubp.mongodb_replica_set_ha.dto.*;
import com.szubp.mongodb_replica_set_ha.service.ReplicaSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/replica-set")
public class ReplicaSetController {

    @Autowired
    private ReplicaSetService replicaSetService;

    @GetMapping("/status")
    public ResponseEntity<ReplicaSetStatusDto> getStatus() {
        return ResponseEntity.ok(replicaSetService.getReplicaSetStatus());
    }

    @GetMapping("/replication-lag")
    public ResponseEntity<ReplicationLagResultDto> measureReplicationLag() {
        try {
            return ResponseEntity.ok(replicaSetService.measureReplicationLag());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/read-preference")
    public ResponseEntity<List<ReadPreferenceResultDto>> demonstrateReadPreference() {
        return ResponseEntity.ok(replicaSetService.demonstrateReadPreference());
    }

    @GetMapping("/write-concern")
    public ResponseEntity<List<WriteConcernResultDto>> demonstrateWriteConcern() {
        return ResponseEntity.ok(replicaSetService.demonstrateWriteConcern());
    }

    // WARNING: stops a Docker container. Run /failback afterwards to restore the replica set.
    @GetMapping("/failover")
    public ResponseEntity<FailoverResultDto> triggerFailover() {
        try {
            return ResponseEntity.ok(replicaSetService.triggerFailover());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(FailoverResultDto.builder()
                            .success(false)
                            .explanation("IO error executing docker command: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/failback")
    public ResponseEntity<FailbackResultDto> triggerFailback() {
        try {
            return ResponseEntity.ok(replicaSetService.triggerFailback());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(FailbackResultDto.builder()
                            .success(false)
                            .explanation("IO error executing docker command: " + e.getMessage())
                            .build());
        }
    }
}

