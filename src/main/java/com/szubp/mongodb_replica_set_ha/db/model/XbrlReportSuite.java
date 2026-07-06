package com.szubp.mongodb_replica_set_ha.db.model;

import com.szubp.mongodb_replica_set_ha.model.EntryPoint;
import com.szubp.mongodb_replica_set_ha.model.EntryPoint.DviEntryPoint;
import com.szubp.mongodb_replica_set_ha.util.WrappedException;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "dvi_report_suite")
@Access(AccessType.PROPERTY)
public class XbrlReportSuite extends BaseEntity {

	private String m_kvknumber;
	private String m_name;
	private String m_entryPoint;
	private String m_xbrl;
	private XbrlDocumentStatus m_status = XbrlDocumentStatus.ACTIVE;
	private Date m_createdAt = new Date();
	private Date m_modifiedAt;
	private Date m_sentAt;
	private AuthUser m_createdBy;
	private AuthUser m_modifiedBy;
	private Date m_completedAt;
	private AuthUser m_completedBy;
	private AuthUser m_sentBy;
	private XbrlDocumentType m_type;
	private Integer m_dataYear;
	private List<XbrlAttachment> m_attachments = new ArrayList<>();
	private List<XbrlSuiteDelivery> m_deliveryList = new ArrayList<>();

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
		if(entryPointHref != null) {
			try {
				EntryPoint entryPoint = EntryPoint.create(new URI(entryPointHref));
				Integer dataYear = entryPoint.getYear();
				if(entryPoint instanceof EntryPoint.DviEntryPoint) {
					return new DataYearAndXbrlType(dataYear, ((DviEntryPoint) entryPoint).getXbrlType());
				}
			} catch (Exception ex) {
				throw WrappedException.wrap(ex);
			}
		}
		return null;
	}

	@Column(name = "kvknumber", nullable = false, length = 8)
	public String getKvknumber() {
		return m_kvknumber;
	}

	public void setKvknumber(String kvknumber) {
		this.m_kvknumber = kvknumber;
	}

	@Column(name = "name", nullable = false, length = 255)
	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		this.m_name = name;
	}

	@Column(name = "entry_point", nullable = false, length = 512)
	public String getEntryPoint() {
		return m_entryPoint;
	}

	public void setEntryPoint(String entryPoint) {
		this.m_entryPoint = entryPoint;
	}

	@Column(name = "xbrl", length = 10240000, nullable = false)
	public String getXbrl() {
		return m_xbrl;
	}

	public void setXbrl(String xbrl) {
		this.m_xbrl = xbrl;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 16)
	public XbrlDocumentStatus getStatus() {
		return m_status;
	}

	public void setStatus(XbrlDocumentStatus status) {
		this.m_status = status;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at")
	public Date getCreatedAt() {
		return m_createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.m_createdAt = createdAt;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified_at")
	public Date getModifiedAt() {
		return m_modifiedAt;
	}

	public void setModifiedAt(Date modifiedAt) {
		this.m_modifiedAt = modifiedAt;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "completed_at")
	public Date getCompletedAt() {
		return m_completedAt;
	}

	public void setCompletedAt(Date completedAt) {
		this.m_completedAt = completedAt;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "completed_by", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name =
			"fk_suite_completed_by"))
	public AuthUser getCompletedBy() {
		return m_completedBy;
	}

	public void setCompletedBy(AuthUser completedBy) {
		this.m_completedBy = completedBy;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sent_at")
	public Date getSentAt() {
		return m_sentAt;
	}

	public void setSentAt(Date sentAt) {
		this.m_sentAt = sentAt;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_suite_created_by"))
	public AuthUser getCreatedBy() {
		return m_createdBy;
	}

	public void setCreatedBy(AuthUser createdBy) {
		this.m_createdBy = createdBy;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "modified_by", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_suite_modified_by"))
	public AuthUser getModifiedBy() {
		return m_modifiedBy;
	}

	public void setModifiedBy(AuthUser modifiedBy) {
		this.m_modifiedBy = modifiedBy;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sent_by", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_suite_sent_by"))
	public AuthUser getSentBy() {
		return m_sentBy;
	}

	public void setSentBy(AuthUser sentBy) {
		this.m_sentBy = sentBy;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, length = 16)
	public XbrlDocumentType getType() {
		return m_type;
	}

	public void setType(XbrlDocumentType type) {
		this.m_type = type;
	}

	@Column(name = "data_year", nullable = false)
	public Integer getDataYear() {
		return m_dataYear;
	}

	public void setDataYear(Integer dataYear) {
		this.m_dataYear = dataYear;
	}

	@OneToMany(mappedBy = "suite", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	public List<XbrlAttachment> getAttachments() {
		return m_attachments;
	}

	public void setAttachments(List<XbrlAttachment> attachments) {
		this.m_attachments = attachments;
	}

	@OneToMany(mappedBy = "xbrlSuite", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	public List<XbrlSuiteDelivery> getDeliveryList() {
		return m_deliveryList;
	}

	public void setDeliveryList(List<XbrlSuiteDelivery> deliveryList) {
		this.m_deliveryList = deliveryList;
	}

	public boolean canBeApproved() {
		if(m_status != XbrlDocumentStatus.COMPLETED) {
			return false;
		}
		Set<XbrlAttachmentType> requiredAttachments = XbrlAttachmentType.getApplicableMandatoryTypes(m_type, m_status);
		if(requiredAttachments.isEmpty()) {
			return false;
		}
		Set<XbrlAttachmentType> attachmentTypes = getAttachments().stream().map(XbrlAttachment::getType).collect(Collectors.toSet());
		return attachmentTypes.containsAll(requiredAttachments);
	}

}
