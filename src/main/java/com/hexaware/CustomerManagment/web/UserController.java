package com.hexaware.CustomerManagment.web;

import com.hexaware.CustomerManagment.Login;
import com.hexaware.CustomerManagment.User;
import com.hexaware.CustomerManagment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path="/api/auth", produces={"application/json", "text/xml"})
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<User> authenticateUser(@RequestBody Login login)  {
        Optional<User> foundUser = userService.findByUsername(login.getUsername());
        if(foundUser.isPresent()) {
            return new ResponseEntity<>(foundUser.get(), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody User user) {
        return  userService.saveUser(user);
    }

    @PatchMapping("/update/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public User updateUser(@PathVariable long id, @RequestBody User updatedUser) {
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
        oldUser.setCanUpdate(updatedUser.isCanUpdate());
        oldUser.setCanDelete(updatedUser.isCanDelete());

        return userService.saveUser(oldUser);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("userId") long userId) {
        try {
            userService.deleteUserById(userId);
        } catch (EmptyResultDataAccessException e) {}
    }
}
