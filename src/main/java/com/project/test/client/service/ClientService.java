package com.project.test.client.service;

import com.project.test.client.dto.ClientDto;

import java.util.List;

public interface ClientService {
    List<ClientDto> getAllClients();

    ClientDto getClientById(Long clientId);
}
