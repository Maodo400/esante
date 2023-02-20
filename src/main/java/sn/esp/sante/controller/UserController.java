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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sn.esp.sante.model.EnumSexe;
import sn.esp.sante.model.User;
import sn.esp.sante.repository.DossierMedRepository;
import sn.esp.sante.repository.RoleRepository;
import sn.esp.sante.repository.UserRepository;
import sn.esp.sante.service.DossierMedService;
import sn.esp.sante.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DossierMedRepository dossierRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    @ModelAttribute("loggedInUser")
    public User populateUserDetails(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = userService.findUserByEmail(auth.getName());
        model.addAttribute("isUser", userService.isUser(loggedInUser));
        model.addAttribute("isAdmin", userService.isAdmin(loggedInUser));
        model.addAttribute("isMedecin", userService.isMedecin(loggedInUser));
        System.out.println(loggedInUser.toString());
        return loggedInUser;
    }
    
	@GetMapping("/")
	public String viewHomePage(Model model, @ModelAttribute User loggedInUser) {
		populateUserDetails(model);
		return findPaginated(1, "firstName", "asc", model);		
	}

    @GetMapping(value = "")
    public String userHome(Model model, @ModelAttribute User loggedInUser){
        populateUserDetails(model);

        return "user/index";
    }

    @GetMapping("/{id}")
    public String userPage(Model model, @PathVariable("id") int id){
        //TODO: create stats page
        return "user/stats";
    }

    @GetMapping("/{id}/account")
    public String userSettings(Model model, @PathVariable("id") int id){
        //TODO: create settings page
        return "user/stats";
    }
    
    @GetMapping("/page/{pageNo}")
	public String findPaginated(@PathVariable (value = "pageNo") int pageNo, 
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			Model model) {
		int pageSize = 5;
		
		Page<User> page = userService.findPaginated(pageNo, pageSize, sortField, sortDir);
		List<User> listUsers = page.getContent();
		
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		model.addAttribute("list_users", listUsers);
		return "user/index";
	}
	@GetMapping("/update_user/{id}")
	public String showFormForUpdate(@PathVariable ( value = "id") long id, Model model) {
		
		// get DossierMed from the service
		//DossierMed user = userService.getDossierMedById(id);
		
		// set DossierMed as a model attribute to pre-populate the form
		model.addAttribute("user", userService.getUserById(id));
		model.addAttribute("sexe", EnumSexe.values());
		//model.addAttribute("user", userService.getUserById(1));
		//model.addAttribute("user", user);
		return "user/update_user";
	}
	@GetMapping("/affiche_user/{id}")
	public String showAffiche(@PathVariable ( value = "id") long id, Model model) {
		
		// get DossierMed from the service
		//DossierMed user = userService.getDossierMedById(id);
		
		// set DossierMed as a model attribute to pre-populate the form
		User user = userService.getUserById(id);
		model.addAttribute("user", populateUserDetails(model));
		model.addAttribute("dossier", dossierRepository.findByUser(user));
		//model.addAttribute("user", userService.getUserById(1));
		//model.addAttribute("user", user);
		return "user/affiche";
	}
	@PostMapping("/save_user")
	public String saveUser(@ModelAttribute("user")@Valid User user, Model model, BindingResult bindingResult) {
		User exists = userService.findUserByEmail(user.getEmail());
        //userRoles.addAll(Arrays.asList(new Role("USER")));

        if(exists != null){
            bindingResult.rejectValue("email", "error.user", "This email already exists!");
        }
        char firstInitial = user.getFirstName().charAt(0);
        user.setUsername((user.getFirstName() +"_"+ user.getLastName()).toLowerCase());
		user.setCreatedBy(user.getUsername());
		user.setLastModifiedBy(user.getUsername());
		userService.saveUser(null, user, null);
		return "redirect:/user/affiche_user/"+user.getId();
	}
	@GetMapping("/delete_user/{id}")
    public String deleteUser(@PathVariable("id") int id, Model model) {
        model.addAttribute("user_id", id);
        return "user/confirm_delete";
    }
    @PostMapping("/delete_user/{id}")
    public String confirmDeleteUser(@PathVariable("id") int id) {
        this.userService.deleteUserById(id);
        return "redirect:/user/";
    }
}
