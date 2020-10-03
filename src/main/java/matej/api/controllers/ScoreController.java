package matej.api.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
	public GameScore getScoreById(@PathVariable int id) {
		return scoreService.getScoreById(id);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public void deleteScoreById(@PathVariable int id) {
		scoreService.deleteScoreById(id);
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