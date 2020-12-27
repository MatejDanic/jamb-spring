package matej.api.controllers;

import java.util.Map;

// import com.google.common.util.concurrent.RateLimiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import matej.api.services.FormService;
import matej.exceptions.IllegalMoveException;
import matej.exceptions.InvalidOwnershipException;
import matej.models.MessageResponse;
import matej.models.types.BoxType;
import matej.security.jwt.JwtUtils;

@RestController
@CrossOrigin(origins = { "*", "http://www.jamb.com.hr", "https://jamb-react.herokuapp.com" })
@RequestMapping("/forms")
public class FormController {

	@Autowired
	FormService formService;

	@Autowired
	JwtUtils jwtUtils;

	// private final RateLimiter rateLimiter = RateLimiter.create(0.2);

	@PutMapping("")
	public ResponseEntity<Object> initializeForm(@RequestHeader(value = "Authorization") String headerAuth) {
		// if (!rateLimiter.tryAcquire(1)) return null;
		try {
			return new ResponseEntity<>(formService.initializeForm(jwtUtils.getUsernameFromHeader(headerAuth)),
					HttpStatus.OK);
		} catch (UsernameNotFoundException e) {
			return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/{id}/roll")
	public ResponseEntity<Object> rollDice(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "id") int id, @RequestBody Map<Integer, Boolean> diceToThrow) {
		try {
			return new ResponseEntity<>(
					formService.rollDice(jwtUtils.getUsernameFromHeader(headerAuth), id, diceToThrow), HttpStatus.OK);
		} catch (InvalidOwnershipException | IllegalMoveException e) {
			return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/{id}/announce")
	public ResponseEntity<Object> announce(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "id") int id, @RequestBody BoxType boxType) {
		try {
			return new ResponseEntity<>(formService.announce(jwtUtils.getUsernameFromHeader(headerAuth), id, boxType),
					HttpStatus.OK);
		} catch (IllegalMoveException | InvalidOwnershipException e) {
			return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/{id}/columns/{columnTypeId}/boxes/{boxTypeId}/fill")
	public ResponseEntity<Object> fillBox(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "id") int id, @PathVariable(value = "columnTypeId") int columnTypeId,
			@PathVariable(value = "boxTypeId") int boxTypeId) {
		try {
			return new ResponseEntity<>(
					formService.fillBox(jwtUtils.getUsernameFromHeader(headerAuth), id, columnTypeId, boxTypeId),
					HttpStatus.OK);
		} catch (IllegalMoveException | InvalidOwnershipException e) {
			return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> deleteFormById(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "id") int id) {
		try {
			formService.deleteFormById(jwtUtils.getUsernameFromHeader(headerAuth), id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (InvalidOwnershipException e) {
			return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/{id}/restart")
	public ResponseEntity<Object> restartFormById(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "id") int id) {
		try {
			formService.restartFormById(jwtUtils.getUsernameFromHeader(headerAuth), id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (InvalidOwnershipException e) {
			return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("")
	public ResponseEntity<Object> getFormList() {
		try {
			return new ResponseEntity<>(formService.getFormList(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getFormById(@PathVariable(value = "id") int id) {
		try {
			return new ResponseEntity<>(formService.getFormById(id), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}/columns")
	public ResponseEntity<Object> getFormColumns(@PathVariable(value = "id") int id) {
		try {
			return new ResponseEntity<>(formService.getFormById(id).getColumns(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}/columns/{columnTypeId}")
	public ResponseEntity<Object> getFormColumnByType(@PathVariable(value = "id") int id,
			@PathVariable(value = "columnTypeId") int columnTypeId) {
		try {
			return new ResponseEntity<>(formService.getFormById(id).getColumnByTypeId(columnTypeId), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}/columns/{columnTypeId}/boxes")
	public ResponseEntity<Object> getFormColumnBoxes(@PathVariable(value = "id") int id,
			@PathVariable(value = "columnTypeId") int columnTypeId) {
		try {
			return new ResponseEntity<>(formService.getFormById(id).getColumnByTypeId(columnTypeId).getBoxes(),
					HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}/columns/{columnTypeId}/boxes/{boxTypeId}")
	public ResponseEntity<Object> getFormColumnBoxBoxByType(@PathVariable(value = "id") int id,
			@PathVariable(value = "columnTypeId") int columnTypeId, @PathVariable(value = "boxTypeId") int boxTypeId) {
		try {
			return new ResponseEntity<>(
					formService.getFormById(id).getColumnByTypeId(columnTypeId).getBoxByTypeId(boxTypeId),
					HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}/dice")
	public ResponseEntity<Object> getFormDice(@PathVariable(value = "id") int id) {
		try {
			return new ResponseEntity<>(formService.getFormById(id).getDice(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
}
