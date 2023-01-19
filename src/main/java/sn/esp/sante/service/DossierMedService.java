package sn.esp.sante.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import sn.esp.sante.model.DossierMed;
import sn.esp.sante.repository.DossierMedRepository;
import sn.esp.sante.service.interf.IDossierMed;

@Service
public class DossierMedService implements IDossierMed {
	@Autowired
	private DossierMedRepository dossierRepository;

	@Override
	public List<DossierMed> getAllDossierMeds() {
		return dossierRepository.findAll();
	}

	@Override
	public void saveDossierMed(DossierMed dossierMed) {
		this.dossierRepository.save(dossierMed);
	}

	@Override
	public DossierMed getDossierMedById(long id) {
		Optional<DossierMed> optional = dossierRepository.findById(id);
		DossierMed DossierMed = null;
		if (optional.isPresent()) {
			DossierMed = optional.get();
		} else {
			throw new RuntimeException(" DossierMed not found for id :: " + id);
		}
		return DossierMed;
	}

	@Override
	public void deleteDossierMedById(long id) {
		this.dossierRepository.deleteById(id);
	}

	@Override
	public Page<DossierMed> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.dossierRepository.findAll(pageable);
	}
}
