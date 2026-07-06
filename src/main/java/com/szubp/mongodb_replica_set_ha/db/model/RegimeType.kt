package com.szubp.mongodb_replica_set_ha.db.model

enum class RegimeType(val documentType: XbrlDocumentType, val value: String, val code: String, val allowedForDviDatavaultConnection: Boolean = true) {
	//DVI
	ADMINISTRATIEVE_SCHEIDING(XbrlDocumentType.DVI, "administratieve-scheiding", "ADM-ENK"),
	ADMINISTRATIEVE_SCHEIDING_GECONSOLIDEERD(XbrlDocumentType.DVI, "administratieve-scheiding-geconsolideerd", "ADM-CON"),
	VERLICHT_REGIME(XbrlDocumentType.DVI, "verlicht-regime", "VER-ENK"),
	VERLICHT_REGIME_GECONSOLIDEERD(XbrlDocumentType.DVI, "verlicht-regime-geconsolideerd", "VER-CON"),
	HYBRIDE_SCHEIDING(XbrlDocumentType.DVI, "hybride-scheiding", "HYB"),
	JURIDISCHE_SCHEIDING(XbrlDocumentType.DVI, "juridische-scheiding", "JUR"),
	//KVK
	GOVAUTH_REALESTATE(XbrlDocumentType.KVK, "nlgaap-toegelaten-instellingen-volkshuisvesting", "NLGAAP-REALESTATE"),
	GOVAUTH_REALESTATE_SMALL(XbrlDocumentType.KVK, "nlgaap-klein", "NLGAAP-SMALL", false),
}
