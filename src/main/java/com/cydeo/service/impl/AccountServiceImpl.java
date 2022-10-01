package com.cydeo.service.impl;

import com.cydeo.enums.AccountStatus;
import com.cydeo.enums.AccountType;
import com.cydeo.model.Account;
import com.cydeo.repository.AccountRepository;
import com.cydeo.service.AccountService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class AccountServiceImpl  implements AccountService {
    AccountRepository accountRepository;


    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Override
    public Account createNewAccount(BigDecimal balance, Date creationDate, AccountType accountType, Long userID) {

        Account account = Account.builder().id(UUID.randomUUID()).userID(userID)
                .accountType(accountType).balance(balance)
                .creationDate(creationDate).accountStatus(AccountStatus.ACTIVE).build();
       return accountRepository.save(account);
    }

    @Override
    public List<Account> listAllAccount() {
        return accountRepository.findAll();
    }

    @Override
    public void deleteAccount(UUID id) {
        //we need to find correct account based on id we have
        //change status to DELETED
        Account account = accountRepository.findById(id);
        account.setAccountStatus(AccountStatus.DELETED);
    }
}
