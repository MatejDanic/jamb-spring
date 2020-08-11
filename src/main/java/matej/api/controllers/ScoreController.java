package matej.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import matej.api.services.ScoreService;
import matej.constants.JambConstants;
import matej.models.Score;


@RestController
@CrossOrigin(origins={"*", "http://www.jamb.com.hr", "https://jamb-react.herokuapp.com"})
@RequestMapping("/scores")
public class ScoreController {

    @Autowired
    ScoreService scoreService;

    @GetMapping("")
	public List<Score> getScores() {
		return scoreService.getScores();
	}
	
    @GetMapping("/{id}")
	public Score getScoreById(@PathVariable int id) {
		return scoreService.getScoreById(id);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public void deleteScoreById(@PathVariable int id) {
		scoreService.deleteScoreById(id);
	}
	
	@GetMapping("/board")
	public List<Score> getScoreboard() {
		return scoreService.getScoreboard(JambConstants.LEADERBOARD_LIMIT);
    }
}