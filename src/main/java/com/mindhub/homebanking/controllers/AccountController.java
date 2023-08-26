package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts(){
       return accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(Collectors.toList());
    }

    @GetMapping("/accounts/{id}")
    public AccountDTO getOneAccount(@PathVariable Long id){
        AccountDTO accountDTO = accountRepository.findById(id).map(account -> new AccountDTO(account)).orElse(null);

        return accountDTO;
    }

    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication){

        Client client = clientRepository.findByEmail(authentication.getName()).orElse(null);

        if (client != null){
            if(client.getAccounts().size() <= 2){

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
                return new ResponseEntity<>("Account created", HttpStatus.CREATED);

            } else{
                return new ResponseEntity<>("Maximum accounts reached", HttpStatus.FORBIDDEN);
            }
        }else{
           return new ResponseEntity<>("Client not found", HttpStatus.BAD_REQUEST);
        }
    }
}
