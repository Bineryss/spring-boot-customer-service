package de.binerys.customerservice.repository;

import de.binerys.customerservice.entity.Customer;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Transactional(readOnly = true)
public class NaturalCustomerRepositoryImpl implements NaturalCustomerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Customer> findByNaturalId(String naturalId) {
        return entityManager.unwrap(Session.class)
                .bySimpleNaturalId(Customer.class)
                .loadOptional(naturalId);
    }
}
