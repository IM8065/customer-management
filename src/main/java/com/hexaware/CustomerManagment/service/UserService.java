package com.hexaware.CustomerManagment.service;

import com.hexaware.CustomerManagment.Customer;
import com.hexaware.CustomerManagment.User;
import com.hexaware.CustomerManagment.data.UserRepository;
import com.hexaware.CustomerManagment.web.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    private static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public Optional<User> findByUsername(String username) {
        LOGGER.info("finding user by username in service using repo");
        Optional<User> foundUser = Optional.ofNullable(userRepo.findByUsername(username));
        return  foundUser;
    }

    public Optional<User> findUserById(long id) {

        LOGGER.info("finding user by id in service using repo");
        return userRepo.findById(id);
    }

    public List<User> findAllUsers() {
        LOGGER.info("finding all users in service using repo");
        return userRepo.findAll();
    }

    public User saveUser(User user) {
        LOGGER.info("saving users in the database using service and repo");
        return userRepo.save(user);
    }

    public void deleteUserById(long id) {
        LOGGER.info("deleting user by id in service using repo");
        userRepo.deleteById(id);
    }

    public HashMap<String, String> validateFields(BindingResult errors) {
        LOGGER.info("Find all errors that might have occured during entity validation");
        HashMap<String, String> errorsList = new HashMap<>();

        if(errors.hasErrors()) {
            errors.getAllErrors().forEach(objectError -> {
                if(objectError instanceof FieldError) {
                    errorsList.put(((FieldError) objectError).getField(), objectError.getDefaultMessage());
                }
            });
        }
        return errorsList;
    }
}
