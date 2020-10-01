package matej.api.controllers;

import java.util.List;
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
import matej.models.GameBox;
import matej.models.GameDice;
import matej.models.GameForm;
import matej.models.GameColumn;
import matej.security.jwt.JwtUtils;

@RestController
@CrossOrigin(origins={"*", "http://www.jamb.com.hr", "https://jamb-react.herokuapp.com"})
@PreAuthorize("isAuthenticated()")
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
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/{id}/roll")
	public ResponseEntity<Object> rollDice(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "id") int id, @RequestBody Map<Integer, Boolean> diceToThrow) {
		try {
			return new ResponseEntity<>(
					formService.rollDice(jwtUtils.getUsernameFromHeader(headerAuth), id, diceToThrow), HttpStatus.OK);
		} catch (InvalidOwnershipException | IllegalMoveException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/{id}/announce")
	public ResponseEntity<Object> announce(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "id") int id, @RequestBody int boxTypeId) {
		try {
			return new ResponseEntity<>(
					formService.announce(jwtUtils.getUsernameFromHeader(headerAuth), id, boxTypeId),
					HttpStatus.OK);
		} catch (IllegalMoveException | InvalidOwnershipException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/{id}/columns/{columnTypeId}/boxes/{boxTypeId}/fill")
	public ResponseEntity<Object> fillBox(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "id") int id, @PathVariable(value = "columnTypeId") int columnTypeId,
			@PathVariable(value = "boxTypeId") int boxTypeId) {
		try {
			return new ResponseEntity<>(formService.fillBox(jwtUtils.getUsernameFromHeader(headerAuth), id,
					columnTypeId, boxTypeId), HttpStatus.OK);
		} catch (IllegalMoveException | InvalidOwnershipException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteFormById(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "id") int id) throws InvalidOwnershipException {
		try {
			formService.deleteFormById(jwtUtils.getUsernameFromHeader(headerAuth), id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (UsernameNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("")
	@PreAuthorize("hasAuthority('ADMIN')")
	public List<GameForm> getFormList() {
		return formService.getFormList();
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public GameForm getFormById(@PathVariable(value = "id") int id) {
		return formService.getFormById(id);
	}

	@GetMapping("/{id}/columns")
	@PreAuthorize("hasAuthority('ADMIN')")
	public List<GameColumn> getFormColumns(@PathVariable(value = "id") int id) {
		return formService.getFormById(id).getColumns();
	}

	@GetMapping("/{id}/columns/{columnTypeId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public GameColumn getFormColumnByType(@PathVariable(value = "id") int id,
			@PathVariable(value = "columnTypeId") int columnTypeId) {
		return formService.getFormById(id).getColumnByTypeId(columnTypeId);
	}

	@GetMapping("/{id}/columns/{columnTypeId}/boxes")
	@PreAuthorize("hasAuthority('ADMIN')")
	public List<GameBox> getFormColumnBoxes(@PathVariable(value = "id") int id,
			@PathVariable(value = "columnTypeId") int columnTypeId) {
		return formService.getFormById(id).getColumnByTypeId(columnTypeId).getBoxes();
	}

	@GetMapping("/{id}/columns/{columnTypeId}/boxes/{boxTypeId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public GameBox getFormColumnBoxBoxByType(@PathVariable(value = "id") int id,
			@PathVariable(value = "columnTypeId") int columnTypeId,
			@PathVariable(value = "boxTypeId") int boxTypeId) {
		return formService.getFormById(id).getColumnByTypeId(columnTypeId)
				.getBoxByTypeId(boxTypeId);
	}

	@GetMapping("/{id}/dice")
	@PreAuthorize("hasAuthority('ADMIN')")
	public List<GameDice> getFormDice(@PathVariable(value = "id") int id) {
		return formService.getFormById(id).getDice();
	}
}
