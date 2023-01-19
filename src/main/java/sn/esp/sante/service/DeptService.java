package sn.esp.sante.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import sn.esp.sante.model.Departement;
import sn.esp.sante.repository.DepartementRepository;
import sn.esp.sante.service.interf.IDeptService;

@Service
public class DeptService implements IDeptService{
	@Autowired
	private DepartementRepository deptRepository;

	@Override
	public List<Departement> getAllDepts() {
		return deptRepository.findAll();
	}

	@Override
	public void saveDept(Departement Dept) {
		this.deptRepository.save(Dept);
	}

	@Override
	public Departement getDeptById(long id) {
		Optional<Departement> optional = deptRepository.findById(id);
		Departement Dept = null;
		if (optional.isPresent()) {
			Dept = optional.get();
		} else {
			throw new RuntimeException(" Dept not found for id :: " + id);
		}
		return Dept;
	}

	@Override
	public void deleteDeptById(long id) {
		this.deptRepository.deleteById(id);
	}

	@Override
	public Page<Departement> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.deptRepository.findAll(pageable);
	}
}
