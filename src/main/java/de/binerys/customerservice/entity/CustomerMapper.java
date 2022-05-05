package de.binerys.customerservice.entity;

import de.binerys.customerservice.dto.CustomerApiDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer map(CustomerApiDTO dto);

    CustomerApiDTO map(Customer customer);

    Customer mapInto(CustomerApiDTO dto, @MappingTarget Customer entity);
}
