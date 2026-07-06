package com.szubp.mongodb_replica_set_ha.db.model;

public enum XbrlDocumentType {
    DVI("dVi", "de-verantwoordingsinformatie"),
    DPI("dPi", "de-prospectieve-informatie"),
    TAX("n/a", "n/a"),
    AEDES("aedes", "aedes-benchmark"),
    KVK("KvK", "jaarverantwoording"),
    ;

    private String m_key;
    private String m_partName;

    XbrlDocumentType(String aKey, String aPartName) {
        m_key = aKey;
        m_partName = aPartName;
    }

    public String getKey() {
        return m_key;
    }

    public void setKey(String key) {
        m_key = key;
    }

    public String getPartName() {
        return m_partName;
    }

    public void setPartName(String partName) {
        m_partName = partName;
    }
}

