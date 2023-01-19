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

import sn.esp.sante.model.Medecin;
import sn.esp.sante.service.MedecinService;

@Controller
@RequestMapping("/medecins")
public class MedecinController {
	@Autowired
	private MedecinService medecinService;
	
	// display list of Medecins
	@GetMapping("/")
	public String viewHomePage(Model model) {
		return findPaginated(1, "nom", "asc", model);		
	}
	
	@GetMapping("/add_medecin")
	public String showNewMedecinForm(Model model) {
		// create model attribute to bind form data
		Medecin medecin = new Medecin();
		model.addAttribute("medecin", medecin);
		return "new_medecin";
	}
	
	@PostMapping("/save_medecin")
	public String saveMedecin(@ModelAttribute("medecin") Medecin medecin) {
		// save Medecin to database
		medecinService.saveMedecin(medecin);
		return "redirect:/";
	}
	
	@GetMapping("/update_medecin/{id}")
	public String showFormForUpdate(@PathVariable ( value = "id") long id, Model model) {
		
		// get Medecin from the service
		Medecin medecin = medecinService.getMedecinById(id);
		
		// set Medecin as a model attribute to pre-populate the form
		model.addAttribute("medecin", medecin);
		return "update_medecin";
	}
	
	@GetMapping("/delete_medecin/{id}")
	public String deleteMedecin(@PathVariable (value = "id") long id) {
		
		// call delete Medecin method 
		this.medecinService.deleteMedecinById(id);
		return "redirect:/";
	}
	
	
	@GetMapping("/page/{pageNo}")
	public String findPaginated(@PathVariable (value = "pageNo") int pageNo, 
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			Model model) {
		int pageSize = 5;
		
		Page<Medecin> page = medecinService.findPaginated(pageNo, pageSize, sortField, sortDir);
		List<Medecin> listMedecins = page.getContent();
		
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		model.addAttribute("list_medecins", listMedecins);
		return "index";
	}
}

