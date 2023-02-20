package sn.esp.sante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.esp.sante.model.DossierMed;
import sn.esp.sante.model.User;

public interface DossierMedRepository extends JpaRepository<DossierMed, Long> {
	
	DossierMed findByUser(User user);
}
