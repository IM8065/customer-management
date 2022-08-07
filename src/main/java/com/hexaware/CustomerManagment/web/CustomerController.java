package com.hexaware.CustomerManagment.web;

import com.hexaware.CustomerManagment.Customer;
import com.hexaware.CustomerManagment.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@RestController
@RequestMapping(path="/api/customer", produces={"application/json", "text/xml"})
@CrossOrigin
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    Logger logger = Logger.getLogger(CustomerController.class.getName());

    @GetMapping("/get/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("id") Long id){
        try {
            logger.info("Retrieving customer by ID");

            Optional<Customer> customer = customerService.findCustomerById(id);

            if(customer.isPresent()) {
                return new ResponseEntity<>(customer.get(), HttpStatus.OK );
            }

        } catch (Exception e) {
            logger.info("Exception: " + e);

        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND );
    }

    @GetMapping("/list")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        try {
            logger.info("Getting all customers");
            return new ResponseEntity<>(customerService.findAllCustomers(), HttpStatus.ACCEPTED);
        }
        catch (Exception e) {
            logger.info("Exception: " + e);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/listFilter")
    public ResponseEntity<List<Customer>> getAllCustomersFiltered(@RequestParam String filter) {
        try {
            logger.info("Getting customers by filter");
            List<Customer> filteredCustomers = customerService.findAllCustomers().stream()
                    .filter(customer -> customerService.containsString(
                            customer.getFirstName(),
                            customer.getLastName(),
                            filter))
                    .collect(Collectors.toList());

            return new ResponseEntity<List<Customer>>(filteredCustomers, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            logger.info("Exception: " + e);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path="/create", consumes="application/json")
    public ResponseEntity<Customer> createCustomer(@Validated @RequestBody Customer customer,
                                                   BindingResult errors) {
        HashMap<String, String> errorsList = customerService.validateFields(errors);

        if(!errorsList.isEmpty()) {
            return new ResponseEntity(errorsList, HttpStatus.BAD_REQUEST);
        }

        try {
            logger.info("Creating a new customer");
            return new ResponseEntity<>(customerService.saveCustomer(customer), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.info("Exception: " + e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/update/{customerId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity updateCustomer(@PathVariable("customerId") Long customerId,
                                   @Validated @RequestBody Customer customerUpdate,
                                   BindingResult errors) {


        HashMap<String, String> errorsList = customerService.validateFields(errors);

        if(!errorsList.isEmpty()) {
            return new ResponseEntity(errorsList, HttpStatus.BAD_REQUEST);
        }

        try {
            logger.info("Updating a customer by ID");
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
        catch (Exception e) {
            logger.info("Exception: " + e);
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("customerId") Long customerId) {
        try {
            logger.info("Deleting a customer");
            customerService.deleteCustomerById(customerId);
        } catch (EmptyResultDataAccessException e) {
            logger.info("Exception: " + e);
        }
    }
}
