package com.szubp.mongodb_replica_set_ha.db.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "auth_user")
@Access(AccessType.PROPERTY)
public class AuthUser extends BaseEntity implements Serializable {

	private String m_email;
	private String m_fullname;
	private String m_kvknumber;

	@Column(name = "email", nullable = false, unique = true, length = 255)
	public String getEmail() {
		return m_email;
	}

	public void setEmail(String email) {
		this.m_email = email;
	}

	@Column(name = "fullname", nullable = false, length = 255)
	public String getFullname() {
		return m_fullname;
	}

	public void setFullname(String fullname) {
		this.m_fullname = fullname;
	}

	@Column(name = "kvknumber", nullable = false, length = 8)
	public String getKvknumber() {
		return m_kvknumber;
	}

	public void setKvknumber(String kvknumber) {
		this.m_kvknumber = kvknumber;
	}
}