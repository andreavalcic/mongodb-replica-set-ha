package com.szubp.mongodb_replica_set_ha.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.szubp.mongodb_replica_set_ha.dto.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class ReplicaSetService {

    private static final String EXP_COLLECTION = "replication_experiment";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    @Qualifier("secondaryMongoTemplate")
    private MongoTemplate secondaryMongoTemplate;

    @Autowired
    @Qualifier("w1MongoTemplate")
    private MongoTemplate w1MongoTemplate;

    @Autowired
    private MongoClient mongoClient;

    public ReplicationLagResultDto measureReplicationLag() throws InterruptedException {
        String testId = UUID.randomUUID().toString();
        Document testDoc = new Document("_id", testId)
                .append("experimentType", "replicationLag")
                .append("createdAt", new Date())
                .append("payload", UUID.randomUUID().toString());

        // w:1 write returns before secondaries have applied the oplog entry
        w1MongoTemplate.execute(EXP_COLLECTION, col -> {
            col.insertOne(testDoc);
            return null;
        });
        long writeTimestampMs = System.currentTimeMillis();

        // Poll secondary until the document appears — measures the replication lag
        int attempts = 0;
        long secondaryReadTimestampMs = -1;
        while (attempts < 200) {
            attempts++;
            Document found = secondaryMongoTemplate.execute(EXP_COLLECTION, col ->
                    col.find(new Document("_id", testId)).first());
            if (found != null) {
                secondaryReadTimestampMs = System.currentTimeMillis();
                break;
            }
            Thread.sleep(5);
        }

        long lagMs = secondaryReadTimestampMs > 0
                ? secondaryReadTimestampMs - writeTimestampMs
                : -1;

        mongoTemplate.execute(EXP_COLLECTION, col -> {
            col.deleteOne(new Document("_id", testId));
            return null;
        });

        return ReplicationLagResultDto.builder()
                .documentId(testId)
                .writeTimestampMs(writeTimestampMs)
                .secondaryReadTimestampMs(secondaryReadTimestampMs)
                .lagMs(lagMs)
                .pollAttempts(attempts)
                .explanation(lagMs >= 0
                        ? "Document written with w:1 (primary only). Secondary applied the oplog entry after ~"
                          + lagMs + " ms (" + attempts + " polls × 5 ms). "
                          + "This window is the replication lag — reads from secondary during this period return stale data."
                        : "Document did not appear on secondary within the 1 s polling budget. "
                          + "The secondary may be under load or lagging significantly.")
                .build();
    }

    public List<ReadPreferenceResultDto> demonstrateReadPreference() {
        String testId = UUID.randomUUID().toString();
        Document seed = new Document("_id", testId)
                .append("experimentType", "readPreference")
                .append("value", 42)
                .append("createdAt", new Date());
        mongoTemplate.execute(EXP_COLLECTION, col -> {
            col.insertOne(seed);
            return null;
        });

        List<ReadPreferenceResultDto> results = new ArrayList<>();

        long start = System.nanoTime();
        Document primaryResult = mongoTemplate.execute(EXP_COLLECTION, col ->
                col.find(new Document("_id", testId)).first());
        long primaryLatencyMs = (System.nanoTime() - start) / 1_000_000;

        results.add(ReadPreferenceResultDto.builder()
                .readPreference("PRIMARY")
                .latencyMs(primaryLatencyMs)
                .nodeDescription("Read routed to the PRIMARY — always returns the most recent committed data.")
                .result(primaryResult != null ? primaryResult.toJson() : null)
                .explanation("PRIMARY reads guarantee strong consistency (no staleness) but concentrate all read traffic on the primary node.")
                .build());

        start = System.nanoTime();
        Document secondaryResult = secondaryMongoTemplate.execute(EXP_COLLECTION, col ->
                col.find(new Document("_id", testId)).first());
        long secondaryLatencyMs = (System.nanoTime() - start) / 1_000_000;

        results.add(ReadPreferenceResultDto.builder()
                .readPreference("SECONDARY")
                .latencyMs(secondaryLatencyMs)
                .nodeDescription("Read routed to a SECONDARY — may return stale data within the replication lag window.")
                .result(secondaryResult != null ? secondaryResult.toJson() : null)
                .explanation("SECONDARY reads distribute read throughput across replica members. "
                        + "Acceptable when slight staleness is tolerable (e.g. reporting, analytics).")
                .build());

        mongoTemplate.execute(EXP_COLLECTION, col -> {
            col.deleteOne(new Document("_id", testId));
            return null;
        });

        return results;
    }

    public List<WriteConcernResultDto> demonstrateWriteConcern() {
        final int SAMPLE_SIZE = 10;

        long w1TotalNs = 0;
        List<String> w1Ids = new ArrayList<>();
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            String id = UUID.randomUUID().toString();
            w1Ids.add(id);
            Document doc = new Document("_id", id)
                    .append("experimentType", "writeConcernW1")
                    .append("iteration", i);
            long t = System.nanoTime();
            w1MongoTemplate.execute(EXP_COLLECTION, col -> {
                col.insertOne(doc);
                return null;
            });
            w1TotalNs += System.nanoTime() - t;
        }

        long majorityTotalNs = 0;
        List<String> majorityIds = new ArrayList<>();
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            String id = UUID.randomUUID().toString();
            majorityIds.add(id);
            Document doc = new Document("_id", id)
                    .append("experimentType", "writeConcernMajority")
                    .append("iteration", i);
            long t = System.nanoTime();
            mongoTemplate.execute(EXP_COLLECTION, col -> {
                col.insertOne(doc);
                return null;
            });
            majorityTotalNs += System.nanoTime() - t;
        }

        List<String> allIds = new ArrayList<>(w1Ids);
        allIds.addAll(majorityIds);
        mongoTemplate.execute(EXP_COLLECTION, col -> {
            col.deleteMany(new Document("_id", new Document("$in", allIds)));
            return null;
        });

        long w1AvgMs = w1TotalNs / SAMPLE_SIZE / 1_000_000;
        long majorityAvgMs = majorityTotalNs / SAMPLE_SIZE / 1_000_000;

        return List.of(
                WriteConcernResultDto.builder()
                        .writeConcern("w:1")
                        .latencyMs(w1AvgMs)
                        .sampleSize(SAMPLE_SIZE)
                        .durabilityNote("Only the PRIMARY acknowledged. Risk: write can be lost if the primary "
                                + "fails before the oplog entry is replicated to a secondary.")
                        .build(),
                WriteConcernResultDto.builder()
                        .writeConcern("w:majority")
                        .latencyMs(majorityAvgMs)
                        .sampleSize(SAMPLE_SIZE)
                        .durabilityNote("Majority of nodes (≥2 of 3) acknowledged. Write survives a primary "
                                + "failover. Higher latency = cost of the extra network round-trip to a secondary.")
                        .build()
        );
    }

    public ReplicaSetStatusDto getReplicaSetStatus() {
        MongoDatabase adminDb = mongoClient.getDatabase("admin");
        Document status = adminDb.runCommand(new Document("replSetGetStatus", 1));

        String setName = status.getString("set");
        // myState is returned as integer (1=PRIMARY, 2=SECONDARY, etc.)
        int myStateInt = status.getInteger("myState", 0);
        String myStateStr = stateCodeToString(myStateInt);
        int term = ((Number) status.getOrDefault("term", 0)).intValue();

        List<Document> members = status.getList("members", Document.class, List.of());

        Date primaryOptimeDate = members.stream()
                .filter(m -> m.getInteger("state", -1) == 1)
                .map(m -> (Date) m.get("optimeDate"))
                .findFirst()
                .orElse(null);

        String primaryHost = members.stream()
                .filter(m -> m.getInteger("state", -1) == 1)
                .map(m -> m.getString("name"))
                .findFirst()
                .orElse("unknown");

        List<NodeStatusDto> nodeDtos = new ArrayList<>();
        long maxLagMs = 0;

        for (Document m : members) {
            int state = m.getInteger("state", 0);
            Date optimeDate = (Date) m.get("optimeDate");

            long lagMs = 0;
            if (state == 2 && primaryOptimeDate != null && optimeDate != null) {
                lagMs = Math.max(0, primaryOptimeDate.getTime() - optimeDate.getTime());
                maxLagMs = Math.max(maxLagMs, lagMs);
            }

            Double health = m.getDouble("health");
            nodeDtos.add(NodeStatusDto.builder()
                    .memberId(m.getInteger("_id", 0))
                    .host(m.getString("name"))
                    .state(state)
                    .stateStr(m.getString("stateStr"))
                    .health(health != null ? health : 0.0)
                    .optimeDate(optimeDate)
                    .replicationLagMs(lagMs)
                    .build());
        }

        return ReplicaSetStatusDto.builder()
                .setName(setName)
                .myState(myStateStr)
                .term(term)
                .members(nodeDtos)
                .primaryHost(primaryHost)
                .maxReplicationLagMs(maxLagMs)
                .build();
    }

    private static String stateCodeToString(int state) {
        return switch (state) {
            case 0  -> "STARTUP";
            case 1  -> "PRIMARY";
            case 2  -> "SECONDARY";
            case 3  -> "RECOVERING";
            case 5  -> "STARTUP2";
            case 6  -> "UNKNOWN";
            case 7  -> "ARBITER";
            case 8  -> "DOWN";
            case 9  -> "ROLLBACK";
            case 10 -> "REMOVED";
            default -> "UNKNOWN(" + state + ")";
        };
    }

    public FailoverResultDto triggerFailover() throws InterruptedException, IOException {
        ReplicaSetStatusDto current = getReplicaSetStatus();
        String previousPrimary = current.getPrimaryHost();

        if (previousPrimary == null || previousPrimary.equals("unknown")) {
            return FailoverResultDto.builder()
                    .success(false)
                    .explanation("No primary found in the current replica set. " +
                            "The set may already be in an election or all nodes are down.")
                    .build();
        }

        String containerName = previousPrimary.split(":")[0];

        try {
            runDockerCommand("stop", containerName);
        } catch (RuntimeException e) {
            return FailoverResultDto.builder()
                    .previousPrimary(previousPrimary)
                    .stoppedContainer(containerName)
                    .success(false)
                    .explanation("docker stop failed — is Docker accessible from the JVM process? " +
                            "Error: " + e.getMessage())
                    .build();
        }

        long electionStartMs = System.currentTimeMillis();

        // Poll with nearest preference so surviving nodes respond after primary stops
        MongoDatabase adminDb = mongoClient.getDatabase("admin");
        String newPrimary = null;
        int attempts = 0;
        final int MAX_ATTEMPTS = 60; // 60 × 500 ms = 30 s

        while (attempts < MAX_ATTEMPTS) {
            attempts++;
            Thread.sleep(500);
            try {
                Document status = adminDb.runCommand(
                        new Document("replSetGetStatus", 1),
                        com.mongodb.ReadPreference.nearest());

                List<Document> members = status.getList("members", Document.class, List.of());

                newPrimary = members.stream()
                        .filter(m -> m.getInteger("state", -1) == 1)
                        .map(m -> m.getString("name"))
                        .filter(name -> !name.equals(previousPrimary))
                        .findFirst()
                        .orElse(null);

                if (newPrimary != null) break;

            } catch (Exception ignored) {
                // Driver reconnecting after primary stops — keep polling
            }
        }

        long electionTimeMs = System.currentTimeMillis() - electionStartMs;
        boolean success = newPrimary != null;

        String explanation;
        if (success) {
            explanation = String.format(
                    "PRIMARY '%s' was stopped (docker stop %s). " +
                    "After %d ms the replica set elected '%s' as the new PRIMARY " +
                    "(%d × 500 ms polls). " +
                    "During the election window all writes were unavailable — " +
                    "this is the brief downtime cost of the HA guarantee. " +
                    "The new primary was elected via Raft: the first secondary to " +
                    "reach a majority of votes (≥2 of 3) became leader.",
                    previousPrimary, containerName, electionTimeMs, newPrimary, attempts);
        } else {
            explanation = String.format(
                    "PRIMARY '%s' was stopped but no new primary was elected within 30 s. " +
                    "Possible causes: only 1 node remains healthy (no majority), " +
                    "or electionTimeoutMillis is configured above 30 s.",
                    previousPrimary);
        }

        return FailoverResultDto.builder()
                .previousPrimary(previousPrimary)
                .stoppedContainer(containerName)
                .newPrimary(success ? newPrimary : "none (timed out)")
                .electionTimeMs(electionTimeMs)
                .pollAttempts(attempts)
                .success(success)
                .explanation(explanation)
                .build();
    }

    public FailbackResultDto triggerFailback() throws InterruptedException, IOException {
        MongoDatabase adminDb = mongoClient.getDatabase("admin");

        Document status = adminDb.runCommand(
                new Document("replSetGetStatus", 1),
                com.mongodb.ReadPreference.nearest());

        List<Document> members = status.getList("members", Document.class, List.of());

        Document downedMember = members.stream()
                .filter(m -> {
                    Double health = m.getDouble("health");
                    return health != null && health == 0.0;
                })
                .findFirst()
                .orElse(null);

        if (downedMember == null) {
            return FailbackResultDto.builder()
                    .success(false)
                    .explanation("No downed member found in rs.status() — all nodes appear healthy. " +
                            "Run /api/replica-set/failover first.")
                    .build();
        }

        String restartedHost = downedMember.getString("name");
        String containerName = restartedHost.split(":")[0];

        try {
            runDockerCommand("start", containerName);
        } catch (RuntimeException e) {
            return FailbackResultDto.builder()
                    .restartedHost(restartedHost)
                    .restartedContainer(containerName)
                    .success(false)
                    .explanation("docker start failed — is Docker accessible from the JVM process? " +
                            "Error: " + e.getMessage())
                    .build();
        }

        long rejoinStartMs = System.currentTimeMillis();

        String finalState = null;
        int attempts = 0;
        final int MAX_ATTEMPTS = 120; // 120 × 500 ms = 60 s

        while (attempts < MAX_ATTEMPTS) {
            attempts++;
            Thread.sleep(500);
            try {
                Document pollStatus = adminDb.runCommand(
                        new Document("replSetGetStatus", 1),
                        com.mongodb.ReadPreference.nearest());

                List<Document> pollMembers = pollStatus.getList("members", Document.class, List.of());

                finalState = pollMembers.stream()
                        .filter(m -> restartedHost.equals(m.getString("name")))
                        .map(m -> stateCodeToString(m.getInteger("state", 0)))
                        .findFirst()
                        .orElse(null);

                if ("SECONDARY".equals(finalState)) break;

            } catch (Exception ignored) {
                // Driver still reconnecting — keep polling
            }
        }

        long rejoinTimeMs = System.currentTimeMillis() - rejoinStartMs;
        boolean success = "SECONDARY".equals(finalState);

        String explanation;
        if (success) {
            explanation = String.format(
                    "Container '%s' (%s) was restarted. After %d ms it rejoined as SECONDARY " +
                    "(%d × 500 ms polls). " +
                    "The node did NOT reclaim PRIMARY — the current primary holds a higher " +
                    "Raft election term. The restarted node caught up via oplog replication " +
                    "and transitioned STARTUP2 → RECOVERING → SECONDARY automatically. " +
                    "The replica set is now fully healthy with 3 members.",
                    containerName, restartedHost, rejoinTimeMs, attempts);
        } else {
            explanation = String.format(
                    "Container '%s' was restarted but did not reach SECONDARY within 60 s. " +
                    "Last observed state: %s. " +
                    "The node may still be performing initial sync. " +
                    "Call /api/replica-set/status to monitor progress.",
                    containerName, finalState != null ? finalState : "unreachable");
        }

        return FailbackResultDto.builder()
                .restartedHost(restartedHost)
                .restartedContainer(containerName)
                .rejoinedAs(finalState != null ? finalState : "unknown")
                .rejoinTimeMs(rejoinTimeMs)
                .pollAttempts(attempts)
                .success(success)
                .explanation(explanation)
                .build();
    }

    // Uses ProcessBuilder (not shell) to avoid command injection
    private void runDockerCommand(String command, String containerName)
            throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder("docker", command, containerName);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            String output = new String(process.getInputStream().readAllBytes());
            throw new RuntimeException(
                    "docker " + command + " " + containerName +
                    " exited with code " + exitCode + ": " + output.strip());
        }
    }
}

