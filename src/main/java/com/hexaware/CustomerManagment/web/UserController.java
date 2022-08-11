package com.hexaware.CustomerManagment.web;

import com.hexaware.CustomerManagment.Login;
import com.hexaware.CustomerManagment.User;
import com.hexaware.CustomerManagment.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping(path="/api/auth", produces={"application/json", "text/xml"})
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    private static Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/login")
    public ResponseEntity<User> authenticateUser(@RequestBody Login login)  {
        try {
            LOGGER.info("Authenticating User");
            Optional<User> foundUser = userService.findByUsername(login.getUsername());
            if(foundUser.isPresent()) {
                return new ResponseEntity<>(foundUser.get(), HttpStatus.ACCEPTED);
            }
        }
        catch(EntityNotFoundException ex) {
            LOGGER.error("Exception: " + ex);
        }
        catch (Exception e) {
            LOGGER.error("Exception: " + e);

        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            LOGGER.info("Getting all users");
            return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.ACCEPTED);
        }
        catch (Exception e) {
            LOGGER.error("Exception: " + e);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Validated @RequestBody User user,
                                         BindingResult errors,
                                         @RequestHeader(value = "username") String userName,
                                         @RequestHeader(value = "password") String password) {
        HashMap<String, String> errorsList = userService.validateFields(errors);
        Optional<User> userOpt = userService.findByUsername(userName);

        if(userOpt.isPresent()
                && userOpt.get().getPassword().equals(password)
                && userOpt.get().getRole().equals("admin")
        ){
            if(!errorsList.isEmpty()) {
                return new ResponseEntity(errorsList, HttpStatus.BAD_REQUEST);
            }

            try {
                LOGGER.info("Creating new user");
                return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
            }
            catch(IllegalArgumentException e) {
                LOGGER.error("Exception: " + e);
            }
            catch(Exception e) {
                LOGGER.error("Exception: " + e);
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    @PatchMapping("/update/{userId}")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public ResponseEntity<User> updateUser(@PathVariable Long id,
//                           @Validated @RequestBody User updatedUser,
//                           BindingResult errors) {
//
//        HashMap<String, String> errorsList = userService.validateFields(errors);
//
//        if(!errorsList.isEmpty()) {
//            return new ResponseEntity(errorsList, HttpStatus.BAD_REQUEST);
//        }
//
//        try {
//            logger.info("Updating a user by ID");
//            User oldUser = userService.findUserById(id).get();
//
//            if(oldUser.getUsername() != null) {
//                oldUser.setUsername(updatedUser.getUsername());
//            }
//            if(oldUser.getPassword() != null){
//                oldUser.setPassword(updatedUser.getPassword());
//            }
//            if(oldUser.getRole() != null){
//                oldUser.setRole(updatedUser.getRole());
//            }
//
//            return new ResponseEntity<>(userService.saveUser(oldUser), HttpStatus.ACCEPTED);
//        }
//        catch (Exception e) {
//            logger.info("Exception: " + e);
//            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//        }
//    }

//    @DeleteMapping("/{userId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteUser(@PathVariable("userId") Long userId) {
//        try {
//            logger.info("Deleting User");
//            userService.deleteUserById(userId);
//        } catch (EmptyResultDataAccessException e) {
//            logger.info("Exception: " + e);
//        }
//    }
}
