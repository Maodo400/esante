package sn.esp.sante.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sn.esp.sante.model.DossierMed;
import sn.esp.sante.model.User;
import sn.esp.sante.repository.UserRepository;
import sn.esp.sante.service.DossierMedService;
import sn.esp.sante.service.UserService;

@Controller
@RequestMapping("/dossiers")
public class DossierMedController {
	@Autowired
	private DossierMedService dossierService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	// display list of DossierMeds
	@GetMapping("/")
	public String viewHomePage(Model model) {
		return findPaginated(1, "id", "asc", model);		
	}
	
	@GetMapping("/add_dossier")
	public String showNewDossierMedForm(Model model) {
		// create model attribute to bind form data
		model.addAttribute("dossier", new DossierMed());
		model.addAttribute("user", userRepository.findAll());
		return "dossier/new_dossier";
	}
	
	@PostMapping("/save_dossier")
	public String saveDossierMed(@ModelAttribute("dossier") DossierMed dossier,
			@ModelAttribute("user") User user) {
		// save DossierMed to database
		user.setCreatedBy("ADMIN");
		dossier.setUser(user);
		dossierService.saveDossierMed(dossier);
		return "redirect:/dossiers/";
	}
	
	@GetMapping("/update_dossier/{id}")
	public String showFormForUpdate(@PathVariable ( value = "id") long id, Model model) {
		
		// get DossierMed from the service
		//DossierMed dossier = dossierService.getDossierMedById(id);
		
		// set DossierMed as a model attribute to pre-populate the form
		model.addAttribute("dossier", dossierService.getDossierMedById(id));
		//model.addAttribute("user", userService.getUserById(1));
		//model.addAttribute("dossier", dossier);
		return "dossier/update_dossier";
	}
	
	@GetMapping("/delete_dossier/{id}")
	public String deleteDossierMed(@PathVariable (value = "id") long id) {
		
		// call delete DossierMed method 
		this.dossierService.deleteDossierMedById(id);
		return "redirect:/dossiers/";
	}
	
	
	@GetMapping("/page/{pageNo}")
	public String findPaginated(@PathVariable (value = "pageNo") int pageNo, 
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			Model model) {
		int pageSize = 5;
		
		Page<DossierMed> page = dossierService.findPaginated(pageNo, pageSize, sortField, sortDir);
		List<DossierMed> listDossierMeds = page.getContent();
		
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		model.addAttribute("list_dossiers", listDossierMeds);
		return "dossier/index";
	}
}

