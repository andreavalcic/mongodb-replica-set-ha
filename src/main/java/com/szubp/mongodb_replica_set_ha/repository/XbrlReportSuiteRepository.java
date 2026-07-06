package com.szubp.mongodb_replica_set_ha.repository;

import com.szubp.mongodb_replica_set_ha.db.model.XbrlDocumentStatus;
import com.szubp.mongodb_replica_set_ha.db.model.XbrlReportSuite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Replaces JpaRepository<XbrlReportSuite, String>.
 *
 * Migration notes:
 *   - JPQL @Query annotations replaced with MongoDB JSON query syntax.
 *   - allWithAttachments: JPA used a LEFT JOIN to force-load the collection;
 *     in MongoDB, attachments are embedded - every findAll already includes them.
 *   - findByIdWithDeliveries: same reasoning - deliveries are embedded, so
 *     findById already returns the full aggregate. Default method delegates.
 *   - findAllWhereKvknumberOrderByCreatedAtDesc: status IN clause rewritten
 *     using MongoDB $in operator.
 */
public interface XbrlReportSuiteRepository extends MongoRepository<XbrlReportSuite, String> {

    @Override
    @NonNull
    Optional<XbrlReportSuite> findById(@NonNull String id);

    @NonNull
    Optional<XbrlReportSuite> findByName(@NonNull String name);

    boolean existsByNameAndKvknumber(@NonNull String name, @NonNull String kvknumber);

    boolean existsByIdAndKvknumber(@NonNull String id, @NonNull String kvknumber);

    @NonNull
    Optional<XbrlReportSuite> findByIdAndKvknumber(@NonNull String id, @NonNull String kvknumber);

    List<XbrlReportSuite> findAllByKvknumberOrderByCreatedAtDesc(String kvknumber);

    /**
     * In MongoDB, attachments are embedded inside the suite document -
     * there is no join; every suite returned already contains its attachments.
     */
    @Query("{ 'kvknumber': ?0 }")
    List<XbrlReportSuite> allWithAttachments(String kvkNumber);

    /**
     * Deliveries are embedded inside the suite document - findById already
     * returns the full aggregate including delivery_list.
     */
    default Optional<XbrlReportSuite> findByIdWithDeliveries(String suiteId) {
        return findById(suiteId);
    }

    /**
     * Replaces JPQL: "… where s.status in :statuses and s.kvknumber = :kvkNumber …"
     * MongoDB equivalent uses the $in operator.
     */
    @Query("{ 'kvknumber': ?0, 'status': { $in: ?1 } }")
    List<XbrlReportSuite> findAllWhereKvknumberOrderByCreatedAtDesc(
            String kvkNumber, List<XbrlDocumentStatus> statuses);
}
