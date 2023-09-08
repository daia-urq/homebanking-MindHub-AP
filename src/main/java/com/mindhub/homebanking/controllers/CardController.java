package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Random;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class CardController {
    @Autowired
    private CardService cardService;
    @Autowired
    private ClientService clientService;

    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> createCard(@RequestParam CardType cardType, @RequestParam CardColor cardColor, Authentication authentication) {

        Client client = clientService.findByEmail(authentication.getName());

        Set<Card> cards = client.getCards();
        long countTypeCards = cards.stream().filter(card -> cardType.equals(card.getType())).count();

        if (countTypeCards < CardColor.values().length) {
            Random random = new Random();
            LocalDate today = LocalDate.now();
            String name = client.getFirstName() + " " + client.getLastName();

            int cvv = random.nextInt(999) + 1;
            String formattedCvv = String.format("%03d", cvv);
            short cvvShort = Short.parseShort(formattedCvv);

            String finalCardNumber;
            do {
                StringBuilder cardNumber = new StringBuilder(16);

                for (int j = 0; j < 4; j++) {
                    int fourDigit = random.nextInt(9999) + 1;
                    String numbers = String.format("%04d", fourDigit);
                    cardNumber.append(numbers);
                    if (j < 3) {
                        cardNumber.append(" ");
                    }
                }
                finalCardNumber = cardNumber.toString();
            } while (cardService.existsByNumber(finalCardNumber));


            Card card = new Card(name, finalCardNumber, cvvShort, cardColor, cardType, today, today.plusYears(5));
            client.addCard(card);

            cardService.saveCard(card);
            return new ResponseEntity<>("Card created", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Maximum card reached", HttpStatus.FORBIDDEN);
}
    }
}
