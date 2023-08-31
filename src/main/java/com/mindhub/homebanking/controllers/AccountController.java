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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;



    //tiene validacion que no ingrese un cliente
    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts(){
       return accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(Collectors.toList());
    }

    //tiene la validacion del cliente autentiicado con sus cuentas, está en antMatchers
    @GetMapping("/accounts/{id}")
    public AccountDTO getOneAccount(@PathVariable Long id){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String authenticatedClientEmail = authentication.getName();

        Client client = clientRepository.findByEmail(authenticatedClientEmail).orElse(null);

        if(client == null){
            return null;
        }

        Account account = accountRepository.findById(id).orElse(null);

        if (account == null){
            return null;
        }

        if ( client != account.getClient()) {
            return null;
        }

        AccountDTO accountDTO = accountRepository.findById(id).map(accountAux -> new AccountDTO(accountAux)).orElse(null);

        return accountDTO;
    }

    //valida que la autenticacion no sea nula , está en antMatchers
    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication){

        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>("LogIn", HttpStatus.FORBIDDEN);
        }

        Client client = clientRepository.findByEmail(authentication.getName()).orElse(null);

        if (client != null){
            if(client.getAccounts().size() <= 2){

               LocalDate today = LocalDate.now();
                Random random = new Random();
                String number ;
                int randomNum;
                
                do {
                    randomNum = random.nextInt(90000000) + 10000000;
                    number = "VIN" + randomNum;
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

    @GetMapping("/clients/current/accounts")
    public ResponseEntity<Object> getAccountCurrent(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>("LogIn", HttpStatus.FORBIDDEN);
        }

        String authenticatedClientEmail = authentication.getName();

        Client client = clientRepository.findByEmail(authenticatedClientEmail).orElse(null);

        if(client == null){
            return new ResponseEntity<>("Client not found", HttpStatus.FORBIDDEN);
        }

        Set<Account> setAccountClientCurrent =  client.getAccounts();

        List<AccountDTO>  listAccountsDTO = setAccountClientCurrent.stream().map(account -> new AccountDTO(account)).collect(Collectors.toList());

        return new ResponseEntity<>(listAccountsDTO, HttpStatus.CREATED);
    }

}
