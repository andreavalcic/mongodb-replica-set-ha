package com.szubp.mongodb_replica_set_ha.db.model;

import com.szubp.mongodb_replica_set_ha.util.WrappedException;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Root aggregate document for the xbrl_report_suites collection.
 *
 * Key migration changes vs JPA entity:
 *   @Entity / @Table  →  @Document(collection = "xbrl_report_suites")
 *
 *   @ManyToOne AuthUser fields  →  @DBRef
 *     MongoDB stores a { $ref, $id } pointer.  AuthUser documents live in
 *     their own collection; this preserves referential identity without
 *     denormalising user data into every suite.
 *
 *   @OneToMany(cascade = ALL) attachments / deliveryList  →  plain List<>
 *     These were owned children with orphanRemoval in JPA.  In MongoDB the
 *     same ownership semantics are expressed by embedding: the lists are stored
 *     as nested BSON arrays inside this document.  No separate collection, no
 *     foreign key, no join - the entire aggregate is one atomic unit.
 *
 *   @Temporal, @Column, @Enumerated, @Lob  →  removed
 *     MongoDB serialises Date, String enums, and byte[] natively.
 *
 *   Import fix: EntryPoint was imported from the wrong package
 *     (com.szubp…model vs com.szubp…db.model).  Corrected here.
 */
@Document(collection = "xbrl_report_suites")
public class XbrlReportSuite extends BaseDocument {

    @Field("kvknumber")
    @Indexed
    private String kvknumber;

    @Field("name")       private String            name;
    @Field("entry_point") private String            entryPoint;
    @Field("xbrl")       private String            xbrl;
    @Field("status")     private XbrlDocumentStatus status    = XbrlDocumentStatus.ACTIVE;
    @Field("created_at") private Date              createdAt  = new Date();
    @Field("modified_at") private Date             modifiedAt;
    @Field("sent_at")    private Date              sentAt;
    @Field("completed_at") private Date            completedAt;
    @Field("type")       private XbrlDocumentType  type;
    @Field("data_year")  private Integer           dataYear;

    // @ManyToOne → @DBRef: MongoDB stores { $ref: "auth_users", $id: "..." }
    @DBRef @Field("created_by")   private AuthUser createdBy;
    @DBRef @Field("modified_by")  private AuthUser modifiedBy;
    @DBRef @Field("completed_by") private AuthUser completedBy;
    @DBRef @Field("sent_by")      private AuthUser sentBy;

    // @OneToMany(cascade = ALL) → embedded BSON arrays
    @Field("attachments")   private List<XbrlAttachment>   attachments  = new ArrayList<>();
    @Field("delivery_list") private List<XbrlSuiteDelivery> deliveryList = new ArrayList<>();

    // ------------------------------------------------------------------
    // Inner type - unchanged business logic
    // ------------------------------------------------------------------

    public static class DataYearAndXbrlType {
        public final Integer dataYear;
        public final XbrlDocumentType xbrlType;

        public DataYearAndXbrlType(Integer dataYear, XbrlDocumentType xbrlType) {
            this.dataYear = dataYear;
            this.xbrlType = xbrlType;
        }
    }

    @Nullable
    public static DataYearAndXbrlType resolveYearAndType(String entryPointHref) {
        if (entryPointHref != null) {
            try {
                EntryPoint ep = EntryPoint.create(new URI(entryPointHref));
                Integer dataYear = ep.getYear();
                if (ep instanceof EntryPoint.DviEntryPoint dvi) {
                    return new DataYearAndXbrlType(dataYear, dvi.getXbrlType());
                }
            } catch (Exception ex) {
                throw WrappedException.wrap(ex);
            }
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Getters / Setters
    // ------------------------------------------------------------------

    public String            getKvknumber()              { return kvknumber; }
    public void              setKvknumber(String v)      { kvknumber = v; }

    public String            getName()                   { return name; }
    public void              setName(String v)           { name = v; }

    public String            getEntryPoint()             { return entryPoint; }
    public void              setEntryPoint(String v)     { entryPoint = v; }

    public String            getXbrl()                   { return xbrl; }
    public void              setXbrl(String v)           { xbrl = v; }

    public XbrlDocumentStatus getStatus()               { return status; }
    public void              setStatus(XbrlDocumentStatus v) { status = v; }

    public Date              getCreatedAt()              { return createdAt; }
    public void              setCreatedAt(Date v)        { createdAt = v; }

    public Date              getModifiedAt()             { return modifiedAt; }
    public void              setModifiedAt(Date v)       { modifiedAt = v; }

    public Date              getSentAt()                 { return sentAt; }
    public void              setSentAt(Date v)           { sentAt = v; }

    public Date              getCompletedAt()            { return completedAt; }
    public void              setCompletedAt(Date v)      { completedAt = v; }

    public AuthUser          getCreatedBy()              { return createdBy; }
    public void              setCreatedBy(AuthUser v)    { createdBy = v; }

    public AuthUser          getModifiedBy()             { return modifiedBy; }
    public void              setModifiedBy(AuthUser v)   { modifiedBy = v; }

    public AuthUser          getCompletedBy()            { return completedBy; }
    public void              setCompletedBy(AuthUser v)  { completedBy = v; }

    public AuthUser          getSentBy()                 { return sentBy; }
    public void              setSentBy(AuthUser v)       { sentBy = v; }

    public XbrlDocumentType  getType()                   { return type; }
    public void              setType(XbrlDocumentType v) { type = v; }

    public Integer           getDataYear()               { return dataYear; }
    public void              setDataYear(Integer v)      { dataYear = v; }

    public List<XbrlAttachment>    getAttachments()               { return attachments; }
    public void                    setAttachments(List<XbrlAttachment> v) { attachments = v; }

    public List<XbrlSuiteDelivery> getDeliveryList()              { return deliveryList; }
    public void                    setDeliveryList(List<XbrlSuiteDelivery> v) { deliveryList = v; }

    // ------------------------------------------------------------------
    // Business logic - unchanged
    // ------------------------------------------------------------------

    public boolean canBeApproved() {
        if (status != XbrlDocumentStatus.COMPLETED) return false;
        Set<XbrlAttachmentType> required = XbrlAttachmentType.getApplicableMandatoryTypes(type, status);
        if (required.isEmpty()) return false;
        Set<XbrlAttachmentType> present = attachments.stream()
                .map(XbrlAttachment::getType)
                .collect(Collectors.toSet());
        return present.containsAll(required);
    }
}
