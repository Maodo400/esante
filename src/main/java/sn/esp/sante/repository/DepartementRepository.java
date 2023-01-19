package sn.esp.sante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.esp.sante.model.Departement;

public interface DepartementRepository extends JpaRepository<Departement, Long> {}