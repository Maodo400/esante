package sn.esp.sante.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sn.esp.sante.model.Departement;
import sn.esp.sante.model.User;
import sn.esp.sante.service.DeptService;
import sn.esp.sante.service.UserService;

@Validated
@Controller
@RequestMapping("/depts")
public class DeptController {
	@Autowired
	private DeptService deptService;
	
	@Autowired
	private UserService userService;
	
	@ModelAttribute("loggedInUser")
    public User populateUserDetails(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = userService.findUserByEmail(auth.getName());
        model.addAttribute("isUser", userService.isUser(loggedInUser));
        model.addAttribute("isAdmin", userService.isAdmin(loggedInUser));
        model.addAttribute("isMedecin", userService.isMedecin(loggedInUser));
        return loggedInUser;
    }
	// display list of Departements
	@GetMapping("/")
	public String viewHomePage(Model model) {
		populateUserDetails(model);
		return findPaginated(1, "nom", "asc", model);		
	}
	
	@GetMapping("/add_dept")
	public String showNewDepartementForm(Model model) {
		// create model attribute to bind form data
		Departement departement = new Departement();
		model.addAttribute("departement", departement);
		return "departement/new_departement";
	}
	
	@PostMapping("/save_dept")
	public String saveDepartement(@ModelAttribute("departement")@Valid Departement departement, BindingResult bindingResult, Model model) {
		// save Departement to database
//		deptService.saveDept(departement);
//		return "redirect:/depts/";
		
		if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            //System.out.println(br.getFieldValue("nom"));
            return "departement/new_departement";
        }
        departement.setLastModifiedBy(populateUserDetails(model).getUsername());
		departement.setCreatedBy(populateUserDetails(model).getUsername());
        deptService.saveDept(departement);
        return "redirect:/depts/";
   
	}
	
	@GetMapping("/update_dept/{id}")
	public String showFormForUpdate(@PathVariable ( value = "id") long id, Model model) {
		
		// get Departement from the service
		Departement departement = deptService.getDeptById(id);
		
		// set Departement as a model attribute to pre-populate the form
		model.addAttribute("departement", departement);
		return "departement/update_departement";
	}
	
//	@GetMapping("/delete_dept/{id}")
//	public String deleteDepartement(@PathVariable (value = "id") long id) {
//		
//		// call delete Departement method 
//		this.deptService.deleteDeptById(id);
//		return "redirect:/depts/";
//	}
	
	
	@GetMapping("/page/{pageNo}")
	public String findPaginated(@PathVariable (value = "pageNo") int pageNo, 
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			Model model) {
		int pageSize = 5;
		
		Page<Departement> page = deptService.findPaginated(pageNo, pageSize, sortField, sortDir);
		List<Departement> listDepartements = page.getContent();
		
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		model.addAttribute("list_departements", listDepartements);
		return "departement/index";
	}
	@GetMapping("/delete_dept/{id}")
    public String deleteDept(@PathVariable("id") int id, Model model) {
        model.addAttribute("dept_id", id);
        return "departement/confirm_delete";
    }
    @PostMapping("/delete_dept/{id}")
    public String confirmDeleteUser(@PathVariable("id") int id) {
        this.deptService.deleteDeptById(id);
        return "redirect:/depts/";
    }
}
