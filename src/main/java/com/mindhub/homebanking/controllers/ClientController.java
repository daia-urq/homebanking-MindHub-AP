package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping("/clients")
    public List<ClientDTO> getClients(){
        return clientRepository.findAll().stream().map(client -> new ClientDTO(client)).collect(toList());
    }

    @RequestMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        ClientDTO clientDTO = clientRepository.findById(id).map(client -> new ClientDTO(client)).orElse(null);

        return clientDTO;
    }

    @PostMapping("/clients")
    public ResponseEntity<Object> register(@RequestParam String firstName, @RequestParam String lastName, @RequestParam  String email, @RequestParam String password){

        if (firstName.isEmpty()) {
            return new ResponseEntity<>("Missing first name", HttpStatus.FORBIDDEN);
        }

        if (lastName.isEmpty() ) {
            return new ResponseEntity<>("Missing last name", HttpStatus.FORBIDDEN);
        }

        if (email.isEmpty()) {
            return new ResponseEntity<>("Missing email", HttpStatus.FORBIDDEN);
        }

        if (password.isEmpty()) {
            return new ResponseEntity<>("Missing password", HttpStatus.FORBIDDEN);
        }

        if (clientRepository.findByEmail(email).isPresent()) {

            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);

        }

        Client client = new Client(firstName, lastName, email, passwordEncoder.encode(password));

        clientRepository.save(client);

        LocalDate today = LocalDate.now();
        Random random = new Random();
        String number ;
        int randomNum;

        do {
            randomNum = random.nextInt(90000000) + 10000000;
            number = "VIN-" + randomNum;
        } while (accountRepository.existsByNumber(number));

        Account account = new Account(number, today, 0.00);
        client.addAccount(account);
        accountRepository.save(account);

        return new ResponseEntity<>("Client created",HttpStatus.CREATED);
    }

    @RequestMapping("clients/current")
    public ClientDTO getClientsCurrent(Authentication authentication){
        ClientDTO clientDTO = clientRepository.findByEmail(authentication.getName()).map(client -> new ClientDTO(client)).orElse(null);

        return clientDTO;
    }

}
