package com.szubp.mongodb_replica_set_ha.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import java.util.Date;

@Entity
@Table(name = "xbr_report_suite_delivery_status")
public class XbrlSuiteDeliveryStatus extends BaseEntity {

	private XbrlSuiteDelivery m_xbrlSuiteDelivery;

	public static final String pXBRL_SUITE_DELIVERY = "xbrlSuiteDelivery";

	private String m_messageid;

	private Date m_datetime;

	public static final String pDATETIME = "datetime";

	private String m_code;
	public static final String pCODE = "code";

	private String m_desc;
	public static final String pDESC = "desc";

	private String m_details;
	public static final String pDETAILS = "details";

	private String m_errorCode;
	public static final String pERRORCODE = "errorCode";

	private String m_errorDetails;
	public static final String pERRORDETAILS = "errorDetails";

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "xbrldeliveryid")
	public XbrlSuiteDelivery getXbrlSuiteDelivery() {
		return m_xbrlSuiteDelivery;
	}

	public void setXbrlSuiteDelivery(XbrlSuiteDelivery value) {
		m_xbrlSuiteDelivery = value;
	}

	@Column(name = "messageid", length = 40, nullable = false)
	public String getMessageid() {
		return m_messageid;
	}

	public void setMessageid(String value) {
		m_messageid = value;
	}

	@Column(name = "status_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDatetime() {
		return m_datetime;
	}

	public void setDatetime(Date value) {
		m_datetime = value;
	}

	@Column(name = "status_code", length = 20, nullable = false)
	public String getCode() {
		return m_code;
	}

	public void setCode(String value) {
		m_code = value;
	}

	@Column(name = "status_desc", length = 80, nullable = true)
	public String getDesc() {
		return m_desc;
	}

	public void setDesc(String value) {
		m_desc = value;
	}

	@Column(name = "status_details", length = 10240000, nullable = true)
	public String getDetails() {
		return m_details;
	}

	public void setDetails(String value) {
		m_details = value;
	}

	@Column(name = "error_code", length = 20, nullable = true)
	public String getErrorCode() {
		return m_errorCode;
	}

	public void setErrorCode(String value) {
		m_errorCode = value;
	}

	@Column(name = "error_details", length = 10240000, nullable = true)
	public String getErrorDetails() {
		return m_errorDetails;
	}

	public void setErrorDetails(String value) {
		m_errorDetails = value;
	}

	@Override
	public String toString() {
		return getAuditIdentity();
	}

	@Transient
	public String getAuditIdentity() {
		String part = getMessageid() + ": status " + getCode();
		if(null != getErrorCode()) {
			part += ", error " + getErrorCode();
		}
		return part;
	}
}
