package de.binerys.customerservice.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final JwtEncoder jwtEncoder;
    private final AuthenticationManager authenticationManager;
    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    public AuthenticationController(JwtEncoder jwtEncoder, AuthenticationManager authenticationManager) {
        this.jwtEncoder = jwtEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping
    public ResponseEntity<String> authenticate(@RequestBody CredentialsDTO credentialsDTO) {
        try {
            var request = new UsernamePasswordAuthenticationToken(
                    credentialsDTO.username(), credentialsDTO.password());
            Authentication auth = authenticationManager.authenticate(request);
            if (auth.isAuthenticated()) {
                var parameters = JwtEncoderParameters.from(
                        JwsHeader.with(MacAlgorithm.HS256).build(),
                        JwtClaimsSet.builder()
                                .subject(credentialsDTO.username())
                                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                                .build()
                );
                var jwtToken = jwtEncoder.encode(parameters).getTokenValue();
                return ResponseEntity.ok(jwtToken);
            }
        } catch (AuthenticationException x) {
            log.warn("Authentication failed", x);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}