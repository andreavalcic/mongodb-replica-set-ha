package com.szubp.mongodb_replica_set_ha.repository;

import com.szubp.mongodb_replica_set_ha.db.model.XbrlSuiteDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface XbrlSuiteDeliveryRepository extends JpaRepository<XbrlSuiteDelivery, String> {

	@Query("SELECT sd FROM XbrlSuiteDelivery sd WHERE sd.xbrlSuite.id = :suiteId AND sd.receiverdesc = :receiverDesc AND sd.active = true")
	List<XbrlSuiteDelivery> findActiveByXbrlSuiteIdAndReceiverDesc(@Param("suiteId") String suiteId, @Param("receiverDesc") String receiverDesc);

	@Query("SELECT sd FROM XbrlSuiteDelivery sd WHERE sd.xbrlSuite.id = :suiteId AND sd.active = true")
	List<XbrlSuiteDelivery> findActiveByXbrlSuiteId(@Param("suiteId") String suiteId);
}

