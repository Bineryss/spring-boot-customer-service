package de.binerys.customerservice.repository;

import de.binerys.customerservice.dto.CustomerApiDTO;
import de.binerys.customerservice.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CrudeRepositoryTest {
    @Autowired
    CustomerRepository customerRepository;

    public static List<Customer> createManyCustomers(int count) {
        var manyOrders = new ArrayList<Customer>();
        for (int i = 1; i < count + 1; i++) {
            var customer = Customer.builder()
                    .customerNbr("C%03d".formatted(i))
                    .firstname("Test")
                    .lastname("Mensch")
                    .build();
            manyOrders.add(customer);
        }
        return manyOrders;
    }

    @Test
    void basicCrudTest() {
        var customer = Customer.builder()
                .customerNbr("A01456")
                .firstname("Test")
                .lastname("Mensch")
                .build();
        customerRepository.saveAndFlush(customer);

        var exists = customerRepository.existsById(customer.getId());
        assertTrue(exists);

        var reloadedOrder = customerRepository.findById(customer.getId());
        assertTrue(reloadedOrder.isPresent());
        var count = customerRepository.count();
        assertEquals(1, count);

        customerRepository.deleteById(customer.getId());
        exists = customerRepository.existsById(customer.getId());
        assertFalse(exists);
    }

    @Test
    void findByLastNameTest() {
        var customer = Customer.builder()
                .customerNbr("A01456")
                .firstname("Test")
                .lastname("Mensch")
                .build();
        customerRepository.saveAndFlush(customer);

        var exists = customerRepository.existsById(customer.getId());
        assertTrue(exists);

        var c = customerRepository.queryByLastName("Mensch");
        assertEquals(1, c.size());

        assertEquals("Mensch", c.get(0).getLastname());
    }

    @Test
    void methodQueryCreationTest() {
        customerRepository.saveAll(createManyCustomers(10));
        var customers = customerRepository.queryByLastName("Mensch");
        assertEquals(10, customers.size());
    }

    @Test
    void queryWithPagingTest() {
        customerRepository.saveAllAndFlush(createManyCustomers(20));
        var customers =
                customerRepository.queryByLastNamePaged("Mensch",
                        PageRequest.of(0, 5, Sort.by("customerNbr")));
        assertEquals(5, customers.size());
    }

    @Test
    void projectionTest() {
        customerRepository.saveAllAndFlush(createManyCustomers(10));
        var dtoList = customerRepository.findByFirstname("Test", CustomerApiDTO.class);
        assertEquals(10, dtoList.size());
    }


}
