package com.hexaware.CustomerManagment.data;

import com.hexaware.CustomerManagment.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u from User u WHERE u.username = ?1")
    User findByUsername(String username);
}
