package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Card;
import org.springframework.stereotype.Service;

@Service
public interface CardService {

    Boolean existsByNumber(String number);

    void saveCard(Card card);

    void deleteCard(Long id);

    Boolean existsById(long id);

    Card findById(Long id);
}
