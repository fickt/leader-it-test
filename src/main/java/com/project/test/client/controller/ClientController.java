package com.project.test.client.controller;

import com.project.test.client.dto.ClientDto;
import com.project.test.client.service.ClientService;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController {
    private static final String ENDPOINT_GET_CLIENT_BY_ID = "/{clientId}";
    private final ClientService clientService;

    @GetMapping
    public List<ClientDto> getAllClients() {
        log.info("GET api/v1/clients");
        return clientService.getAllClients();
    }

    @GetMapping(ENDPOINT_GET_CLIENT_BY_ID)
    public ClientDto getClientById(@PositiveOrZero @PathVariable Long clientId) {
        log.info("GET api/v1/clients/{}", clientId);
        return clientService.getClientById(clientId);
    }


}
