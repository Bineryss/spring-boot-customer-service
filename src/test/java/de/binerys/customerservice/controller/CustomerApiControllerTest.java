package de.binerys.customerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.binerys.customerservice.dto.CustomerApiDTO;
import de.binerys.customerservice.entity.Customer;
import de.binerys.customerservice.entity.CustomerMapperImpl;
import de.binerys.customerservice.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CustomerApiController.class)
@Import(CustomerMapperImpl.class)
@WithMockUser
class CustomerApiControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    CustomerRepository customerRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testFindAll() throws Exception {
        var customer = CustomerApiDTO.builder()
                .firstname("Test")
                .lastname("Mensch")
                .customerNbr("C001")
                .build();
        when(customerRepository.findAllProjectedBy(CustomerApiDTO.class)).thenReturn(List.of(customer));
        mockMvc.perform(get("/api/customer"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].firstname")
                        .value(customer.getFirstname()))
                .andExpect(jsonPath("$[0].lastname")
                        .value(customer.getLastname()))
                .andExpect(jsonPath("$[0].customerNbr")
                        .value(customer.getCustomerNbr()));
        verify(customerRepository).findAllProjectedBy(CustomerApiDTO.class);
    }

    @Test
    void testFindAllPaged() throws Exception {
        var customer = CustomerApiDTO.builder().
                customerNbr("C001").firstname("Test").lastname("Mensch").build();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("customerNbr"));

        when(customerRepository.findAllProjectedBy(CustomerApiDTO.class, pageRequest))
                .thenReturn(List.of(customer));

        mockMvc.perform(get("/api/customer?page=0"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].firstname")
                        .value(customer.getFirstname()))
                .andExpect(jsonPath("$[0].lastname")
                        .value(customer.getLastname()))
                .andExpect(jsonPath("$[0].customerNbr")
                        .value(customer.getCustomerNbr()));

        verify(customerRepository).findAllProjectedBy(CustomerApiDTO.class, pageRequest);
    }

    @Test
    void testFindByCustomerNbr() throws Exception {
        var customer = CustomerApiDTO.builder().
                customerNbr("C001").firstname("Test").lastname("Mensch").build();
        when(customerRepository.findProjectionsByCustomerNbr(customer.getCustomerNbr(),
                CustomerApiDTO.class))
                .thenReturn(Optional.of(customer));
        mockMvc.perform(get("/api/customer/{id}", customer.getCustomerNbr()))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstname")
                        .value(customer.getFirstname()))
                .andExpect(jsonPath("$.lastname")
                        .value(customer.getLastname()))
                .andExpect(jsonPath("$.customerNbr")
                        .value(customer.getCustomerNbr()));
        verify(customerRepository).findProjectionsByCustomerNbr(customer.getCustomerNbr(),
                CustomerApiDTO.class);
    }

    @Test
    void testInsert() throws Exception {
        var customer = CustomerApiDTO.builder().
                customerNbr("C001").firstname("Test").lastname("Mensch").build();
        var mockCustomerEntity = Customer.builder()
                .id(UUID.randomUUID())
                .firstname(customer.getFirstname())
                .lastname(customer.getLastname())
                .customerNbr(customer.getCustomerNbr())
                .build();
        when(customerRepository.save(any())).thenReturn(mockCustomerEntity);
        var json = objectMapper.writeValueAsString(customer);
        mockMvc.perform(post("/api/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header()
                        .string("Location",
                                "http://localhost/api/customer/" + customer.getCustomerNbr()));
        var argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(argumentCaptor.capture());
        var savedCustomer = argumentCaptor.getValue();
        assertThat(customer).usingRecursiveComparison().isEqualTo(savedCustomer);
    }

    @Test
    void testNotFound() throws Exception {
        var unknown = "unknown";
        mockMvc.perform(get("/api/customer/{customerNbr}", unknown))
                .andExpect(status().isNotFound());
        verify(customerRepository).findProjectionsByCustomerNbr(unknown, CustomerApiDTO.class);
    }
}
