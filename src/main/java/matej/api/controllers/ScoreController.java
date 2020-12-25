package matej.api.controllers;

import java.util.List;
import java.util.Map;

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
import matej.models.GameScore;
import matej.models.MessageResponse;


@RestController
@CrossOrigin(origins={"*", "http://www.jamb.com.hr", "https://jamb-react.herokuapp.com"})
@RequestMapping("/scores")
public class ScoreController {

    @Autowired
    ScoreService scoreService;

    @GetMapping("")
	public List<GameScore> getScores() {
		return scoreService.getScores();
	}
	
    @GetMapping("/{id}")
	public ResponseEntity<Object> getScoreById(@PathVariable int id) {
        try {
            GameScore score = scoreService.getScoreById(id);
			return new ResponseEntity<>(score, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> deleteScoreById(@PathVariable int id) {
        try {
            scoreService.deleteScoreById(id);
			return new ResponseEntity<>(new MessageResponse("Rezultat uspješno izbrisan."), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/scoreboard")
	public List<Map<String, String>> getScoreboard() {
		return scoreService.getScoreboard(GameConstants.LEADERBOARD_LIMIT);
	}
	
	@GetMapping("/leader")
	public String getCurrentWeekLeader() {
		return scoreService.getCurrentWeekLeader();
	}
}