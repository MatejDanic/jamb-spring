package matej.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import matej.api.services.UserService;
import matej.models.AuthUser;
import matej.models.MessageResponse;


@RestController
@CrossOrigin(origins={"*", "http://www.jamb.com.hr", "https://jamb-react.herokuapp.com"})
@RequestMapping("/users")
public class UserController {

	@Autowired
	UserService userService;

    @GetMapping("")
	public List<AuthUser> getUsers() {
        return userService.getUsers();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getUserById(@PathVariable int id) {
        try {
            AuthUser user = userService.getUserById(id);
			return new ResponseEntity<>(user, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
    }

    @PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteUserById(@PathVariable int id) {
		try {
            userService.deleteUserById(id);
			return new ResponseEntity<>(new MessageResponse("Korisnik uspješno izbrisan."), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
}


