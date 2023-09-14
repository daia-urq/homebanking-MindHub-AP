package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountService {

    List<AccountDTO> getAccountsDTO();

    void saveAccount(Account account);

    Account findById(long id);

    Boolean existsById(long id);

    Boolean existsByNumber(String number);

    Account findByNumber(String number);

    AccountDTO getAccountDTOById(long id);

    void deleteAccount(Long id);

}
