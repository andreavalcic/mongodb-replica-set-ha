package com.szubp.mongodb_replica_set_ha.db.model;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * Embedded subdocument - lives inside XbrlSuiteDelivery.statuses list.
 * No @Document annotation: MongoDB stores this as a nested BSON object,
 * not a separate collection.
 * Back-reference to XbrlSuiteDelivery is removed because embedding
 * implies the ownership relationship (no FK needed).
 */
public class XbrlSuiteDeliveryStatus extends BaseDocument {

    public static final String pXBRL_SUITE_DELIVERY = "xbrlSuiteDelivery"; // kept for SuiteDeliveryStatus.kt compat
    public static final String pDATETIME    = "datetime";
    public static final String pCODE        = "code";
    public static final String pDESC        = "desc";
    public static final String pDETAILS     = "details";
    public static final String pERRORCODE   = "errorCode";
    public static final String pERRORDETAILS = "errorDetails";

    @Field("messageid")    private String messageid;
    @Field("datetime")     private Date   datetime;
    @Field("code")         private String code;
    @Field("desc")         private String desc;
    @Field("details")      private String details;
    @Field("errorCode")    private String errorCode;
    @Field("errorDetails") private String errorDetails;

    public String getMessageid()           { return messageid; }
    public void   setMessageid(String v)   { messageid = v; }

    public Date   getDatetime()            { return datetime; }
    public void   setDatetime(Date v)      { datetime = v; }

    public String getCode()                { return code; }
    public void   setCode(String v)        { code = v; }

    public String getDesc()                { return desc; }
    public void   setDesc(String v)        { desc = v; }

    public String getDetails()             { return details; }
    public void   setDetails(String v)     { details = v; }

    public String getErrorCode()           { return errorCode; }
    public void   setErrorCode(String v)   { errorCode = v; }

    public String getErrorDetails()        { return errorDetails; }
    public void   setErrorDetails(String v){ errorDetails = v; }

    @Override
    public String toString() { return getAuditIdentity(); }

    public String getAuditIdentity() {
        String part = getMessageid() + ": status " + getCode();
        if (getErrorCode() != null) part += ", error " + getErrorCode();
        return part;
    }
}
