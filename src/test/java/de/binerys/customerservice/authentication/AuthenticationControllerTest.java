package de.binerys.customerservice.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtDecoder jwtDecoder;

    @Test
    void testAuthentication() throws Exception {
        var credentials = new CredentialsDTO("user", "pass");
        var json = objectMapper.writeValueAsString(credentials);

        var result = mockMvc.perform(
                        post("/api/auth")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isOk())
                .andReturn();
        var token = result.getResponse()
                .getContentAsString();
        var jwt = jwtDecoder.decode(token);
        assertEquals("user", jwt.getSubject());
    }
}
