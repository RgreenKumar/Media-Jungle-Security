package com.VsmartEngine.MediaJungle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.VsmartEngine.MediaJungle.model.AddUser;

public interface AddUserRepository extends JpaRepository<AddUser, Long>{
	@Query("SELECT u FROM AddUser u WHERE u.Username = ?1")
    Optional<AddUser> findByUsername(String Username);
	@Query("SELECT u FROM AddUser u WHERE u.email = ?1")
    Optional<AddUser> findByEmail(String email);
	Optional<AddUser> findFirstByRole(String role);

	default Optional<AddUser> findByRole(String role) {
		return findFirstByRole(role);
	}

	java.util.List<AddUser> findAllByRole(String role);
}
