package com.szubp.mongodb_replica_set_ha.db.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.lang.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "xbr_report_suite_delivery")
public class XbrlSuiteDelivery extends BaseEntity {

	private XbrlReportSuite m_xbrlSuite;

	private String m_sendertype;

	private String m_sendernum;

	private String m_receivertype;

	private String m_receivernum;

	private String m_receiverdesc;

	private String m_messagetype;

	private String m_messageid;

	private Date m_sentTimestamp;

	private boolean m_active;

	@NonNull
	private List<XbrlSuiteDeliveryStatus> m_statusList = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "xbrlsuiteid")
	public XbrlReportSuite getXbrlSuite() {
		return m_xbrlSuite;
	}

	public void setXbrlSuite(XbrlReportSuite value) {
		m_xbrlSuite = value;
	}

	@Column(name = "sendertype", length = 35, nullable = false)
	public String getSendertype() {
		return m_sendertype;
	}

	public void setSendertype(String value) {
		m_sendertype = value;
	}

	@Column(name = "sendernum", length = 20, nullable = false)
	public String getSendernum() {
		return m_sendernum;
	}

	public void setSendernum(String value) {
		m_sendernum = value;
	}

	@Column(name = "receivertype", length = 35, nullable = false)
	public String getReceivertype() {
		return m_receivertype;
	}

	public void setReceivertype(String value) {
		m_receivertype = value;
	}

	@Column(name = "receivernum", length = 20, nullable = false)
	public String getReceivernum() {
		return m_receivernum;
	}

	public void setReceivernum(String value) {
		m_receivernum = value;
	}

	@Column(name = "receiverdesc", length = 256, nullable = false)
	public String getReceiverdesc() {
		return m_receiverdesc;
	}

	public void setReceiverdesc(String value) {
		m_receiverdesc = value;
	}

	@Column(name = "messagetype", length = 64, nullable = false)
	public String getMessagetype() {
		return m_messagetype;
	}

	public void setMessagetype(String value) {
		m_messagetype = value;
	}

	@Column(name = "messageid", length = 40, nullable = false)
	public String getMessageid() {
		return m_messageid;
	}

	public void setMessageid(String value) {
		m_messageid = value;
	}

	@Column(name = "sent_time", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getSentTimestamp() {
		return m_sentTimestamp;
	}

	public void setSentTimestamp(Date value) {
		m_sentTimestamp = value;
	}

	@Column(name = "active", nullable = false)
	public boolean isActive() {
		return m_active;
	}

	public void setActive(boolean value) {
		m_active = value;
	}

	@NonNull
	@OneToMany(fetch = FetchType.LAZY, mappedBy = XbrlSuiteDeliveryStatus.pXBRL_SUITE_DELIVERY)
	@Fetch(FetchMode.SUBSELECT)
	public List<XbrlSuiteDeliveryStatus> getStatuses() {
		return m_statusList;
	}

	public void setStatuses(@NonNull List<XbrlSuiteDeliveryStatus> value) {
		m_statusList = value;
	}

	@Override
	public String toString() {
		return getAuditIdentity();
	}

	@NonNull
	@Transient
	public String getAuditIdentity() {
		return getMessageid();
	}
}
