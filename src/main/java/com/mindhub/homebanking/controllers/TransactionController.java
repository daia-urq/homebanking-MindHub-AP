package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ClientService clientService;


    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(@RequestParam String fromAccountNumber, @RequestParam String toAccountNumber, @RequestParam Double amount, @RequestParam String description) {
        //obtener datos de autenticadocion
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>("LogIn", HttpStatus.FORBIDDEN);
        }

        //validar que los parametros recibidos  no esten vacios
        if (fromAccountNumber.isEmpty()) {
            return new ResponseEntity<>("Missing from account number", HttpStatus.FORBIDDEN);
        }
        if (toAccountNumber.isEmpty()) {
            return new ResponseEntity<>("Missing to account number", HttpStatus.FORBIDDEN);
        }
        if (description.isEmpty()) {
            return new ResponseEntity<>("Missing description", HttpStatus.FORBIDDEN);
        }
        if (amount.toString().isEmpty()) {
            return new ResponseEntity<>("Missing amount", HttpStatus.FORBIDDEN);
        }

        //validad que el numero de cuanta de origen y destino recibido por parametro no sean los mismos
        if (fromAccountNumber.equals(toAccountNumber)) {
            return new ResponseEntity<>("The accounts cannot be the same", HttpStatus.FORBIDDEN);
        }

        if(!accountService.existsByNumber(fromAccountNumber)){
            return new ResponseEntity<>("The accounts don´t exists", HttpStatus.FORBIDDEN);
        }
        //cuenta de destino recibida por parametro
        Account fromAccount = accountService.findByNumber(fromAccountNumber);

        //email cliente que esta autenticado
        String nameClientAuthenticated = authentication.getName();
        Client clientAuthenticated = clientService.findByEmail(nameClientAuthenticated);

        //validar que cliente autenticado sea el cliente asociados a la cuenta de origen
        if (clientAuthenticated != fromAccount.getClient()) {
            return new ResponseEntity<>("This is not your account", HttpStatus.FORBIDDEN);
        }

        if(!accountService.existsByNumber(toAccountNumber)){
            return new ResponseEntity<>("The accounts don´t exists", HttpStatus.FORBIDDEN);
        }
        //cuenta de destino recibida por parametro
        Account toAccount = accountService.findByNumber(toAccountNumber);

        if (amount < 1) {
            return new ResponseEntity<>("invalid amount", HttpStatus.FORBIDDEN);

        }

        if (fromAccount.getBalance() < amount) {
            return new ResponseEntity<>("Insufficient funds to approve this operations", HttpStatus.FORBIDDEN);
        }

        LocalDateTime now = LocalDateTime.now();

        Transaction transactionDebit = new Transaction(TransactionType.DEBIT, (amount * -1), description, now);
        Transaction transactionCredit = new Transaction(TransactionType.CREDIT, amount, description, now);

        fromAccount.addTransaction(transactionDebit);
        toAccount.addTransaction(transactionCredit);
        transactionService.saveTransaction(transactionDebit);
        transactionService.saveTransaction(transactionCredit);

        double restBalance = fromAccount.getBalance() + (amount * -1);
        fromAccount.setBalance(restBalance);
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountService.saveAccount(fromAccount);
        accountService.saveAccount(toAccount);

        return new ResponseEntity<>("Successful transfer", HttpStatus.OK);
    }

}
