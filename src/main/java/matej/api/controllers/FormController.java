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
import matej.models.types.BoxType;
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

	@PutMapping("/{formId}/roll")
	public ResponseEntity<Object> rollDice(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "formId") int formId, @RequestBody Map<Integer, Boolean> diceToThrow) {
		try {
			return new ResponseEntity<>(
					formService.rollDice(jwtUtils.getUsernameFromHeader(headerAuth), formId, diceToThrow), HttpStatus.OK);
		} catch (InvalidOwnershipException | IllegalMoveException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/{formId}/announce")
	public ResponseEntity<Object> announce(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "formId") int formId, @RequestBody BoxType boxType) {
		try {
			return new ResponseEntity<>(
					formService.announce(jwtUtils.getUsernameFromHeader(headerAuth), formId, boxType),
					HttpStatus.OK);
		} catch (IllegalMoveException | InvalidOwnershipException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/{formId}/columns/{columnTypeId}/boxes/{boxTypeId}/fill")
	public ResponseEntity<Object> fillBox(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "formId") int formId, @PathVariable(value = "columnTypeId") int columnTypeId,
			@PathVariable(value = "boxTypeId") int boxTypeId) {
		try {
			return new ResponseEntity<>(formService.fillBox(jwtUtils.getUsernameFromHeader(headerAuth), formId,
					columnTypeId, boxTypeId), HttpStatus.OK);
		} catch (IllegalMoveException | InvalidOwnershipException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/{formId}")
	public ResponseEntity<Object> deleteFormById(@RequestHeader(value = "Authorization") String headerAuth,
			@PathVariable(value = "formId") int formId) throws InvalidOwnershipException {
		try {
			formService.deleteFormById(jwtUtils.getUsernameFromHeader(headerAuth), formId);
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

	@GetMapping("/{formId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public GameForm getFormById(@PathVariable(value = "formId") int formId) {
		return formService.getFormById(formId);
	}

	@GetMapping("/{formId}/columns")
	@PreAuthorize("hasAuthority('ADMIN')")
	public List<GameColumn> getFormColumns(@PathVariable(value = "formId") int formId) {
		return formService.getFormById(formId).getColumns();
	}

	@GetMapping("/{formId}/columns/{columnTypeId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public GameColumn getFormColumnByType(@PathVariable(value = "formId") int formId,
			@PathVariable(value = "columnTypeId") int columnTypeId) {
		return formService.getFormById(formId).getColumnByTypeId(columnTypeId);
	}

	@GetMapping("/{formId}/columns/{columnTypeId}/boxes")
	@PreAuthorize("hasAuthority('ADMIN')")
	public List<GameBox> getFormColumnBoxes(@PathVariable(value = "formId") int formId,
			@PathVariable(value = "columnTypeId") int columnTypeId) {
		return formService.getFormById(formId).getColumnByTypeId(columnTypeId).getBoxes();
	}

	@GetMapping("/{formId}/columns/{columnTypeId}/boxes/{boxTypeId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public GameBox getFormColumnBoxBoxByType(@PathVariable(value = "formId") int formId,
			@PathVariable(value = "columnTypeId") int columnTypeId,
			@PathVariable(value = "boxTypeId") int boxTypeId) {
		return formService.getFormById(formId).getColumnByTypeId(columnTypeId)
				.getBoxByTypeId(boxTypeId);
	}

	@GetMapping("/{formId}/dice")
	@PreAuthorize("hasAuthority('ADMIN')")
	public List<GameDice> getFormDice(@PathVariable(value = "formId") int formId) {
		return formService.getFormById(formId).getDice();
	}
}
