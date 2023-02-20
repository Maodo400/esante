package sn.esp.sante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.esp.sante.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	  User findByEmail(String email);
	  User findById(int id);
	  User findByConfirmationToken(String confirmationToken);
	}