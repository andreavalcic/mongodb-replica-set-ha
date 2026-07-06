package com.szubp.mongodb_replica_set_ha.repository;

import com.szubp.mongodb_replica_set_ha.db.model.XbrlAttachment;
import com.szubp.mongodb_replica_set_ha.db.model.XbrlAttachmentType;
import com.szubp.mongodb_replica_set_ha.db.model.XbrlReportSuite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface XbrlAttachmentRepository extends JpaRepository<XbrlAttachment, String> {

	List<XbrlAttachment> findBySuite(XbrlReportSuite suite);

	boolean existsBySuiteAndType(XbrlReportSuite suite, XbrlAttachmentType type);

	Optional<XbrlAttachment> findByType(XbrlAttachmentType type);

	Optional<XbrlAttachment> findBySuiteIdAndType(String suiteId, XbrlAttachmentType type);
}
