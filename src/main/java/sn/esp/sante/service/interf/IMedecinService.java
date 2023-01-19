package sn.esp.sante.service.interf;

import java.util.List;

import org.springframework.data.domain.Page;

import sn.esp.sante.model.Medecin;

public interface IMedecinService {
	List<Medecin> getAllMedecins();
	void saveMedecin(Medecin medecin);
	Medecin getMedecinById(long id);
	void deleteMedecinById(long id);
	Page<Medecin> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);

}
