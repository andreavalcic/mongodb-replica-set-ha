package com.szubp.mongodb_replica_set_ha.db.model;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;;

@MappedSuperclass
@Access(AccessType.PROPERTY)
public abstract class BaseEntity {

	private String m_id;

	@Id
	@UidGenerated
	@Column(length = 23, nullable = false, updatable = false)
	public String getId() {
		return m_id;
	}

	public void setId(String id) {
		this.m_id = id;
	}
}
