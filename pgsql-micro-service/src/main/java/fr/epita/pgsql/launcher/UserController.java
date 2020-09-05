package fr.epita.pgsql.launcher;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.epita.pgsql.datamodel.User;
import fr.epita.pgsql.services.data.PgsqlUserDAO;
import fr.epita.services.business.PgsqlAddressBusinessException;
import fr.epita.services.business.PgsqlProfileBusinessException;
import fr.epita.services.business.PgsqlUserBusinessException;

@RestController
public class UserController {
	
	@Inject 
	PgsqlUserDAO dao;
	
	@CrossOrigin(origins = "*")
	@GetMapping(value="/users")
	public List<User> listUsers() throws PgsqlUserBusinessException{
		return dao.listUsers();
	}
	
	@CrossOrigin(origins = "*")
	@GetMapping(value="/users/{id}")
	public User getUserById(@PathVariable long id) throws PgsqlUserBusinessException, PgsqlProfileBusinessException, PgsqlAddressBusinessException{
		return dao.getUserById(id);
	}
	
	@CrossOrigin(origins = "*")
	@PostMapping(value="/users")
	public User addUser(@RequestBody User user) throws PgsqlUserBusinessException, PgsqlProfileBusinessException, PgsqlAddressBusinessException{
		return dao.addUser(user);
	}
	
	@CrossOrigin(origins = "*")
	@PatchMapping(value="/users/{id}")
	public User updateUser(@RequestBody User user, @PathVariable long id) throws PgsqlUserBusinessException, PgsqlProfileBusinessException, PgsqlAddressBusinessException {
		return dao.updateUser(user, id);
	}
	
	@CrossOrigin(origins = "*")
	@DeleteMapping(value="/users/{id}")
	public User deleteUser(@PathVariable long id) throws PgsqlUserBusinessException, PgsqlProfileBusinessException, PgsqlAddressBusinessException {
		return dao.deleteUser(id);
	}
	
	@CrossOrigin(origins = "*")
	@GetMapping(value="/users/auth")
	public User loginUser(@RequestParam String username, @RequestParam String password) throws PgsqlUserBusinessException, PgsqlProfileBusinessException, PgsqlAddressBusinessException {
		return dao.userLogin(username, password);
	}
}
