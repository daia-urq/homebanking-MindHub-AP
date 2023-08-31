package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
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
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;


    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction( @RequestParam String fromAccountNumber, @RequestParam String toAccountNumber, @RequestParam Double amount, @RequestParam String description){
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
        if(amount.toString().isEmpty()){
            return new ResponseEntity<>("Missing amount", HttpStatus.FORBIDDEN);
        }

        //validad que el numero de cuanta de origen y destino recibido por parametro no sean los mismos
        if( fromAccountNumber.equals(toAccountNumber)){
            return new ResponseEntity<>("The accounts cannot be the same", HttpStatus.FORBIDDEN);
        }

        //cuenta de destino recibida por parametro
        Account fromAccount = accountRepository.findByNumber(fromAccountNumber);

        if(fromAccount == null ){
            return new ResponseEntity<>("The accounts dont exists", HttpStatus.FORBIDDEN);
        }

        //cliente que esta autenticado
        String nameClientAuthenticated = authentication.getName();
        Client clientAuthenticated = clientRepository.findByEmail(nameClientAuthenticated).orElse(null);

        //validar que cliente autenticado sea el cliente asociados a la cuenta de origen
        if( clientAuthenticated != fromAccount.getClient()){
            return new ResponseEntity<>("This is not your account", HttpStatus.FORBIDDEN);
        }

        //cuenta de destino recibida por parametro
        Account toAccount = accountRepository.findByNumber(toAccountNumber);

        if(toAccount== null ){
            return new ResponseEntity<>("The accounts dont exists", HttpStatus.FORBIDDEN);
        }

        if(amount <1){
            return new ResponseEntity<>("invalid amount", HttpStatus.FORBIDDEN);

        }
        if(fromAccount.getBalance() < amount){
            return new ResponseEntity<>("Insufficient funds to approve this operations", HttpStatus.FORBIDDEN);
        }

        LocalDateTime now = LocalDateTime.now();

        Transaction transactionDebit = new Transaction(TransactionType.DEBIT, (amount*-1),description, now);
        Transaction transactionCredit = new Transaction(TransactionType.CREDIT, amount,description,now);

        fromAccount.addTransaction(transactionDebit);
        toAccount.addTransaction(transactionCredit);
        transactionRepository.save(transactionDebit);
        transactionRepository.save(transactionCredit);

        double restBalance = fromAccount.getBalance()+(amount*-1);
        fromAccount.setBalance(restBalance);
        toAccount.setBalance( toAccount.getBalance() +amount );

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        return new ResponseEntity<>("Successful transfer", HttpStatus.OK);
    }

}
