package com.project.test.client.service;

import com.project.test.client.dto.ClientDto;
import com.project.test.client.mapper.ClientMapper;
import com.project.test.client.repository.ClientRepository;
import com.project.test.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.test.exception.Message.CLIENT_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public List<ClientDto> getAllClients() {
        return clientRepository.findAll().stream()
                .map(ClientMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ClientDto getClientById(Long clientId) {
        return clientRepository.findById(clientId)
                .map(ClientMapper.INSTANCE::toDto)
                .orElseThrow(() -> new ClientNotFoundException(String.format(CLIENT_NOT_FOUND.get(), clientId)));
    }
}
