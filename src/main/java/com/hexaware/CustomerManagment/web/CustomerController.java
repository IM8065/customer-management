package com.hexaware.CustomerManagment.web;

import com.hexaware.CustomerManagment.Customer;
import com.hexaware.CustomerManagment.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path="/api/customer", produces={"application/json", "text/xml"})
@CrossOrigin
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/get/{id}")
    public ResponseEntity<Customer> getCustomer(@PathParam("id") long id){
        Optional<Customer> customer = customerService.findCustomerById(id);
        if(customer.isPresent()) {
            return new ResponseEntity<>(customer.get(), HttpStatus.OK );
        }

        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND );
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public List<Customer> getAllCustomers() {
        return customerService.findAllCustomers();
    }

    @PostMapping(path="/create", consumes="application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Customer createClaim(@RequestBody Customer customer) {
        return customerService.saveCustomer(customer);
    }

    @PatchMapping("/update/{customerId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Customer updateCustomer(@PathVariable("customerId") long customerId,
                                   @RequestBody Customer customerUpdate) {
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

        return customerService.saveCustomer(oldCustomer);
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("customerId") long customerId) {
        try {
            customerService.deleteCustomerById(customerId);
        } catch (EmptyResultDataAccessException e) {}
    }
}
