package de.binerys.customerservice.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Jacksonized
@Builder
public class CustomerApiDTO {
    @NotBlank
    private String customerNbr;
    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
}
