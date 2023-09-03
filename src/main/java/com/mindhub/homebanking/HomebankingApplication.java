package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {
    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(HomebankingApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository,
                                      TransactionRepository transactionRepository, LoanRepository loanRepository,
                                      ClientLoanRepository clientLoanRepository, CardRepository cardRepository) {
        return (args) -> {

            Client client = new Client("Melba", "Morel", "melba@mindhub.com", passwordEncoder.encode("melba"));
            clientRepository.save(client);

            Client client2 = new Client("Juana", "Gonzalez", "Juanita@mindhub.com", passwordEncoder.encode("juanita"));
            clientRepository.save(client2);

            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);
            LocalDate someday = today.plusDays(3);

            Account account1 = new Account("VIN00000001", today, 5000.00);
            Account account2 = new Account("VIN00000002", tomorrow, 7500.00);
            client.addAccount(account1);
            client.addAccount(account2);

            accountRepository.save(account1);
            accountRepository.save(account2);

            Account account4 = new Account("VIN00000003", today, 3000.00);
            Account account5 = new Account("VIN00000004", someday, 4500.00);
            client2.addAccount(account4);
            client2.addAccount(account5);

            accountRepository.save(account4);
            accountRepository.save(account5);

            LocalDateTime now1 = LocalDateTime.now();
            Transaction transaction1 = new Transaction(TransactionType.CREDIT, 1000.00, "DELIVERY", now1);
            Transaction transaction2 = new Transaction(TransactionType.DEBIT, -500.00, "PHOTOCOPY", now1);
            Transaction transaction3 = new Transaction(TransactionType.CREDIT, 5000.00, "SALE", now1);

            Transaction transaction4 = new Transaction(TransactionType.CREDIT, 10000.00, "SALE", now1);
            Transaction transaction5 = new Transaction(TransactionType.DEBIT, -5000.00, "INTERNET PAYMENT", now1);
            Transaction transaction6 = new Transaction(TransactionType.CREDIT, 3000.00, "OTHER", now1);

            account1.addTransaction(transaction1);
            account1.addTransaction(transaction2);

            account2.addTransaction(transaction3);
            account4.addTransaction(transaction4);
            account4.addTransaction(transaction5);
            account5.addTransaction(transaction6);

            transactionRepository.save(transaction1);
            transactionRepository.save(transaction2);
            transactionRepository.save(transaction3);
            transactionRepository.save(transaction4);
            transactionRepository.save(transaction5);
            transactionRepository.save(transaction6);

            account1.setBalance(account1.getBalance() + transaction1.getAmount());
            account1.setBalance(account1.getBalance() + transaction2.getAmount());
            account2.setBalance(account2.getBalance() + transaction3.getAmount());
            account4.setBalance(account4.getBalance() + transaction4.getAmount());
            account4.setBalance(account4.getBalance() + transaction5.getAmount());
            account5.setBalance(account5.getBalance() + transaction6.getAmount());

            accountRepository.save(account1);
            accountRepository.save(account2);
            accountRepository.save(account4);
            accountRepository.save(account5);

            List<Integer> listPayments1 = List.of(12, 24, 36, 48, 60);
            List<Integer> listPayments2 = List.of(6, 12, 24);
            List<Integer> listPayments3 = List.of(6, 12, 24, 36);

            Loan loan1 = new Loan("mortgage", 500000.00, listPayments1);
            Loan loan2 = new Loan("personal", 100000.00, listPayments2);
            Loan loan3 = new Loan("automotive", 300000.00, listPayments3);

            loanRepository.save(loan1);
            loanRepository.save(loan2);
            loanRepository.save(loan3);

            ClientLoan clientLoan1 = new ClientLoan(400000.00, 60);
            ClientLoan clientLoan2 = new ClientLoan(50000.00, 12);
            clientLoanRepository.save(clientLoan1);
            clientLoanRepository.save(clientLoan2);

            client.addClientLoan(clientLoan1);
            loan1.addClientLoan(clientLoan1);
            clientLoanRepository.save(clientLoan1);

            client.addClientLoan(clientLoan2);
            loan2.addClientLoan(clientLoan2);
            clientLoanRepository.save(clientLoan2);

            ClientLoan clientLoan3 = new ClientLoan(100000.00, 24);
            ClientLoan clientLoan4 = new ClientLoan(200000.00, 36);
            clientLoanRepository.save(clientLoan3);
            clientLoanRepository.save(clientLoan4);

            client2.addClientLoan(clientLoan3);
            loan2.addClientLoan(clientLoan3);
            clientLoanRepository.save(clientLoan3);

            client2.addClientLoan(clientLoan4);
            loan3.addClientLoan(clientLoan4);
            clientLoanRepository.save(clientLoan4);


            String name1 = client.getFirstName() + " " + client.getLastName();
            String name2 = client2.getFirstName() + " " + client2.getLastName();

            Card card1 = new Card(name1, "4666 4666 4666 4666", (short) 789, CardColor.GOLD, CardType.DEBIT, today, today.plusYears(5));
            Card card2 = new Card(name1, "4777 4777 4777 4777", (short) 456, CardColor.TITANIUM, CardType.CREDIT, today, today.plusYears(5));
            Card card3 = new Card(name2, "4888 4888 4888 4888", (short) 123, CardColor.SILVER, CardType.CREDIT, today, today.plusYears(5));

            client.addCard(card1);
            client.addCard(card2);
            cardRepository.save(card1);
            cardRepository.save(card2);

            client2.addCard(card3);
            cardRepository.save(card3);

        };
    }
}
