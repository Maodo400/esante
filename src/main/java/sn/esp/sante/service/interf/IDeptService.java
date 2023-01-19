package sn.esp.sante.service.interf;

import java.util.List;

import org.springframework.data.domain.Page;

import sn.esp.sante.model.Departement;

public interface IDeptService {
	List<Departement> getAllDepts();
	void saveDept(Departement Dept);
	Departement getDeptById(long id);
	void deleteDeptById(long id);
	Page<Departement> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);

}
