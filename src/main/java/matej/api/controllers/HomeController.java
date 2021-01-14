package matej.api.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = { "*", "http://www.jamb.com.hr", "https://jamb-react.herokuapp.com" })
@RequestMapping("")
public class HomeController {
	
	@GetMapping("/api")
	public void home(HttpServletResponse response) {
		try {
			response.sendRedirect("/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
