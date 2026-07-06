package com.szubp.mongodb_replica_set_ha.repository;

import com.szubp.mongodb_replica_set_ha.db.model.XbrlReportSuite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * After MongoDB migration XbrlSuiteDelivery is an embedded subdocument inside
 * XbrlReportSuite - it has no own collection, so this repository operates on
 * the parent "xbrl_report_suites" collection.
 *
 * Original JPA queries that joined on xbrlSuite.id are replaced with MongoDB
 * $elemMatch queries that filter on the embedded delivery_list array.
 *
 * Callers should extract and post-filter the delivery_list from the returned
 * suite(s) in application code.
 */
public interface XbrlSuiteDeliveryRepository extends MongoRepository<XbrlReportSuite, String> {

    /**
     * Replaces JPQL: "… WHERE sd.xbrlSuite.id = :suiteId
     *                        AND sd.receiverdesc = :receiverDesc
     *                        AND sd.active = true"
     *
     * Returns suites containing at least one matching embedded delivery.
     * Call suite.getDeliveryList() and filter in code for the exact delivery.
     */
    @Query("{ '_id': ?0, 'delivery_list': { $elemMatch: { 'receiverdesc': ?1, 'active': true } } }")
    List<XbrlReportSuite> findSuitesWithActiveDeliveryByReceiverDesc(String suiteId, String receiverDesc);

    /**
     * Replaces JPQL: "… WHERE sd.xbrlSuite.id = :suiteId AND sd.active = true"
     */
    @Query("{ '_id': ?0, 'delivery_list': { $elemMatch: { 'active': true } } }")
    List<XbrlReportSuite> findSuitesWithActiveDeliveries(String suiteId);
}
