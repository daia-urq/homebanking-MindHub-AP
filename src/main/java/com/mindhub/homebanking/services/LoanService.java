package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Loan;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LoanService {

    List<LoanDTO> getLoansDTO();

    boolean existsById(long id);

    Loan findById(long id);
}
