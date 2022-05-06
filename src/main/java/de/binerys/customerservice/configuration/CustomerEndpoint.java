package de.binerys.customerservice.configuration;

import de.binerys.customerservice.repository.CustomerRepository;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "customer") // @WebEndpoint, @JmxEndpoint
public class CustomerEndpoint {
    private final CustomerRepository customerRepository;

    public CustomerEndpoint(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @ReadOperation
    public long getCustomerCount() {
        return customerRepository.count();
    }
}
