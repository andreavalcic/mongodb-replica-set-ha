package com.szubp.mongodb_replica_set_ha.db.model

enum class SuiteDeliveryStatus(val digiPortCodes: List<String>? = null) {
	REJECTED(listOf("420")),
	COMPLETED(listOf("500")),
	BUSY;

	companion object {
		fun determineSuiteDeliveryStatus(deliveries: List<XbrlSuiteDelivery>): SuiteDeliveryStatus = when {
			allSuccess(deliveries) -> COMPLETED
			allRejected(deliveries) -> REJECTED
			else -> BUSY
		}

		fun allSuccess(deliveries: List<XbrlSuiteDelivery>): Boolean {
			return deliveries.all { it.statuses.any { status -> status.code in COMPLETED.digiPortCodes!! } }
		}

		fun allRejected(deliveries: List<XbrlSuiteDelivery>): Boolean {
			return deliveries.all { it.statuses.any { status -> status.code in REJECTED.digiPortCodes!! } }
		}
	}
}

