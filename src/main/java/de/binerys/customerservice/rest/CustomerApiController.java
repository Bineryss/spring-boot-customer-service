package de.binerys.customerservice.rest;

import de.binerys.customerservice.dto.CustomerApiDTO;
import de.binerys.customerservice.entity.CustomerMapper;
import de.binerys.customerservice.repository.CustomerRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/customer")
public class CustomerApiController {

    CustomerRepository customerRepository;
    CustomerMapper customerMapper;

    public CustomerApiController(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @GetMapping
    public List<CustomerApiDTO> findAll() {
        return customerRepository.findAllProjectedBy(CustomerApiDTO.class);
    }

    @GetMapping(params = {"page"})
    public List<CustomerApiDTO> findAllPaged(@PageableDefault(sort = "customerNbr") Pageable pageable) {
        return customerRepository.findAllProjectedBy(CustomerApiDTO.class, pageable);
    }

    @GetMapping("/{customerNbr}")
    public CustomerApiDTO findByCustomerNbr(@PathVariable("customerNbr") String customerNbr) {
        return customerRepository.findProjectionsByCustomerNbr(customerNbr, CustomerApiDTO.class).orElseThrow(() -> new EntityNotFoundException("Not found"));
    }

    @PostMapping
    public CustomerApiDTO insert(@RequestBody @Valid CustomerApiDTO customerApiDTO) {
        var customerEntity = customerMapper.map(customerApiDTO);
        customerEntity = customerRepository.save(customerEntity);
        return customerMapper.map(customerEntity);
    }
}
