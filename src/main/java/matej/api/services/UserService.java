package matej.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import matej.api.repositories.UserRepository;
import matej.models.Score;
import matej.models.User;

@Service
public class UserService {

    @Autowired
    UserRepository userRepo;

    public List<User> getUsers() {
        return userRepo.findAll();
    }

    public void deleteUserById(int id) {
        userRepo.deleteById(id);
    }

    public User getUserById(int id) {
        return userRepo.getOne(id);
    }

    public List<Score> getUserScores(int id) {
        return userRepo.getOne(id).getScores();
    }
}