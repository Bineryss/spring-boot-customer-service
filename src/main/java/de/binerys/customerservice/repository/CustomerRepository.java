package de.binerys.customerservice.repository;

import de.binerys.customerservice.dto.CustomerApiDTO;
import de.binerys.customerservice.entity.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID>, NaturalCustomerRepository {

    @Query("FROM Customer WHERE lastname = :lastname")
    List<Customer> queryByLastName(@Param("lastname") String lastname);

    //multiline string mit 3 "
    @Query("""
            FROM Customer
            WHERE lastname = :lastname
            """)
    List<Customer> queryByLastNamePaged(@Param("lastname") String lastname, Pageable pageable);

    //hier kein @Query, da es dann nicht mehr generisch funktioniert!
    <T> List<T> findByFirstname(@Param("firstname") String firstname, Class<T> type);

    <T> List<T> findAllProjectedBy(Class<T> type);
    <T> List<T> findAllProjectedBy(Class<T> customerApiDTOClass, Pageable pageable);

    <T> Optional<T> findProjectionsByCustomerNbr(String customerNbr, Class<T> type);
}
