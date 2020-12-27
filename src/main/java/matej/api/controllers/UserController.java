package matej.api.controllers;

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
import matej.models.MessageResponse;


@RestController
@CrossOrigin(origins={"*", "http://www.jamb.com.hr", "https://jamb-react.herokuapp.com"})
@RequestMapping("/users")
public class UserController {

	@Autowired
	UserService userService;

    @GetMapping("")
	public ResponseEntity<Object> getUsers() {
		try {
			return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getUserById(@PathVariable int id) {
        try {
			return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
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


