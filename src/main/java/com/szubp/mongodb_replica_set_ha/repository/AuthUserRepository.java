package com.szubp.mongodb_replica_set_ha.repository;

import com.szubp.mongodb_replica_set_ha.db.model.AuthUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Replaces JpaRepository<AuthUser, String>.
 * Spring Data MongoDB derives findByEmail and findByEmailAndKvknumber
 * from the method names - no @Query needed for simple field lookups.
 */
public interface AuthUserRepository extends MongoRepository<AuthUser, String> {

    Optional<AuthUser> findByEmail(String email);

    Optional<AuthUser> findByEmailAndKvknumber(String email, String kvknumber);
}
