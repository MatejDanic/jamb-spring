package matej.api.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import matej.api.services.ScoreService;
import matej.constants.JambConstants;


@RestController
@CrossOrigin(origins="*")
@RequestMapping("")
public class HomeController {
	
	@Autowired
	ScoreService scoreService;
	
	@GetMapping("")
	public void handleGet(HttpServletResponse response) {
		response.setHeader("Location", "http://www.jamb.com.hr");
		response.setStatus(302);
	}

	@GetMapping("/wake")
	public String wake() {
		return "yawn...";
	}
	
	@GetMapping("/scores")
	public ResponseEntity<Object> getLeaderboard() {
		try {
			return new ResponseEntity<>(scoreService.getLeaderboard(JambConstants.LEADERBOARD_LIMIT), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/admin")
	@PreAuthorize("hasAuthority('ADMIN')")
	public String adminAccess() {
		return "You are an admin";
	}
}


