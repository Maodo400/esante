package sn.esp.sante.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import sn.esp.sante.config.JwtService;
import sn.esp.sante.config.UserNotActivatedException;
import sn.esp.sante.model.AuthenticationResponse;
import sn.esp.sante.model.Mail;
import sn.esp.sante.model.User;
import sn.esp.sante.model.Role;
import sn.esp.sante.repository.UserRepository;
import sn.esp.sante.service.interf.IUserService;
import sn.esp.sante.service.mail.MailService;

@Service
public class UserService implements IUserService{
	@Autowired
	private MailService mailService;
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
			AuthenticationManager authenticationManager) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
	}

	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	@Override
	public ResponseEntity<?> confirm(@RequestParam String token) {
		User user = userRepository.findByConfirmationToken(token);
		if (user == null) {
			return ResponseEntity.badRequest().body("Invalid token");
		}
		user.setActivated(true);
		user.setConfirmationToken("");

		System.out.println(user.getId() + " active " + user.isActivated());

		userRepository.save(user);
		return ResponseEntity.status(HttpStatus.OK).body("User email successfully verified!");
	}

	@Override
	public ResponseEntity<?> register(@RequestBody User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(Role.USER);
		user.setCreatedBy("ADMIN");
		user.setLastModifiedBy("ADMIN");
		Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
		System.out.println(existingUser);
		if (!existingUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User with this email already exists");
			//return ResponseEntity.badRequest().body("User with this email already exists");
		}
		user.setConfirmationToken(UUID.randomUUID().toString());
		System.out.println(user.isEnabled());
		userRepository.save(user);

		String appUrl = "http://localhost:8081";
		//String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		System.out.println(appUrl);
		Mail mail = new Mail();
		mail.setMailFrom("mysteremady@gmail.com");
		mail.setMailTo(user.getEmail());
		mail.setMailSubject("Email Confirmation");
		mail.setMailContent("To confirm you email-address, please click the link below:\n" + appUrl
				+ "/api/v1/auth/confirm?token=" + user.getConfirmationToken());
		// mailService.sendEmail(mail);
		System.out.println(appUrl + "/api/v1/auth/confirm?token=" + user.getConfirmationToken());

		return ResponseEntity.status(HttpStatus.CREATED).body("User successfully signed up!");
	}

	@Override
	public AuthenticationResponse authenticate(User request) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
		if (user != null && user.isActivated()) {
			String jwtToken = jwtService.generateToken(user);
			return new AuthenticationResponse(jwtToken);
		} else {
			// System.out.println("Your account is not activated");
			throw new UserNotActivatedException("Your account is not activated");
		}
	}

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

	@Override
	public ResponseEntity<?> update(User user) {
		userRepository.save(user);
		return ResponseEntity.status(HttpStatus.CREATED).body("User successfully signed up!");
	}
}