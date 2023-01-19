package sn.esp.sante.service.interf;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import sn.esp.sante.model.AuthenticationResponse;
import sn.esp.sante.model.User;

public interface IUserService {
	public List<User> getAllUsers();
	public ResponseEntity<?> register(User user);
	public User getUserById(long id);
	public void deleteUserById(long id);
	public Page<User> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);
	
	public ResponseEntity<?> confirm(String token);
	public AuthenticationResponse authenticate(User request);
	public ResponseEntity<?> update(User user);
}
