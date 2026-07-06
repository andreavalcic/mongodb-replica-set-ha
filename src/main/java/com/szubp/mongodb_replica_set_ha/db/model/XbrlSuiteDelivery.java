package com.szubp.mongodb_replica_set_ha.db.model;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Embedded subdocument - lives inside XbrlReportSuite.deliveryList list.
 * No @Document annotation: stored as a nested BSON array element.
 *
 * Key migration changes:
 *   - @ManyToOne xbrlSuite removed  (embedding implies parent ownership)
 *   - @OneToMany statuses replaced by plain List<XbrlSuiteDeliveryStatus>
 *   - @Fetch(FetchMode.SUBSELECT) removed  (Hibernate-specific, irrelevant for MongoDB)
 */
public class XbrlSuiteDelivery extends BaseDocument {

    @Field("sendertype")   private String sendertype;
    @Field("sendernum")    private String sendernum;
    @Field("receivertype") private String receivertype;
    @Field("receivernum")  private String receivernum;
    @Field("receiverdesc") private String receiverdesc;
    @Field("messagetype")  private String messagetype;
    @Field("messageid")    private String messageid;
    @Field("sent_time")    private Date   sentTimestamp;
    @Field("active")       private boolean active;

    /**
     * Replaces @OneToMany(mappedBy=…) from JPA.
     * Spring Data MongoDB stores this list as an embedded BSON array
     * inside the parent XbrlSuiteDelivery sub-document.
     */
    @Field("statuses")
    @NonNull
    private List<XbrlSuiteDeliveryStatus> statuses = new ArrayList<>();

    public String  getSendertype()                { return sendertype; }
    public void    setSendertype(String v)         { sendertype = v; }

    public String  getSendernum()                 { return sendernum; }
    public void    setSendernum(String v)          { sendernum = v; }

    public String  getReceivertype()              { return receivertype; }
    public void    setReceivertype(String v)       { receivertype = v; }

    public String  getReceivernum()               { return receivernum; }
    public void    setReceivernum(String v)        { receivernum = v; }

    public String  getReceiverdesc()              { return receiverdesc; }
    public void    setReceiverdesc(String v)       { receiverdesc = v; }

    public String  getMessagetype()               { return messagetype; }
    public void    setMessagetype(String v)        { messagetype = v; }

    public String  getMessageid()                 { return messageid; }
    public void    setMessageid(String v)          { messageid = v; }

    public Date    getSentTimestamp()             { return sentTimestamp; }
    public void    setSentTimestamp(Date v)        { sentTimestamp = v; }

    public boolean isActive()                     { return active; }
    public void    setActive(boolean v)            { active = v; }

    @NonNull
    public List<XbrlSuiteDeliveryStatus> getStatuses()               { return statuses; }
    public void setStatuses(@NonNull List<XbrlSuiteDeliveryStatus> v) { statuses = v; }

    @Override
    public String toString() { return getAuditIdentity(); }

    public String getAuditIdentity() { return getMessageid(); }
}
