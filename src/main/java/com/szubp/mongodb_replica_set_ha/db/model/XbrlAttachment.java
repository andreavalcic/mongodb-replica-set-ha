package com.szubp.mongodb_replica_set_ha.db.model;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * Embedded subdocument - lives inside XbrlReportSuite.attachments list.
 * No @Document annotation: stored inline in the parent suite document.
 *
 * Key migration changes:
 *   - @ManyToOne suite (back-ref) removed  (embedding implies ownership)
 *   - @ManyToOne createdBy → @DBRef        (cross-collection reference preserved)
 *   - @Lob removed                          (MongoDB stores byte[] as BinData natively)
 *   - @Temporal, @Column, @Enumerated removed (MongoDB handles dates/enums/blobs natively)
 */
public class XbrlAttachment extends BaseDocument {

    @Field("name")            private String            name;
    @Field("type")            private XbrlAttachmentType type;
    @Field("mimetype")        private String            mimeType;
    @Field("sha1")            private String            sha1;
    @Field("xbrl_csv_sha256") private String            xbrlCsvSha256;
    @Field("file")            private byte[]            file;
    @Field("size")            private Long              size;
    @Field("created_at")      private Date              createdAt;

    /**
     * @DBRef stores a { $ref, $id } pointer to the auth_users collection.
     * The MongoDB Java driver resolves this lazily, mirroring FetchType.LAZY.
     */
    @DBRef
    @Field("created_by")
    private AuthUser createdBy;

    public String             getName()             { return name; }
    public void               setName(String v)     { name = v; }

    public XbrlAttachmentType getType()             { return type; }
    public void               setType(XbrlAttachmentType v) { type = v; }

    public String             getMimeType()         { return mimeType; }
    public void               setMimeType(String v) { mimeType = v; }

    public String             getSha1()             { return sha1; }
    public void               setSha1(String v)     { sha1 = v; }

    public String             getXbrlCsvSha256()    { return xbrlCsvSha256; }
    public void               setXbrlCsvSha256(String v) { xbrlCsvSha256 = v; }

    public byte[]             getFile()             { return file; }
    public void               setFile(byte[] v)     { file = v; }

    public Long               getSize()             { return size; }
    public void               setSize(Long v)       { size = v; }

    public Date               getCreatedAt()        { return createdAt; }
    public void               setCreatedAt(Date v)  { createdAt = v; }

    public AuthUser           getCreatedBy()        { return createdBy; }
    public void               setCreatedBy(AuthUser v) { createdBy = v; }
}
