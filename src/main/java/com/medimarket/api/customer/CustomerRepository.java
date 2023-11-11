package com.medimarket.api.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository  extends JpaRepository<CustomerToken, Integer> {
    CustomerToken findByToken(String token);
    Optional<CustomerToken> findByUserId(int user_id);
}
