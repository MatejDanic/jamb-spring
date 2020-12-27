package matej.api.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import matej.payload.response.MessageResponse;
import matej.payload.request.LoginRequest;
import matej.payload.request.RegisterRequest;
import matej.api.services.AuthService;

@RestController
@CrossOrigin(origins = { "*", "http://www.jamb.com.hr", "https://jamb-react.herokuapp.com" })
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            authService.login(loginRequest);
            return new ResponseEntity<>(authService.login(loginRequest), HttpStatus.OK);
        } catch (Exception exc) {
            return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            authService.register(registerRequest);
            return new ResponseEntity<>(new MessageResponse("Korisnik uspješno registriran!"), HttpStatus.OK);
        } catch (Exception exc) {
            return new ResponseEntity<>(new MessageResponse(exc.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}