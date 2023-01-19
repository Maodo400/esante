package sn.esp.sante.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.esp.sante.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	  Optional<User> findByEmail(String email);
	  User findByConfirmationToken(String confirmationToken);
	}