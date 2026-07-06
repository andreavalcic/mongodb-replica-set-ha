package com.szubp.mongodb_replica_set_ha.repository;

import com.szubp.mongodb_replica_set_ha.db.model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, String> {
    @Query("SELECT u FROM AuthUser u WHERE u.email = :email")
    Optional<AuthUser> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM AuthUser u WHERE u.email = :email AND u.kvknumber = :kvknumber")
    Optional<AuthUser> findByEmailAndKvknumber(@Param("email") String email, @Param("kvknumber") String kvknumber);
}
