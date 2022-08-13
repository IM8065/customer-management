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
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

@RestController
@RequestMapping(path="/api/auth", produces={"application/json", "text/xml"})
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    private static Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/login")
    public ResponseEntity authenticateUser(@RequestBody Login login) {
        LOGGER.info("Entering authenticateUser()");
        HashMap<String, String> errorsList = new HashMap<>();
        try {
            LOGGER.info("Authenticating User");
            Optional<User> foundUser = userService.findByUsername(login.getUsername());
            if (foundUser.isPresent()) {
                return new ResponseEntity<>(foundUser.get(), HttpStatus.ACCEPTED);
            }
        } catch (EntityNotFoundException ex) {
            LOGGER.error("Exception: " + ex);
            errorsList.put("message", "could not find user: " + login.getUsername());
            errorsList.put("status", HttpStatus.NOT_FOUND.toString());
            return new ResponseEntity(errorsList, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            LOGGER.error("Exception: " + e);
            errorsList.put("message", "There was a problem authenticating user: " + login.getUsername());
            errorsList.put("status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
            return new ResponseEntity(errorsList, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        LOGGER.info("Exiting authenticateUser()");
        errorsList.put("message", "Was not able to find user with username: " + login.getUsername());
        errorsList.put("status", HttpStatus.NOT_FOUND.toString());
        return new ResponseEntity<>(errorsList, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/list")
    public ResponseEntity getAllUsers() {
        LOGGER.info("Entering getAllUsers()");
        HashMap<String, String> errorsList = new HashMap<>();
        try {
            LOGGER.info("Getting all users");
            return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.ACCEPTED);
        } catch (NoResultException e) {
            LOGGER.error("Exception: " + e);
            errorsList.put("message", "Could not find any users");
            errorsList.put("status", HttpStatus.NOT_FOUND.toString());
            return new ResponseEntity<>(errorsList, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            LOGGER.error("Exception: " + e);
            LOGGER.info("Exiting getAllUsers()");
            errorsList.put("message", "There was an error finding users: " + e);
            errorsList.put("status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
            return new ResponseEntity<>(errorsList, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@Validated @RequestBody User user,
                                   BindingResult errors,
                                   @RequestHeader(value = "username") String userName,
                                   @RequestHeader(value = "password") String password) {
        LOGGER.info("Entering register()");
        HashMap<String, String> errorsList = userService.validateFields(errors);
        Optional<User> userOpt = userService.findByUsername(userName);

        if (userOpt.isPresent()
                && userOpt.get().getPassword().equals(password)
                && userOpt.get().getRole().equals("admin")
        ) {
            if (!errorsList.isEmpty()) {
                errorsList.put("status", HttpStatus.BAD_REQUEST.toString());
                return new ResponseEntity(errorsList, HttpStatus.BAD_REQUEST);
            }

            try {
                LOGGER.info("Creating new user");
                return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
            } catch (Exception e) {
                LOGGER.error("Exception: " + e);
                errorsList.put("message", "You are not Authorized to perform this action");
                errorsList.put("status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
                return new ResponseEntity<>(errorsList, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        LOGGER.info("Exiting register()");
        errorsList.put("message", "You are not Authorized to perform this action");
        errorsList.put("status", HttpStatus.UNAUTHORIZED.toString());
        return new ResponseEntity<>(errorsList, HttpStatus.UNAUTHORIZED);
    }

}