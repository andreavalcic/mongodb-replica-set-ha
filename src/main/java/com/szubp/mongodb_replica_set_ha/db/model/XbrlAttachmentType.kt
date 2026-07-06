package com.szubp.mongodb_replica_set_ha.db.model

import kotlin.collections.plus

/**
 * Type of XBRL attachment. Relates to type and state of XBRL document.
 */
enum class XbrlAttachmentType(val fileName:String, val xbrlDocTypes: List<XbrlDocumentType>, val status: XbrlDocumentStatus, val isMandatory: Boolean, val usedAsCachedDoc: Boolean = false, val validUntil: Int? = null) {
	/** Accountantsverslag */
	ACCOUNTANT_REP("Accountantsverslag.pdf", listOf(XbrlDocumentType.DVI), XbrlDocumentStatus.COMPLETED, true),

	/** Accountantsverklaring */
	ACC_VERKLARING("Accountantsverklaring.xbrl", listOf(XbrlDocumentType.KVK), XbrlDocumentStatus.COMPLETED, true),

	/** Detached signature */
	DETACH_SIGNATURE("Detached_signature.xml", listOf(XbrlDocumentType.KVK), XbrlDocumentStatus.APPROVED, true, true),

	/** Jaarverslag */
	ANNUAL_REP("Jaarverslag.pdf", listOf(XbrlDocumentType.DVI), XbrlDocumentStatus.COMPLETED, true),

	/** Jaarrekening */
	FIN_STATEMENTS("Jaarrekening.pdf", listOf(XbrlDocumentType.DVI), XbrlDocumentStatus.COMPLETED, false),

	/** Volkshuisvestingverslag */
	PUBLIC_HOUS_PER("Volkshuisvestingsverslag.pdf", listOf(XbrlDocumentType.DVI), XbrlDocumentStatus.COMPLETED, false),

	/** Controleverklaring */
	AUDITOR_REP("Controleverklaring.pdf", listOf(XbrlDocumentType.DVI), XbrlDocumentStatus.COMPLETED, false),

	/** Managementletter */
	MANAGEMENT_LETR("Managementletter.pdf", listOf(XbrlDocumentType.DVI), XbrlDocumentStatus.COMPLETED, false),

	/** WOZ Bezitstabel */
	WOZ_PROPERTY_TBL("WOZ bezitstabel.csv", listOf(XbrlDocumentType.DVI), XbrlDocumentStatus.ACTIVE, true),

	/** Bestuursverklaring */
	BOARD_STATEMENT("Bestuursverklaring.pdf", listOf(XbrlDocumentType.DVI), XbrlDocumentStatus.COMPLETED, true, false, 2022),

	/** Assurance wet en regelgeving */
	LAW_REGULA_ASS("Assurance wet en regelgeving.pdf", listOf(), XbrlDocumentStatus.COMPLETED, true),

	/** Assurance cijfermatig verantwoording */
	NUM_ACCOUNTA_ASS("Assurance cijfermatige verantwoording.pdf", listOf(), XbrlDocumentStatus.COMPLETED, true),

	/** Bestuursverklaring */
	BOARD_STATEMENT2("Bestuursverklaring.pdf", listOf(), XbrlDocumentStatus.COMPLETED, true),

	/** Meerjarenbegroting */
	MULTI_YEAR_BUDG("Meerjarenbegroting.pdf", listOf(XbrlDocumentType.DPI), XbrlDocumentStatus.COMPLETED, true),

	/** Begroting */
	BUDGET("Begroting.pdf", listOf(XbrlDocumentType.DPI), XbrlDocumentStatus.COMPLETED, false),

	/** Aedes Bezitstabel */
	AEDES_PROPS_TBL("Aedes Bezitstabel.csv", listOf(XbrlDocumentType.DVI), XbrlDocumentStatus.ACTIVE, false),

	/** Special type - used to cache XBRL export to PDF */
	EXPORT_TO_PDF("XBRL.pdf", listOf(XbrlDocumentType.DVI, XbrlDocumentType.KVK), XbrlDocumentStatus.ACTIVE, false, true),

	/** Special type - used to cache XBRL export to PDF with correction checks */
	EXPORT_TO_PDF_C("XBRL-with-corrections.pdf", listOf(XbrlDocumentType.DVI, XbrlDocumentType.KVK), XbrlDocumentStatus.ACTIVE, false, true),

	/** Special type - used to cache accountant XBRL export to PDF */
	ACCOUNT_PDF("Accountant_XBRL.pdf", listOf(XbrlDocumentType.DVI, XbrlDocumentType.KVK), XbrlDocumentStatus.ACTIVE, false, true),
	;

	companion object {

		val EDITABLE_ATTACHMENT_TYPES_OVERRIDE = mapOf(
			XbrlDocumentType.DVI to setOf(WOZ_PROPERTY_TBL),
		)

		@JvmStatic
		fun getApplicableTypes(type: XbrlDocumentType, status: XbrlDocumentStatus, dataYear: Int): List<XbrlAttachmentType> =
            //TODO: AEDES - temporarily removed
            entries.filter { it.xbrlDocTypes.contains(type)
                    && it.status <= status
                    && !it.usedAsCachedDoc
                    && it != AEDES_PROPS_TBL
                    && (null == it.validUntil || it.validUntil >= dataYear )
            }

		@JvmStatic
		fun getApplicableMandatoryTypes(type: XbrlDocumentType, status: XbrlDocumentStatus): Set<XbrlAttachmentType> =
			entries.filter {
				it.xbrlDocTypes.contains(type)
					&& it.status == status
					&& it.isMandatory
			}.toSet()

		@JvmStatic
		fun getEditableAttachmentTypes(type: XbrlDocumentType): Set<XbrlAttachmentType> {
			return entries.filter { type in it.xbrlDocTypes && !it.usedAsCachedDoc }.toSet()
				.plus(EDITABLE_ATTACHMENT_TYPES_OVERRIDE[type].orEmpty())
		}

		@JvmStatic
		fun getApplicableTypesByDocTypeOnly(type: XbrlDocumentType): List<XbrlAttachmentType> {
			return entries.filter { it.xbrlDocTypes.contains(type) }
		}

        @JvmStatic
        fun getDeletableTypes(
			type: XbrlDocumentType,
	        status: XbrlDocumentStatus,
	        dataYear: Int
        ): List<XbrlAttachmentType> = entries.filter {
            it.status <= status
                    && !it.usedAsCachedDoc
                    && it.xbrlDocTypes.contains(type)
                    && it !in listOf(WOZ_PROPERTY_TBL, AEDES_PROPS_TBL)
                    && (null == it.validUntil || it.validUntil >= dataYear)
        }

		fun fromSlug(slug: String): XbrlAttachmentType? {
			return try {
                valueOf(
                    slug.replace("-", "_").uppercase()
                )
			} catch (_: IllegalArgumentException) {
				null
			}
		}
	}
}
