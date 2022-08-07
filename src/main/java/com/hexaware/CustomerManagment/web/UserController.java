package com.hexaware.CustomerManagment.web;

import com.hexaware.CustomerManagment.Login;
import com.hexaware.CustomerManagment.User;
import com.hexaware.CustomerManagment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping(path="/api/auth", produces={"application/json", "text/xml"})
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    Logger logger = Logger.getLogger(UserController.class.getName());

    @PostMapping("/login")
    public ResponseEntity<User> authenticateUser(@RequestBody Login login)  {
        try {
            logger.info("Authenticating User");
            Optional<User> foundUser = userService.findByUsername(login.getUsername());
            if(foundUser.isPresent()) {
                return new ResponseEntity<>(foundUser.get(), HttpStatus.ACCEPTED);
            }
        }
        catch (Exception e) {
            logger.info("Exception: " + e);

        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            logger.info("Getting all users");
            return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.ACCEPTED);
        }
        catch (Exception e) {
            logger.info("Exception: " + e);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Validated @RequestBody User user, BindingResult errors) {
        HashMap<String, String> errorsList = userService.validateFields(errors);

        if(!errorsList.isEmpty()) {
            return new ResponseEntity(errorsList, HttpStatus.BAD_REQUEST);
        }
        try {
            logger.info("Creating new user");
            return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
        }
        catch(Exception e) {
            logger.info("Exception: " + e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/update/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<User> updateUser(@PathVariable Long id,
                           @Validated @RequestBody User updatedUser,
                           BindingResult errors) {

        HashMap<String, String> errorsList = userService.validateFields(errors);

        if(!errorsList.isEmpty()) {
            return new ResponseEntity(errorsList, HttpStatus.BAD_REQUEST);
        }

        try {
            logger.info("Updating a user by ID");
            User oldUser = userService.findUserById(id).get();

            if(oldUser.getUsername() != null) {
                oldUser.setUsername(updatedUser.getUsername());
            }
            if(oldUser.getPassword() != null){
                oldUser.setPassword(updatedUser.getPassword());
            }
            if(oldUser.getRole() != null){
                oldUser.setRole(updatedUser.getRole());
            }

            return new ResponseEntity<>(userService.saveUser(oldUser), HttpStatus.ACCEPTED);
        }
        catch (Exception e) {
            logger.info("Exception: " + e);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("userId") Long userId) {
        try {
            logger.info("Deleting User");
            userService.deleteUserById(userId);
        } catch (EmptyResultDataAccessException e) {
            logger.info("Exception: " + e);
        }
    }
}
