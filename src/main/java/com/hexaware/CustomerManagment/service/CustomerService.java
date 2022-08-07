package com.hexaware.CustomerManagment.service;

import com.hexaware.CustomerManagment.Customer;
import com.hexaware.CustomerManagment.data.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepo;

    public CustomerService(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    public Optional<Customer> findCustomerById(long id) {
        return customerRepo.findById(id);
    }

    public List<Customer> findAllCustomers() {
        return customerRepo.findAll();
    }

    public Customer saveCustomer(Customer customer){
        return customerRepo.save(customer);
    }

    public void deleteCustomerById(long id) {
        customerRepo.deleteById(id);
    }

    public boolean containsString(String firstName, String lastName, String filterString) {
        return firstName.contains(filterString) || lastName.contains(filterString);
    }

    public HashMap<String, String> validateFields(BindingResult errors) {
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
