package com.hexaware.CustomerManagment.service;

import com.hexaware.CustomerManagment.Customer;
import com.hexaware.CustomerManagment.data.CustomerRepository;
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
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepo;

    private static Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

    public CustomerService(CustomerRepository customerRepo) {
        LOGGER.info("Customer Repository bean");
        this.customerRepo = customerRepo;
    }

    public Optional<Customer> findCustomerById(long id) {
        LOGGER.info("find customer by id in service using repository");
        return customerRepo.findById(id);
    }

    public List<Customer> findAllCustomers() {
        LOGGER.info("find all customers in service using repository");
        return customerRepo.findAll();
    }

    public Customer saveCustomer(Customer customer){

        LOGGER.info("save the customer, inside the service using repository");
        return customerRepo.save(customer);
    }

    public void deleteCustomerById(long id) {
        LOGGER.info("delete customer by id in service using repository");
        customerRepo.deleteById(id);
    }

    public boolean containsString(String firstName, String lastName, String filterString) {
        LOGGER.info("check if string contains substring");
        return firstName.contains(filterString) || lastName.contains(filterString);
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
