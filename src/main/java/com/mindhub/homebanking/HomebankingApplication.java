package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository repositoryClient, AccountRepository accountRepository) {
		return (args) -> {

			Client client = new Client("Melba" ,"Morel" , "melba@mindhub.com");
			repositoryClient.save(client);

			Client client2 = new Client("Juana" ,"Gonzalez" , "Juanita@mindhub.com");
			repositoryClient.save(client2);

			LocalDate today = LocalDate.now();
			LocalDate  tomorrow = today.plusDays(1);
			LocalDate  someday = today.plusDays(3);

			Account account1 = new Account( "VIN0001", today, 5000.00 , client);
			Account account2 = new Account( "VIN0002", tomorrow, 7500.00 , client);

			accountRepository.save(account1);
			accountRepository.save(account2);

			Account account4 = new Account( "VIN0003", today, 3000.00 , client2);
			Account account5 = new Account( "VIN0004", someday, 4500.00 , client2);
			accountRepository.save(account4);
			accountRepository.save(account5);

		};
	}
}
