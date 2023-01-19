package sn.esp.sante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.esp.sante.model.DossierMed;

public interface DossierMedRepository extends JpaRepository<DossierMed, Long> {

}
