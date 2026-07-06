package com.szubp.mongodb_replica_set_ha.db.model

enum class XbrlDocumentStatus(val order: Int) {
	/** Xbrl document is editable - active for changes */
	ACTIVE(1),
	/** Xbrl document is finalized and no longer active for changes, but attachments are still editable */
	COMPLETED(2),
	/** All needed attachments are added and the document is ready for sending */
	APPROVED(3),
	/** Suite is sent to the Digipoort authority, no changes allowed anymore */
	SENT(4);

	companion object {
		val REVERTABLE_STATUSES = setOf(COMPLETED, APPROVED, SENT)
	}
}
