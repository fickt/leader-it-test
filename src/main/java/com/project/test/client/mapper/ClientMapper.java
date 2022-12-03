package com.project.test.client.mapper;

import com.project.test.client.dto.ClientDto;
import com.project.test.client.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    ClientDto toDto(Client client);
}
