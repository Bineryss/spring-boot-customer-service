package de.binerys.customerservice.integration;

import de.binerys.customerservice.dto.CustomerApiDTO;
import de.binerys.customerservice.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerApiIntegrationTest {
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    CustomerRepository repository;
    @Autowired
    JwtEncoder jwtEncoder;


    @Test
    void testSave() {
        // first lets get a token
        var parameters = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                JwtClaimsSet.builder().subject("test").build()
        );
        var jwtAuth = jwtEncoder.encode(parameters).getTokenValue();

        // now we save the user
        var headers = new HttpHeaders();
        headers.setBearerAuth(jwtAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        var customer = CustomerApiDTO.builder().customerNbr("C001").firstname("Test").lastname("Mensch").build();
        // TestRestTemplate knows about the random port!
        var entity = restTemplate.postForEntity("/api/customer", new HttpEntity<>(customer, headers), Void.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var userInDb = repository.findByNaturalId(customer.getCustomerNbr()).get();
        assertThat(customer).usingRecursiveComparison().isEqualTo(userInDb);

        var location = entity.getHeaders().getLocation();
        var reloadedEntity = restTemplate.exchange(
                location,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                CustomerApiDTO.class
        );
        assertThat(reloadedEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reloadedEntity.getBody()).isEqualTo(customer);
        repository.deleteById(userInDb.getId());
    }
}
