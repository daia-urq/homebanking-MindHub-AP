package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Transaction;
import org.springframework.stereotype.Service;

@Service
public interface TransactionService {

    void saveTransaction(Transaction transaction);
}
