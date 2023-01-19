package sn.esp.sante.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sn.esp.sante.model.AuthenticationResponse;
import sn.esp.sante.model.User;
import sn.esp.sante.repository.UserRepository;
import sn.esp.sante.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

	private final UserService userService;
	
	public UserController(UserService userService, UserRepository userRepository) {
		super();
		this.userService = userService;
	}
	
	@GetMapping("/add_user")
	public String showNewUserForm(Model model) {
		// create model attribute to bind form data
		User user = new User();
		model.addAttribute("user", user);
		return "user/new_user";
	}
	
//	@PostMapping("/register")
//	public ResponseEntity<?> register(@RequestBody User user, HttpServletRequest request) {
//		return ResponseEntity.ok(userService.register(user, request));
//	}
	
	@PostMapping("/register")
	public String register(@ModelAttribute("user") User user) {
		// save Departement to database
		userService.register(user);
		return "redirect:/users/";
	}
	
	@PostMapping("/update")
	public String update(@ModelAttribute("user") User user) {
		// save Departement to database
		userService.update(user);
		return "redirect:/users/";
	}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody User request) throws Exception {
		return ResponseEntity.ok(userService.authenticate(request));
	}
	
	@GetMapping("/confirm")
	public ResponseEntity<?> confirmEmail(@RequestParam String token)
	{
		return ResponseEntity.ok(userService.confirm(token));
	}
	
	@GetMapping("/update_user/{id}")
	public String showFormForUpdate(@PathVariable ( value = "id") long id, Model model) {
		
		// get User from the userService
		User user = userService.getUserById(id);
		
		// set User as a model attribute to pre-populate the form
		model.addAttribute("user", user);
		return "user/update_user";
	}
	
	@GetMapping("/delete_user/{id}")
	public String deleteUser(@PathVariable (value = "id") long id) {
		
		// call delete User method 
		this.userService.deleteUserById(id);
		return "redirect:/users/";
	}
	
	@GetMapping("/")
	public String viewHomePage(Model model) {
		return findPaginated(1, "firstname", "asc", model);		
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
}
