package sn.esp.sante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.esp.sante.model.Medecin;

public interface MedecinRepository extends JpaRepository<Medecin, Long> {

}
