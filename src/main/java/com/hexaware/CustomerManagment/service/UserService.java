package com.hexaware.CustomerManagment.service;

import com.hexaware.CustomerManagment.User;
import com.hexaware.CustomerManagment.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    public Optional<User> findByUsername(String username) {
        Optional<User> foundUser = Optional.ofNullable(userRepo.findByUsername(username));
        return  foundUser;
    }

    public Optional<User> findUserById(long id) {
        return userRepo.findById(id);
    }

    public User saveUser(User user) {
        return userRepo.save(user);
    }

    public void deleteUserById(long id) {
        userRepo.deleteById(id);
    }
}
