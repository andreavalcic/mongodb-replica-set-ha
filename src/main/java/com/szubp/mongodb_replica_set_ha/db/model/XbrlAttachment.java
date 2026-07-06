package com.szubp.mongodb_replica_set_ha.db.model;

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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import java.util.Date;

@Entity
@Table(name = "dvi_attachment", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"suiteid", "type"})
})
@Access(AccessType.PROPERTY)
public class XbrlAttachment extends BaseEntity {

	private String m_name;
	private XbrlAttachmentType m_type;
	private String m_mimeType;
	private String m_sha1;
	private String m_xbrlCsvSha256;
	private byte[] m_file;
	private Long m_size;
	private XbrlReportSuite m_suite;
	private Date m_createdAt;
	private AuthUser m_createdBy;

	@Column(name = "name", nullable = false, length = 255)
	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		this.m_name = name;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, length = 32)
	public XbrlAttachmentType getType() {
		return m_type;
	}

	public void setType(XbrlAttachmentType type) {
		this.m_type = type;
	}

	@Column(name = "mimetype", length = 32, nullable = false)
	public String getMimeType() {
		return m_mimeType;
	}

	public void setMimeType(String mimeType) {
		m_mimeType = mimeType;
	}

	@Column(name = "sha1", length = 40, nullable = false)
	public String getSha1() {
		return m_sha1;
	}

	public void setSha1(String value) {
		m_sha1 = value;
	}

	@Column(name = "xbrl_csv_sha256", length = 64)
	public String getXbrlCsvSha256() {
		return m_xbrlCsvSha256;
	}

	public void setXbrlCsvSha256(String value) {
		m_xbrlCsvSha256 = value;
	}

	@Lob
	@Column(name = "file", nullable = false)
	public byte[] getFile() {
		return m_file;
	}

	public void setFile(byte[] file) {
		this.m_file = file;
	}

	@Column(name = "size", nullable = false)
	public Long getSize() {
		return m_size;
	}

	public void setSize(Long size) {
		this.m_size = size;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "suiteid", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_attachment_suite"))
	public XbrlReportSuite getSuite() {
		return m_suite;
	}

	public void setSuite(XbrlReportSuite suite) {
		this.m_suite = suite;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at")
	public Date getCreatedAt() {
		return m_createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.m_createdAt = createdAt;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_attachment_created_by"))
	public AuthUser getCreatedBy() {
		return m_createdBy;
	}

	public void setCreatedBy(AuthUser createdBy) {
		this.m_createdBy = createdBy;
	}
}

