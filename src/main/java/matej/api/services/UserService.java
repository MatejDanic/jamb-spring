package matej.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import matej.api.repositories.PreferenceRepository;
import matej.api.repositories.UserRepository;
import matej.models.AuthUser;
import matej.models.GameScore;
import matej.models.Preference;
import matej.payload.request.PreferenceRequest;

/**
 * Service Class for managing {@link User} repostiory
 *
 * @author MatejDanic
 * @version 1.0
 * @since 2020-08-20
 */
@Service
public class UserService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    PreferenceRepository prefRepo;

    public Preference getUserPreference(int userId) {
        return userRepo.findById(userId).get().getPreference();
    }

    public Preference updateUserPreference(int userId, PreferenceRequest prefRequest) {
        AuthUser user = userRepo.findById(userId).get();
        Preference preference = user.getPreference();
        if (preference != null) {
            if (prefRequest.getVolume() != null) {
                preference.setVolume(prefRequest.getVolume());
            }
        } else {
            preference = new Preference();
            preference.setUser(user);
            if (prefRequest.getVolume() != null) {
                if (prefRequest.getVolume() < 0 || prefRequest.getVolume() > 3) throw new IllegalArgumentException("Glasnoća zvuka mora biti unutar granica! [0-3]");
                preference.setVolume(prefRequest.getVolume());
            }
        }
        user.setPreference(preference);
        userRepo.save(user);
        return user.getPreference();
    }

    public List<AuthUser> getUsers() {
        return userRepo.findAll();
    }

    public void deleteUserById(int id) {
        userRepo.deleteById(id);
    }

    public AuthUser getUserById(int id) {
        return userRepo.findById(id).get();
    }

    public List<GameScore> getUserScores(int id) {
        return userRepo.findById(id).get().getScores();
    }

    public boolean checkFormOwnership(String username, int formId) {
        AuthUser user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Korisnik s imenom " + username + " nije pronađen."));
        return user.getForm().getId() == formId;
    }

    public boolean checkOwnership(String username, int userId) {
        AuthUser user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Korisnik s imenom " + username + " nije pronađen."));
        return user.getId() == userId;
    }
}