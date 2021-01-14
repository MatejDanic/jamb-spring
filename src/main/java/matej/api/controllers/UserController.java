package matej.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import matej.api.services.UserService;
import matej.exceptions.InvalidOwnershipException;
import matej.models.payload.request.PreferenceRequest;
import matej.models.payload.response.MessageResponse;
import matej.components.JwtUtils;

@RestController
@CrossOrigin(origins = { "*", "http://www.jamb.com.hr", "https://jamb-react.herokuapp.com" })
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	JwtUtils jwtUtils;

	@GetMapping("")
	public ResponseEntity<Object> getUsers() {
		try {
			return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
		} catch (Exception exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getUserById(@PathVariable int id) {
		try {
			return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
		} catch (Exception exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}/form")
	public ResponseEntity<Object> getUserForm(@PathVariable int id) {
		try {
			return new ResponseEntity<>(userService.getUserById(id).getForm(), HttpStatus.OK);
		} catch (Exception exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteUserById(@PathVariable int id) {
		try {
			userService.deleteUserById(id);
			return new ResponseEntity<>(new MessageResponse("Korisnik uspješno izbrisan."), HttpStatus.OK);
		} catch (Exception exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}/preferences")
	public ResponseEntity<Object> getUserPreferences(@PathVariable int id) {
		try {
			return new ResponseEntity<>(userService.getUserPreference(id), HttpStatus.OK);
		} catch (Exception exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@PatchMapping("/{id}/preferences")
	public ResponseEntity<Object> updatePreference(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "id") int id, @RequestBody PreferenceRequest prefRequest) {
		try {
			if (!userService.checkOwnership(jwtUtils.getUsernameFromHeader(headerAuth), id)) {
				throw new InvalidOwnershipException("Korisnik nema ovlasti nad računom s id-em " + id);
			}
			return new ResponseEntity<>(userService.updateUserPreference(id, prefRequest), HttpStatus.OK);
		} catch (InvalidOwnershipException exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
}
