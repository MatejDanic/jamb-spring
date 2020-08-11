package matej.api.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import matej.api.services.ScoreService;


@RestController
@CrossOrigin(origins={"*", "http://www.jamb.com.hr", "https://jamb-react.herokuapp.com"})
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
	
	@GetMapping("/admin")
	@PreAuthorize("hasAuthority('ADMIN')")
	public String adminAccess() {
		return "You are an admin";
	}
}


