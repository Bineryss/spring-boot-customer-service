package de.binerys.customerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Schema(name = "Customer")
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
