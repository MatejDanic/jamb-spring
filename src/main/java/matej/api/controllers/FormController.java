package matej.api.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import matej.api.services.FormService;
import matej.api.services.UserService;
import matej.exceptions.IllegalMoveException;
import matej.exceptions.InvalidOwnershipException;
import matej.models.payload.response.MessageResponse;
import matej.models.types.BoxType;
import matej.components.JwtUtils;

@RestController
@CrossOrigin(origins = { "*", "http://www.jamb.com.hr", "https://jamb-react.herokuapp.com" })
@RequestMapping("/api/forms")
public class FormController {

	@Autowired
	FormService formService;

	@Autowired
	UserService userService;

	@Autowired
	JwtUtils jwtUtils;

	@PutMapping("")
	public ResponseEntity<Object> initializeForm(@RequestHeader(value = "Authorization") String headerAuth) {
		try {
			return new ResponseEntity<>(formService.initializeForm(jwtUtils.getUsernameFromHeader(headerAuth)),
					HttpStatus.OK);
		} catch (UsernameNotFoundException exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("")
	public ResponseEntity<Object> newForm(@RequestHeader(value = "Authorization") String headerAuth) {
		try {
			return new ResponseEntity<>(formService.newForm(jwtUtils.getUsernameFromHeader(headerAuth)),
					HttpStatus.OK);
		} catch (UsernameNotFoundException | InvalidOwnershipException exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/{id}/roll")
	public ResponseEntity<Object> rollDice(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "id") int id, @RequestBody Map<Integer, Boolean> diceToThrow) {
		try {
			if (!userService.checkFormOwnership(jwtUtils.getUsernameFromHeader(headerAuth), id)) {
				throw new InvalidOwnershipException("Forma s id-em " + id + " ne pripada korisniku.");
			}
			return new ResponseEntity<>(formService.rollDice(id, diceToThrow), HttpStatus.OK);
		} catch (InvalidOwnershipException | IllegalMoveException exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/{id}/announce")
	public ResponseEntity<Object> announce(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "id") int id, @RequestBody BoxType boxType) {
		try {
			if (!userService.checkFormOwnership(jwtUtils.getUsernameFromHeader(headerAuth), id)) {
				throw new InvalidOwnershipException("Forma s id-em " + id + " ne pripada korisniku.");
			}
			return new ResponseEntity<>(formService.announce(id, boxType), HttpStatus.OK);
		} catch (IllegalMoveException | InvalidOwnershipException exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/{id}/columns/{columnTypeId}/boxes/{boxTypeId}/fill")
	public ResponseEntity<Object> fillBox(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "id") int id, @PathVariable(value = "columnTypeId") int columnTypeId,
			@PathVariable(value = "boxTypeId") int boxTypeId) {
		try {
			if (!userService.checkFormOwnership(jwtUtils.getUsernameFromHeader(headerAuth), id)) {
				throw new InvalidOwnershipException("Forma s id-em " + id + " ne pripada korisniku.");
			}
			return new ResponseEntity<>(
					formService.fillBox(id, columnTypeId, boxTypeId),
					HttpStatus.OK);
		} catch (IllegalMoveException | InvalidOwnershipException exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> deleteFormById(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "id") int id) {
		try {
			if (!userService.checkFormOwnership(jwtUtils.getUsernameFromHeader(headerAuth), id)) {
				throw new InvalidOwnershipException("Forma s id-em " + id + " ne pripada korisniku.");
			}
			formService.deleteFormById(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (InvalidOwnershipException exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/{id}/restart")
	public ResponseEntity<Object> restartFormById(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "id") int id) {
		try {
			if (!userService.checkFormOwnership(jwtUtils.getUsernameFromHeader(headerAuth), id)) {
				throw new InvalidOwnershipException("Forma s id-em " + id + " ne pripada korisniku.");
			}
			formService.restartFormById(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (InvalidOwnershipException exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("")
	public ResponseEntity<Object> getFormList() {
		try {
			return new ResponseEntity<>(formService.getFormList(), HttpStatus.OK);
		} catch (Exception exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getFormById(@PathVariable(value = "id") int id) {
		try {
			return new ResponseEntity<>(formService.getFormById(id), HttpStatus.OK);
		} catch (Exception exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}/columns")
	public ResponseEntity<Object> getFormColumns(@PathVariable(value = "id") int id) {
		try {
			return new ResponseEntity<>(formService.getFormById(id).getColumns(), HttpStatus.OK);
		} catch (Exception exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}/columns/{columnTypeId}")
	public ResponseEntity<Object> getFormColumnByType(@PathVariable(value = "id") int id,
			@PathVariable(value = "columnTypeId") int columnTypeId) {
		try {
			return new ResponseEntity<>(formService.getFormById(id).getColumnByTypeId(columnTypeId), HttpStatus.OK);
		} catch (Exception exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}/columns/{columnTypeId}/boxes")
	public ResponseEntity<Object> getFormColumnBoxes(@PathVariable(value = "id") int id,
			@PathVariable(value = "columnTypeId") int columnTypeId) {
		try {
			return new ResponseEntity<>(formService.getFormById(id).getColumnByTypeId(columnTypeId).getBoxes(),
					HttpStatus.OK);
		} catch (Exception exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}/columns/{columnTypeId}/boxes/{boxTypeId}")
	public ResponseEntity<Object> getFormColumnBoxBoxByType(@PathVariable(value = "id") int id,
			@PathVariable(value = "columnTypeId") int columnTypeId, @PathVariable(value = "boxTypeId") int boxTypeId) {
		try {
			return new ResponseEntity<>(
					formService.getFormById(id).getColumnByTypeId(columnTypeId).getBoxByTypeId(boxTypeId),
					HttpStatus.OK);
		} catch (Exception exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}/dice")
	public ResponseEntity<Object> getFormDice(@PathVariable(value = "id") int id) {
		try {
			return new ResponseEntity<>(formService.getFormById(id).getDice(), HttpStatus.OK);
		} catch (Exception exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
}
