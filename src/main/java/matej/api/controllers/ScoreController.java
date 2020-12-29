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

import matej.api.services.ScoreService;
import matej.constants.GameConstants;
import matej.models.payload.response.MessageResponse;


@RestController
@CrossOrigin(origins={"*", "http://www.jamb.com.hr", "https://jamb-react.herokuapp.com"})
@RequestMapping("/scores")
public class ScoreController {

    @Autowired
    ScoreService scoreService;

    @GetMapping("")
	public ResponseEntity<Object>  getScores() {
		try {
			return new ResponseEntity<>(scoreService.getScores(), HttpStatus.OK);
		} catch (Exception exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
	
    @GetMapping("/{id}")
	public ResponseEntity<Object> getScoreById(@PathVariable int id) {
        try {
			return new ResponseEntity<>(scoreService.getScoreById(id), HttpStatus.OK);
		} catch (Exception exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> deleteScoreById(@PathVariable int id) {
        try {
            scoreService.deleteScoreById(id);
			return new ResponseEntity<>(new MessageResponse("Rezultat uspješno izbrisan."), HttpStatus.OK);
		} catch (Exception exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/scoreboard")
	public ResponseEntity<Object> getScoreboard() {
		try {
			return new ResponseEntity<>(scoreService.getScoreboard(GameConstants.LEADERBOARD_LIMIT), HttpStatus.OK);
		} catch (Exception exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/leader")
	public ResponseEntity<Object> getCurrentWeekLeader() {
		try {
			return new ResponseEntity<>(scoreService.getCurrentWeekLeader(), HttpStatus.OK);
		} catch (Exception exc) {
			return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
}