package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.ClientLoan;
import org.springframework.stereotype.Service;

@Service
public interface ClientLoanService {

    void saveClientLoan(ClientLoan clientLoan);
}
