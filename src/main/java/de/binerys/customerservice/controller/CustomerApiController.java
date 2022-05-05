package de.binerys.customerservice.controller;

import de.binerys.customerservice.dto.CustomerApiDTO;
import de.binerys.customerservice.entity.CustomerMapper;
import de.binerys.customerservice.repository.CustomerRepository;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@RestController
@Tag(name = "Customer Api", description = "The Customer Service")
@RequestMapping("api/customer")
public class CustomerApiController {
    private static final Logger log = LoggerFactory.getLogger(CustomerApiController.class);

    CustomerRepository customerRepository;
    CustomerMapper customerMapper;

    public CustomerApiController(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Hidden
    @GetMapping
    public List<CustomerApiDTO> findAll() {
        return customerRepository.findAllProjectedBy(CustomerApiDTO.class);
    }

    @Operation(
            description = "Load all users paged",
            responses = @ApiResponse(
                    description = "A page of users",
                    responseCode = "200"
            ))
    @GetMapping(params = {"page"})
    public List<CustomerApiDTO> findAllPaged(@PageableDefault(sort = "customerNbr") Pageable pageable) {
        return customerRepository.findAllProjectedBy(CustomerApiDTO.class, pageable);
    }

    @GetMapping("/{customerNbr}")
    public ResponseEntity<CustomerApiDTO> findByCustomerNbr(@PathVariable("customerNbr") String customerNbr) {
        return customerRepository.findProjectionsByCustomerNbr(customerNbr, CustomerApiDTO.class)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody @Valid CustomerApiDTO customerApiDTO) {
        var customerEntity = customerMapper.map(customerApiDTO);
        customerEntity = customerRepository.save(customerEntity);

        var uri = MvcUriComponentsBuilder
                .fromMethodCall(
                        MvcUriComponentsBuilder.on(CustomerApiController.class)
                                .findByCustomerNbr(customerApiDTO.getCustomerNbr()))
                .build();

        return ResponseEntity.created(uri.toUri()).build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public void handleConstraintViolation(DataIntegrityViolationException x) {
        log.error("Constraint Violation", x);
    }
}
