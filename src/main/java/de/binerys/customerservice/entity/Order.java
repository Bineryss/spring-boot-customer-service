package de.binerys.customerservice.entity;

import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder(toBuilder = true)
@Table(name="ORDERS")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    @GeneratedValue
    @NotNull
    private UUID id;
    @NaturalId
    @EqualsAndHashCode.Include
    @NotBlank
    private String orderNbr;
    @NotBlank
    @Setter
    private String orderText;
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @Setter
    private Customer customer;
}
