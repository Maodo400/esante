package sn.esp.sante.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import sn.esp.sante.model.Role;
import sn.esp.sante.model.User;
import sn.esp.sante.repository.RoleRepository;
import sn.esp.sante.repository.UserRepository;
import sn.esp.sante.service.interf.IUserService;

@Service
public class UserService implements IUserService{
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public UserService(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    
    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

//	@Override
//	public ResponseEntity<?> confirm(@RequestParam String token) {
//		User user = userRepository.findByConfirmationToken(token);
//		if (user == null) {
//			return ResponseEntity.badRequest().body("Invalid token");
//		}
//		user.setActivated(true);
//		user.setConfirmationToken("");
//
//		System.out.println(user.getId() + " active " + user.isActivated());
//
//		userRepository.save(user);
//		return ResponseEntity.status(HttpStatus.OK).body("User email successfully verified!");
//	}

//	@Override
//	public ResponseEntity<?> register(@RequestBody User user) {
//		user.setPassword(passwordEncoder.encode(user.getPassword()));
//		user.setCreatedBy("ADMIN");
//		user.setLastModifiedBy("ADMIN");
//		Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
//		System.out.println(existingUser);
//		if (!existingUser.isEmpty()) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User with this email already exists");
//			//return ResponseEntity.badRequest().body("User with this email already exists");
//		}
//		user.setConfirmationToken(UUID.randomUUID().toString());
//		System.out.println(user.isEnabled());
//		userRepository.save(user);
//
//		String appUrl = "http://localhost:8081";
//		//String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
//		System.out.println(appUrl);
//		Mail mail = new Mail();
//		mail.setMailFrom("mysteremady@gmail.com");
//		mail.setMailTo(user.getEmail());
//		mail.setMailSubject("Email Confirmation");
//		mail.setMailContent("To confirm you email-address, please click the link below:\n" + appUrl
//				+ "/api/v1/auth/confirm?token=" + user.getConfirmationToken());
//		// mailService.sendEmail(mail);
//		System.out.println(appUrl + "/api/v1/auth/confirm?token=" + user.getConfirmationToken());
//
//		return ResponseEntity.status(HttpStatus.CREATED).body("User successfully signed up!");
//	}

//	@Override
//	public AuthenticationResponse authenticate(AuthenticationRequest request) {
//		authenticationManager
//				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
//		var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
//		if (user != null && user.isActivated()) {
//			String jwtToken = jwtService.generateToken(user);
//			return new AuthenticationResponse(jwtToken);
//		} else {
//			// System.out.println("Your account is not activated");
//			throw new UserNotActivatedException("Your account is not activated");
//		}
//	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public User getUserById(long id) {
		Optional<User> optional = userRepository.findById(id);
		User user = null;
		if (optional.isPresent()) {
			user = optional.get();
		} else {
			throw new RuntimeException(" user not found for id :: " + id);
		}
		return user;
	}

	@Override
	public void deleteUserById(long id) {
		this.userRepository.deleteById(id);
	}

	@Override
	public Page<User> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.userRepository.findAll(pageable);
	}

//	@Override
//	public ResponseEntity<?> update(User user) {
//		userRepository.save(user);
//		return ResponseEntity.status(HttpStatus.CREATED).body("User successfully signed up!");
//	}


	    @Autowired
	    private BCryptPasswordEncoder bCryptPasswordEncoder;

	    @Override
	    public boolean findRoleByUser(String role, User user) {
	        return user.getRoles().contains(roleRepository.findByRole(role));
	    }

	    @SuppressWarnings("unlikely-arg-type")
		@Override
	    public boolean isAdmin(User user) {
	        return user.getRoles().contains("ADMIN");
	    }

	    @SuppressWarnings("unlikely-arg-type")
		@Override
	    public boolean isUser(User user) {
	        return user.getRoles().contains("USER");
	    }
	    
	    @SuppressWarnings("unlikely-arg-type")
		@Override
	    public boolean isMedecin(User user) {
	        return user.getRoles().contains("MEDECIN");
	    }

	    @Override
	    public void saveUser(User user) {
	        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
	        user.setActive(0);
	        Role userRole = roleRepository.findByRole("USER");
	        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
	        userRepository.save(user);
	    }
	    
	    @Override
	    public void saveUser(Model model, @Valid User user, BindingResult bindingResult) {
	        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
	        user.setActive(0);
	        Role userRole = roleRepository.findByRole("USER");
	        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
	        userRepository.save(user);
	    }

		@Override
		public User findByConfirmationToken(String confirmationToken) {
			return userRepository.findByConfirmationToken(confirmationToken);
		}
}