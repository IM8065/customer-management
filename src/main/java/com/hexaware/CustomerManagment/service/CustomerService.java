package com.hexaware.CustomerManagment.service;

import com.hexaware.CustomerManagment.Customer;
import com.hexaware.CustomerManagment.data.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
