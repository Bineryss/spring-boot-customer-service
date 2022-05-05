package de.binerys.customerservice.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class CustomerApiDTO {
    @NotBlank
    private String customerNbr;
    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
}
