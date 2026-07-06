package com.szubp.mongodb_replica_set_ha.repository;

import com.szubp.mongodb_replica_set_ha.db.model.XbrlAttachmentType;
import com.szubp.mongodb_replica_set_ha.db.model.XbrlReportSuite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * After MongoDB migration XbrlAttachment is an embedded subdocument inside
 * XbrlReportSuite - it has no own collection, so this repository operates on
 * the parent "xbrl_report_suites" collection.
 *
 * Original JPA methods that referenced a suite via a separate FK
 * (findBySuiteAndType, existsBySuiteAndType) are replaced with equivalent
 * MongoDB queries that match on the suite's _id and the embedded array field.
 *
 * For finer-grained extraction of individual XbrlAttachment objects from the
 * returned suite, callers should filter suite.getAttachments() in application code
 * or use an aggregation pipeline via MongoTemplate.
 */
public interface XbrlAttachmentRepository extends MongoRepository<XbrlReportSuite, String> {

    /** Find suites that contain at least one attachment of the given type. */
    @Query("{ 'attachments.type': ?0 }")
    List<XbrlReportSuite> findSuitesByAttachmentType(XbrlAttachmentType type);

    /** True if the suite with the given id contains an attachment of the given type. */
    @Query(value = "{ '_id': ?0, 'attachments.type': ?1 }", exists = true)
    boolean existsBySuiteIdAndAttachmentType(String suiteId, XbrlAttachmentType type);

    /** Return the suite if it contains an attachment of the given type. */
    @Query("{ '_id': ?0, 'attachments.type': ?1 }")
    Optional<XbrlReportSuite> findBySuiteIdAndAttachmentType(String suiteId, XbrlAttachmentType type);
}
