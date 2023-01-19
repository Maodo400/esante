package sn.esp.sante.service.interf;

import java.util.List;

import org.springframework.data.domain.Page;

import sn.esp.sante.model.DossierMed;

public interface IDossierMed {
	List<DossierMed> getAllDossierMeds();
	void saveDossierMed(DossierMed medecin);
	DossierMed getDossierMedById(long id);
	void deleteDossierMedById(long id);
	Page<DossierMed> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);

}
