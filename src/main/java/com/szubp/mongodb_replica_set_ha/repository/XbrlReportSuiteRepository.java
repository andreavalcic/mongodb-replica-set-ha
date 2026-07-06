package com.szubp.mongodb_replica_set_ha.repository;

import com.szubp.mongodb_replica_set_ha.db.model.XbrlReportSuite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface XbrlReportSuiteRepository extends JpaRepository<XbrlReportSuite, String> {

	@Override
	@NonNull
	Optional<XbrlReportSuite> findById(@NonNull String id);

	@NonNull
	Optional<XbrlReportSuite> findByName(@NonNull String name);

	boolean existsByNameAndKvknumber(@NonNull String name, @NonNull String kvkNumber);

	boolean existsByIdAndKvknumber(@NonNull String id, @NonNull String kvkNumber);

	@NonNull
	Optional<XbrlReportSuite> findByIdAndKvknumber(@NonNull String id, @NonNull String kvknumber);

	List<XbrlReportSuite> findAllByKvknumberOrderByCreatedAtDesc(String kvknumber);

	@Query("select distinct s from XbrlReportSuite s left join s.attachments where s.kvknumber = :kvkNumber")
	List<XbrlReportSuite> allWithAttachments(@Param("kvkNumber") String kvkNumber);

	@Query("SELECT distinct suite FROM XbrlReportSuite suite left join fetch suite.deliveryList deliveries where suite" +
			".id = :suiteId")
	Optional<XbrlReportSuite> findByIdWithDeliveries(@Param("suiteId") String suiteId);

	@Query("select drs from XbrlReportSuite drs where drs.status in :statuses and drs.kvknumber=:kvkNumber order by drs.createdAt desc")
	List<XbrlReportSuite> findAllWhereKvknumberOrderByCreatedAtDesc(@Param("kvkNumber") String kvkNumber);
}
