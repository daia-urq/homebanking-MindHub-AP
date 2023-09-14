package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanAplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    private LoanService loanService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientLoanService clientLoanService;
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/loans")
    public List<LoanDTO> getLoans() {
        return loanService.getLoansDTO();
    }

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> createLoan(@RequestBody LoanAplicationDTO loanAplicationDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (loanAplicationDTO.getLoanId() <= 0) {
            return new ResponseEntity<>("Error id", HttpStatus.FORBIDDEN);
        }

        if (loanAplicationDTO.getAmount() == 0.00 || loanAplicationDTO.getAmount() < 0) {
            return new ResponseEntity<>("Invalid amount", HttpStatus.FORBIDDEN);
        }

        if (loanAplicationDTO.getPayments() <= 0) {
            return new ResponseEntity<>("Invalid payments", HttpStatus.FORBIDDEN);
        }

        if (loanAplicationDTO.getToAccountNumber().isBlank()) {
            return new ResponseEntity<>("Missing account number", HttpStatus.FORBIDDEN);
        }

        if (!accountService.existsByNumber(loanAplicationDTO.getToAccountNumber())) {
            return new ResponseEntity<>("This account does not exist", HttpStatus.FORBIDDEN);
        }

        Account account = accountService.findByNumber(loanAplicationDTO.getToAccountNumber());

        Client client = clientService.findByEmail(authentication.getName());

        if (!client.getAccounts().contains(accountService.findByNumber(loanAplicationDTO.getToAccountNumber()))) {
            return new ResponseEntity<>("This account is not allowed for you", HttpStatus.FORBIDDEN);
        }

        if(!loanService.existsById(loanAplicationDTO.getLoanId())){
            return new ResponseEntity<>("Loan does not exist", HttpStatus.FORBIDDEN);
        }

        Loan loan = loanService.findById(loanAplicationDTO.getLoanId());

        if (!(loanAplicationDTO.getAmount() <= loan.getMaxAmount())) {
            return new ResponseEntity<>("Amount exceeded", HttpStatus.FORBIDDEN);
        }

        if (!loan.getPayments().contains(loanAplicationDTO.getPayments())) {
            return new ResponseEntity<>("Invalid payments", HttpStatus.FORBIDDEN);
        }

        Double amountInterest = loanAplicationDTO.getAmount() * 1.20;
        String description = loan.getName() + " loan approved";
        LocalDateTime now = LocalDateTime.now();

        Transaction transaction = new Transaction(TransactionType.CREDIT, loanAplicationDTO.getAmount(), description, now);

        transactionService.saveTransaction(transaction);
        account.addTransaction(transaction);

        account.setBalance(account.getBalance() + loanAplicationDTO.getAmount());

        accountService.saveAccount(account);

        ClientLoan clientLoan = new ClientLoan(amountInterest, loanAplicationDTO.getPayments());

        clientLoanService.saveClientLoan(clientLoan);

        client.addClientLoan(clientLoan);
        loan.addClientLoan(clientLoan);

        return new ResponseEntity<>("Successful loan", HttpStatus.OK);
    }


}
