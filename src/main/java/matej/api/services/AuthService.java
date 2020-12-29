package matej.api.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import matej.models.AuthRole;
import matej.models.AuthUser;
import matej.models.payload.request.LoginRequest;
import matej.models.payload.request.RegisterRequest;
import matej.models.payload.response.JwtResponse;
import matej.api.repositories.RoleRepository;
import matej.api.repositories.UserRepository;
import matej.api.services.AuthService;
import matej.exceptions.UsernameTakenException;
import matej.components.JwtUtils;
import matej.models.UserDetailsImpl;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepo;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles);
    }

    public void register(RegisterRequest registerRequest) throws UsernameTakenException {
        if (userRepo.existsByUsername(registerRequest.getUsername()))
            throw new UsernameTakenException("Korisničko ime je već zauzeto!");

        AuthUser user = new AuthUser(registerRequest.getUsername(), encoder.encode(registerRequest.getPassword()));

        Set<String> strRoles = registerRequest.getRole();
        Set<AuthRole> roles = new HashSet<>();

        if (strRoles == null) {
            AuthRole userRole = roleRepo.findByLabel("USER")
                    .orElseThrow(() -> new RuntimeException("Uloga nije pronađena."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        AuthRole adminRole = roleRepo.findByLabel("ADMIN")
                                .orElseThrow(() -> new RuntimeException("Admin uloga nije pronađena."));
                        roles.add(adminRole);
                        break;
                    default:
                        AuthRole userRole = roleRepo.findByLabel("USER")
                                .orElseThrow(() -> new RuntimeException("Uloga nije pronađena."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepo.save(user);
    }

}
