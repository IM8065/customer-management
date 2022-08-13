package com.hexaware.CustomerManagment.web;

import com.hexaware.CustomerManagment.Customer;
import com.hexaware.CustomerManagment.User;
import com.hexaware.CustomerManagment.service.CustomerService;
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
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping(path="/api/customer", produces={"application/json", "text/xml"})
@CrossOrigin
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    private static Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);


    @GetMapping("/get/{id}")
    public ResponseEntity getCustomer(@PathVariable("id") Long id){
        LOGGER.info("Entering getCustomer()");
        HashMap<String, String> errorsList = new HashMap<>();
        try {
            LOGGER.info("Retrieving customer by ID");

            Optional<Customer> customer = customerService.findCustomerById(id);

            if(customer.isPresent()) {
                return new ResponseEntity<>(customer.get(), HttpStatus.OK );
            }

        }
        catch(EntityNotFoundException ex) {
            LOGGER.error("Exception: " + ex);

        }
        catch (Exception e) {
            LOGGER.error("Exception: " + e);
        }
        LOGGER.info("Exiting getCustomer()");
        errorsList.put("message","Not able to get customer with id: " + id);
        errorsList.put("status", HttpStatus.NOT_FOUND.toString());
        return new ResponseEntity<>(errorsList, HttpStatus.NOT_FOUND );
    }

    @GetMapping("/list")
    public ResponseEntity getAllCustomers() {
        LOGGER.info("Entering getAllCustomer()");
        HashMap<String, String> errorsList = new HashMap<>();
        try {
            LOGGER.info("Getting all customers");
            return new ResponseEntity<>(customerService.findAllCustomers(), HttpStatus.OK);
        }
        catch (Exception e) {
            LOGGER.error("Exception: " + e);
            LOGGER.info("Exiting getCustomer()");
            errorsList.put("message", "Not able to get all customers: " + e);
            errorsList.put("status", HttpStatus.NOT_FOUND.toString());
            return new ResponseEntity<>(errorsList, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/listFilter")
    public ResponseEntity getAllCustomersFiltered(@RequestParam String filter) {
        LOGGER.info("Entering getAllCustomersFiltered()");
        HashMap<String, String> errorsList = new HashMap<>();
        try {
            LOGGER.info("Getting customers by filter");
            List<Customer> filteredCustomers = customerService.findAllCustomers().stream()
                    .filter(customer -> customerService.containsString(
                            customer.getFirstName().toLowerCase(),
                            customer.getLastName().toLowerCase(),
                            filter.toLowerCase()))
                    .collect(Collectors.toList());

            return new ResponseEntity<List<Customer>>(filteredCustomers, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Exception: " + e);
            LOGGER.info("Exiting getAllCustomersFiltered()");
            errorsList.put("message", "Could not filter customers: " + e);
            errorsList.put("status", HttpStatus.NOT_FOUND.toString());
            return new ResponseEntity<>(errorsList, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path="/create", consumes="application/json")
    public ResponseEntity createCustomer(@Validated @RequestBody Customer customer,
                                                   BindingResult errors,
                                                   @RequestHeader(value = "username") String userName,
                                                   @RequestHeader(value="password") String password) {
        LOGGER.info("Entering createCustomer()");
        HashMap<String, String> errorsList = customerService.validateFields(errors);
        Optional<User> userOpt = userService.findByUsername(userName);
        if(userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            if (!errorsList.isEmpty()) {
                errorsList.put("status", HttpStatus.BAD_REQUEST.toString());
                return new ResponseEntity(errorsList, HttpStatus.BAD_REQUEST);
            }

            try {
                LOGGER.info("Creating a new customer");
                return new ResponseEntity<>(customerService.saveCustomer(customer), HttpStatus.CREATED);
            }
            catch(IllegalArgumentException e) {
                LOGGER.error("Exception: " + e);
                errorsList.put("message", "One or more of your customer fields are invalid: " + e);
                errorsList.put("status", HttpStatus.BAD_REQUEST.toString());
                return new ResponseEntity(errorsList, HttpStatus.BAD_REQUEST);
            }
            catch (Exception e) {
                LOGGER.error("Exception: " + e);
                errorsList.put("message", "Could not create customer: " + e);
                errorsList.put("status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
                return new ResponseEntity<>(errorsList, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        LOGGER.info("Exiting createCustomer()");
        errorsList.put("message", "You are not Authorized to perform this action");
        errorsList.put("status", HttpStatus.UNAUTHORIZED.toString());
        return new ResponseEntity<>(errorsList, HttpStatus.UNAUTHORIZED);
    }

    @PatchMapping("/update/{customerId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity updateCustomer(@PathVariable("customerId") Long customerId,
                                   @Validated @RequestBody Customer customerUpdate,
                                   BindingResult errors,
                                         @RequestHeader(value = "username") String userName,
                                         @RequestHeader(value="password") String password) {

        LOGGER.info("Entering updateCustomer()");
        HashMap<String, String> errorsList = customerService.validateFields(errors);
        Optional<User> userOpt = userService.findByUsername(userName);
        if(userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            if(!errorsList.isEmpty()) {
                errorsList.put("status", HttpStatus.BAD_REQUEST.toString());
                return new ResponseEntity(errorsList, HttpStatus.BAD_REQUEST);
            }

            try {
                LOGGER.info("Updating a customer by ID");
                Customer oldCustomer = customerService.findCustomerById(customerId).get();

                if(customerUpdate.getFirstName() != null) {
                    oldCustomer.setFirstName(customerUpdate.getFirstName());
                }
                if(customerUpdate.getLastName() != null) {
                    oldCustomer.setLastName(customerUpdate.getLastName());
                }
                if(customerUpdate.getAddress() != null) {
                    oldCustomer.setAddress(customerUpdate.getAddress());
                }
                if(customerUpdate.getEmail() != null) {
                    oldCustomer.setEmail(customerUpdate.getEmail());
                }
                if(customerUpdate.getBalance() != 0) {
                    oldCustomer.setBalance(customerUpdate.getBalance());
                }

                return new ResponseEntity<>(customerService.saveCustomer(oldCustomer), HttpStatus.ACCEPTED);
            }
            catch(EntityNotFoundException ex) {
                LOGGER.error("Exception: " + ex);
                errorsList.put("message", "Was not able to find specified customer");
                errorsList.put("status", HttpStatus.BAD_REQUEST.toString());
                return new ResponseEntity(errorsList, HttpStatus.BAD_REQUEST);
            }
            catch (Exception e) {
                LOGGER.error("Exception: " + e);
                errorsList.put("message", "Was not able to update customer: " + e);
                errorsList.put("status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
                return new ResponseEntity(errorsList, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        errorsList.put("message", "You are not Authorized to perform this action");
        LOGGER.info("Exiting updateCustomer()");
        return new ResponseEntity(errorsList, HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity deleteCustomer(@PathVariable("customerId") Long customerId,
                               @RequestHeader(value = "username") String userName,
                               @RequestHeader(value="password") String password) {
        LOGGER.info("Entering deleteCustomer()");
        Optional<User> userOpt = userService.findByUsername(userName);
        HashMap<String, String> errorsList = new HashMap<>();
        if(userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            try {
                LOGGER.info("Deleting a customer");
                customerService.deleteCustomerById(customerId);
            }
            catch(EntityNotFoundException ex) {
                LOGGER.error("Exception: " + ex);
            }
            catch (EmptyResultDataAccessException e) {
                LOGGER.error("Exception: " + e);
                errorsList.put("message", "Not able to find customer");
                errorsList.put("status", HttpStatus.NO_CONTENT.toString());
                return new ResponseEntity(errorsList, HttpStatus.NO_CONTENT);
            }
        }
        LOGGER.info("Exiting deleteCustomer()");
        errorsList.put("message", "You are not Authorized to perform this action");
        errorsList.put("status", HttpStatus.UNAUTHORIZED.toString());
        return new ResponseEntity(errorsList, HttpStatus.UNAUTHORIZED);
    }
}
