package sn.esp.sante.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import sn.esp.sante.model.Departement;
import sn.esp.sante.model.Mail;
import sn.esp.sante.model.User;
import sn.esp.sante.repository.DepartementRepository;
import sn.esp.sante.repository.UserRepository;
import sn.esp.sante.service.UserService;
import sn.esp.sante.service.mail.MailService;

@Controller
public class IndexController {

    @Autowired
    UserService userService;

    @Autowired
    DepartementRepository deptRepository;
    
	@Autowired
	private MailService mailService;
    
    @Autowired
    UserRepository userRepository;

    @ModelAttribute("loggedInUser")
    public User populateUserDetails(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = userService.findUserByEmail(auth.getName());
        return loggedInUser;
    }

    @RequestMapping(value = "")
    public String index(Model model, @ModelAttribute("loggedInUser") User loggedInUser, HttpServletRequest request){

        List<Departement> openDepartements = deptRepository.findAll();
        model.addAttribute("openDepartements", openDepartements);


        return "index";
    }

    @GetMapping(value = "login")
    public String login(Model model){
        model.addAttribute("title", "login");
        return "login";
    }
    @GetMapping("index")
    public String home(){
        return "index";
    }
    
    @GetMapping("/access_denied")
    public String access(){
        return "403";
    }

    @GetMapping("register")
    public String showRegistrationForm(Model model){
        User user = new User();
        model.addAttribute("user", user);
        return "register";
    }

    // handler method to handle register user form submit request
    @PostMapping("/register/save")
    public String createNewAccount(Model model, @Valid User user, BindingResult bindingResult, HttpServletRequest request){
        User exists = userService.findUserByEmail(user.getEmail());
        //userRoles.addAll(Arrays.asList(new Role("USER")));

        if(exists != null){
            bindingResult.rejectValue("email", "error.user", "This email already exists!");
        }
        if(bindingResult.hasErrors()){
            return "register";
        } else {
            //TODO: allow user to login using either username or email
            char firstInitial = user.getFirstName().charAt(0);
            user.setUsername((user.getFirstName() +"_"+ user.getLastName()).toLowerCase());
            user.setCreatedBy(user.getUsername());
    		user.setLastModifiedBy(user.getUsername());
    		user.setConfirmationToken(UUID.randomUUID().toString());
            userService.saveUser(user);
            
            String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    		System.out.println(appUrl);
    		
            Mail mail = new Mail();
    		mail.setMailFrom("assmalicksy461@gmail.com");
    		mail.setMailTo(user.getEmail());
    		mail.setMailSubject("Activation du compte");
    		mail.setMailContent("Por activer votre compte, merci de cliquer sur ce lien ci dessous:\n"
    				+ appUrl + "/confirm?token=" + user.getConfirmationToken());
    		try {
        		mailService.sendEmail(mail);
			} catch (Exception e) {
				System.out.println(appUrl + "/confirm?token=" + user.getConfirmationToken());
				System.out.println("Probléme de connection");
			}
            model.addAttribute("msg","User registered successfully");
            model.addAttribute("user", new User());
            return "login";
        }
    }

    @GetMapping("/users")
    public String listRegisteredUsers(Model model){
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users";
    }
    
    @GetMapping("/confirm")
	public ResponseEntity<?> confirmEmail(@RequestParam String token)
	{
		User user = userService.findByConfirmationToken(token);
		if(user == null) 
		{
			return ResponseEntity
					.badRequest()
					.body("Votre compte a été déja ativé");
		}
		user.setActive(1);
		user.setConfirmationToken("");
		userRepository.save(user);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body("User email successfully verified!");
	}

}