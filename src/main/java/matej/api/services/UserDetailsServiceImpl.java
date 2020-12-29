package matej.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import matej.models.AuthUser;
import matej.models.UserDetailsImpl;
import matej.api.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AuthUser user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Korisnik s imenom " + username + " nije pronađen."));

		return UserDetailsImpl.build(user);
	}

}