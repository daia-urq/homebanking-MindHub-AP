package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface ClientService {

    List<ClientDTO> getClientsDTO();

    void saveClient(Client client);

    Client findById(long id);

    ClientDTO getClientDTOByID(long id);

    Client findByEmail(String email);

    Boolean existsByEmail(String email);

    ClientDTO getClientDTOByEmail(String email);
}
