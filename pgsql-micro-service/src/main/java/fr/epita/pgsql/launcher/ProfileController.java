package fr.epita.pgsql.launcher;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import fr.epita.pgsql.datamodel.Profile;
import fr.epita.pgsql.services.data.PgsqlProfileDAO;
import fr.epita.services.business.PgsqlAddressBusinessException;
import fr.epita.services.business.PgsqlProfileBusinessException;

@RestController
public class ProfileController {

	@Inject
	PgsqlProfileDAO dao;
	
	@CrossOrigin(origins = "*")
	@GetMapping(value="/profiles/{id}")
	public Profile getProfileById(@PathVariable long id) throws PgsqlProfileBusinessException, PgsqlAddressBusinessException {
		return dao.getProfileById(id);
	}
	
	@CrossOrigin(origins = "*")
	@PostMapping(value="/profiles/{userId}")
	public Profile addProfle(@RequestBody Profile profile, @PathVariable long userId) throws PgsqlProfileBusinessException, PgsqlAddressBusinessException {
		return dao.addProfile(profile, userId);
	}
	
	@CrossOrigin(origins = "*")
	@PatchMapping(value="/profiles/{id}")
	public Profile updateProfle(@RequestBody Profile profile, @PathVariable long id) throws PgsqlProfileBusinessException, PgsqlAddressBusinessException {
		return dao.updateProfile(profile, id);
	}
	
	@CrossOrigin(origins = "*")
	@DeleteMapping(value="/profiles/{id}")
	public Profile deleteProfle(@PathVariable long id) throws PgsqlProfileBusinessException, PgsqlAddressBusinessException {
		return dao.deleteProfile(id);
	}
}
