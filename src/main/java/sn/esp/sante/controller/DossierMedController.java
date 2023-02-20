package sn.esp.sante.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
	
    @ModelAttribute("loggedInUser")
    public User populateUserDetails(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = userService.findUserByEmail(auth.getName());
        model.addAttribute("isUser", userService.isUser(loggedInUser));
        model.addAttribute("isAdmin", userService.isAdmin(loggedInUser));
        model.addAttribute("isMedecin", userService.isMedecin(loggedInUser));
        return loggedInUser;
    }
	
	// display list of DossierMeds
	@GetMapping("/")
	public String viewHomePage(Model model, @ModelAttribute User loggedInUser) {
		populateUserDetails(model);

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
	public String saveDossierMed(@ModelAttribute("dossier") DossierMed dossier, Model model,
			@ModelAttribute("user")@Valid User user) {
		// save DossierMed to database
		User user1 = userService.getUserById(user.getId());
        dossier.setLastModifiedBy(populateUserDetails(model).getUsername());
		dossier.setCreatedBy(populateUserDetails(model).getUsername());
		dossier.setUser(user1);
		dossierService.saveDossierMed(dossier);
		return "redirect:/dossiers/";
	}
	
	@PostMapping("/save_dossier1")
	public String saveDossierMed1(@ModelAttribute("dossier") DossierMed dossier,@ModelAttribute("user1")@Valid User user) {
        dossier.setLastModifiedBy(user.getUsername());
		dossier.setCreatedBy(user.getUsername());
		dossierService.saveDossierMed(dossier);
		return "redirect:/dossiers/";
	}
	
//	@PostMapping("/save_dossier_update")
//	public String saveDossierMedUpdate(@ModelAttribute("dossier") DossierMed dossier,
//			@ModelAttribute("user") User user) {
//		// save DossierMed to database
//		dossierService.saveDossierMed(dossier);
//		return "redirect:/user/affiche/"+user.getId();
//	}
//	
//	@GetMapping("/update_dossier_update/{id}")
//	public String showFormForUpdateu(@PathVariable ( value = "id") long id, Model model) {
//		
//		// get DossierMed from the service
//		//DossierMed dossier = dossierService.getDossierMedById(id);
//		
//		// set DossierMed as a model attribute to pre-populate the form
//		model.addAttribute("dossier", dossierService.getDossierMedById(id));
//		model.addAttribute("user", userService.getUserById(dossierService.getDossierMedById(id).getUser().getId()));
//		//model.addAttribute("dossier", dossier);
//		return "dossier/update_dossier";
//	}
//	
	@GetMapping("/update_dossier/{id}")
	public String showFormForUpdate(@PathVariable ( value = "id") long id, Model model) {
		
		// get DossierMed from the service
		//DossierMed dossier = dossierService.getDossierMedById(id);
		
		// set DossierMed as a model attribute to pre-populate the form
		model.addAttribute("dossier", dossierService.getDossierMedById(id));
		model.addAttribute("user1", userService.getUserById(dossierService.getDossierMedById(id).getUser().getId()));
		//model.addAttribute("user", userService.getUserById(1));
		//model.addAttribute("dossier", dossier);
		return "dossier/update_dossier";
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
	@GetMapping("/delete_dossier/{id}")
    public String deleteDept(@PathVariable("id") int id, Model model) {
        model.addAttribute("dossier_id", id);
        return "dossier/confirm_delete";
    }
    @PostMapping("/delete_dossier/{id}")
    public String confirmDeleteUser(@PathVariable("id") int id) {
        this.dossierService.deleteDossierMedById(id);
        return "redirect:/dossiers/";
    }
}

