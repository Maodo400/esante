package sn.esp.sante.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import sn.esp.sante.model.Medecin;
import sn.esp.sante.repository.MedecinRepository;
import sn.esp.sante.service.interf.IMedecinService;

@Service
public class MedecinService implements IMedecinService{
	@Autowired
	private MedecinRepository medecinRepository;

	@Override
	public List<Medecin> getAllMedecins() {
		return medecinRepository.findAll();
	}

	@Override
	public void saveMedecin(Medecin medecin) {
		this.medecinRepository.save(medecin);
	}

	@Override
	public Medecin getMedecinById(long id) {
		Optional<Medecin> optional = medecinRepository.findById(id);
		Medecin medecin = null;
		if (optional.isPresent()) {
			medecin = optional.get();
		} else {
			throw new RuntimeException(" medecin not found for id :: " + id);
		}
		return medecin;
	}

	@Override
	public void deleteMedecinById(long id) {
		this.medecinRepository.deleteById(id);
	}

	@Override
	public Page<Medecin> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.medecinRepository.findAll(pageable);
	}
}
