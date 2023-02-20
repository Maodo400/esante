package sn.esp.sante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.esp.sante.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	  Role findByRole(String role);
	    Role findById(int id);

	}