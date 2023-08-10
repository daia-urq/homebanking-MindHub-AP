package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository repositoryClient, AccountRepository accountRepository, TransactionRepository transactionRepository) {
		return (args) -> {

			Client client = new Client("Melba" ,"Morel" , "melba@mindhub.com");
			repositoryClient.save(client);

			Client client2 = new Client("Juana" ,"Gonzalez" , "Juanita@mindhub.com");
			repositoryClient.save(client2);

			LocalDate today = LocalDate.now();
			LocalDate  tomorrow = today.plusDays(1);
			LocalDate  someday = today.plusDays(3);

			Account account1 = new Account( "VIN0001", today, 5000.00 );
			Account account2 = new Account( "VIN0002", tomorrow, 7500.00 );
			client.addAccount(account1);
			client.addAccount(account2);

			accountRepository.save(account1);
			accountRepository.save(account2);

			Account account4 = new Account( "VIN0003", today, 3000.00 );
			Account account5 = new Account( "VIN0004", someday, 4500.00 );
			client2.addAccount(account4);
			client2.addAccount(account5);

			accountRepository.save(account4);
			accountRepository.save(account5);

			LocalDateTime now1 = LocalDateTime.now();
			Transaction transaction1 = new Transaction(TransactionType.CREDITO,1000.00, "envío", now1);
			Transaction transaction2 = new Transaction(TransactionType.DEBITO,-500.00, "fotocopias", now1);
			Transaction transaction3 = new Transaction(TransactionType.CREDITO,5000.00, "venta", now1);

			Transaction transaction4 = new Transaction(TransactionType.CREDITO,10000.00, "venta", now1);
			Transaction transaction5 = new Transaction(TransactionType.DEBITO,-5000.00, "pago wifi", now1);
			Transaction transaction6 = new Transaction(TransactionType.CREDITO,3000.00, "otros", now1);

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

		};
	}
}
