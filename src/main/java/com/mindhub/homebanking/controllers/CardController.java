package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
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
public class CardController  {
    @Autowired
    CardRepository cardRepository;
    @Autowired
    ClientRepository clientRepository;

    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> createCard( @RequestParam CardType cardType, @RequestParam CardColor cardColor,  Authentication authentication){

        Client client  = clientRepository.findByEmail(authentication.getName()).orElse(null);

        if(client != null){
            Set<Card> cards = client.getCards();
            long countTypeCards = cards.stream().filter(card -> cardType.equals(card.getType())).count();

            if(countTypeCards <=2){
                Random random = new Random();
                LocalDate today = LocalDate.now();
                String name = client.getFirstName()+ " "+ client.getLastName();

                int cvv = random.nextInt(999) + 1;
                //agrega ceros a la izquierda si el nuumero es menor a 100
                String formattedCvv = String.format("%03d", cvv);
                //lo pasa a short para poder pasrlo por el constructor
                short cvvShort = Short.parseShort(formattedCvv);

                //numero de tarjeta
                String cardNumber;
                do {
                    //para crear el numero de tarjeta de 16 digitos
                    StringBuilder cardNumberBuilder = new StringBuilder();
                    cardNumber = null;
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 4; j++) {
                            int digit = random.nextInt(10);
                            cardNumberBuilder.append(digit);
                        }
                        if (i < 3) {
                            cardNumberBuilder.append(" ");
                        }
                    }
                    cardNumber = cardNumberBuilder.toString();
                } while (cardRepository.existsByNumber(cardNumber));

                Card card = new Card(name,cardNumber, cvvShort, cardColor, cardType, today, today.plusYears(5) );
                client.addCard(card);
                cardRepository.save(card);
                return new ResponseEntity<>("Card created", HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>("Maximum card reached", HttpStatus.FORBIDDEN);
            }
        }else {
            return new ResponseEntity<>("Client not found", HttpStatus.BAD_REQUEST);
        }
    }
}
