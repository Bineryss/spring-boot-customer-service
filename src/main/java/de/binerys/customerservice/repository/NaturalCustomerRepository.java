package de.binerys.customerservice.repository;

import de.binerys.customerservice.entity.Customer;

import java.util.Optional;


public interface NaturalCustomerRepository {
    Optional<Customer> findByNaturalId(String naturalId);
}
