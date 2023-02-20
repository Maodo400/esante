package sn.esp.sante.service.interf;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import sn.esp.sante.model.User;

public interface IUserService {
	public List<User> getAllUsers();
	//public ResponseEntity<?> register(User user);
	public User getUserById(long id);
	public void deleteUserById(long id);
	public Page<User> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);
	
    boolean findRoleByUser(String role, User user);
    
    User findUserByEmail(String email);

    boolean isAdmin(User user);

    boolean isUser(User user);

    void saveUser(User user);
    
    void saveUser(Model model, @Valid User user, BindingResult bindingResult);
    
    boolean isMedecin(User user);
    
	public User findByConfirmationToken(String confirmationToken);

}
