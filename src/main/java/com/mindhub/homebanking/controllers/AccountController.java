package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
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
    private AccountService accountService;
    @Autowired
    private ClientService clientService;

    //tiene validacion que no ingrese un cliente
    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts() {
        return accountService.getAccountsDTO();
    }

    //tiene la validacion del cliente autentiicado con sus cuentas, está en antMatchers
    @GetMapping("/accounts/{id}")
    public AccountDTO getOneAccount(@PathVariable Long id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String authenticatedClientEmail = authentication.getName();

        Client client = clientService.findByEmail(authenticatedClientEmail);

        if (!accountService.existsById(id)) {
            return null;
        }

        Account account = accountService.findById(id);

        if (client != account.getClient()) {
            return null;
        }

        return accountService.getAccountDTOById(id);
    }

    //valida que la autenticacion no sea nula , está en antMatchers
    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication) {

        Client client = clientService.findByEmail(authentication.getName());

        if (client.getAccounts().size() <= 2) {

            LocalDate today = LocalDate.now();
            Random random = new Random();
            String number;
            int randomNum;
            do {
                randomNum = random.nextInt(90000000) + 10000000;
                number = "VIN" + randomNum;
            } while (accountService.existsByNumber(number));

            Account account = new Account(number, today, 0.00);
            client.addAccount(account);
            accountService.saveAccount(account);
            return new ResponseEntity<>("Account created", HttpStatus.CREATED);

        } else {
            return new ResponseEntity<>("Maximum accounts reached", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/clients/current/accounts")
    public ResponseEntity<Object> getAccountCurrent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String authenticatedClientEmail = authentication.getName();

        Client client = clientService.findByEmail(authenticatedClientEmail);

        Set<Account> setAccountClientCurrent = client.getAccounts();

        List<AccountDTO> listAccountsDTO = setAccountClientCurrent.stream().map(account -> new AccountDTO(account)).collect(Collectors.toList());

        return new ResponseEntity<>(listAccountsDTO, HttpStatus.OK);
    }

}
