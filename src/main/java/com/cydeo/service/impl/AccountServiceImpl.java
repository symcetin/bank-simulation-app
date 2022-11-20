package com.cydeo.service.impl;

import com.cydeo.dto.AccountDTO;
import com.cydeo.entity.Account;
import com.cydeo.enums.AccountStatus;
import com.cydeo.enums.AccountType;
import com.cydeo.mapper.AccountMapper;
import com.cydeo.repository.AccountRepository;
import com.cydeo.service.AccountService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AccountServiceImpl implements AccountService {

    AccountRepository accountRepository;
    AccountMapper accountMapper;

    public AccountServiceImpl(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    public void createNewAccount(AccountDTO accountDTO) {

         //we will complete the DTO
        //convert it to entity and save it to database
        accountDTO.setAccountStatus(AccountStatus.ACTIVE);
        accountDTO.setCreationDate(new Date());

         accountRepository.save(accountMapper.convertToEntity(accountDTO));
    }

    @Override
    public List<AccountDTO> listAllAccount() {
        /*
            we are getting list of accounts from repo(database)
            but we need to return list of AccountDTO to controller
            what we need to do is we will convert Account to AccountDTO
         */
        List<Account> accountList = accountRepository.findAll();

        //we are converting list of account to accountDTOs and returing it.
        return accountList.stream().map(accountMapper::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteAccount(Long id) {
        //we need to find correct account based on id we have
        //change status to DELETED
        /*
            find the Account, update the Status to delete
         */
        Account account = accountRepository.findById(id).get();
        account.setAccountStatus(AccountStatus.DELETED);

        accountRepository.save(account);


    }

    @Override
    public AccountDTO retrieveById(Long id) {

        return accountMapper.convertToDTO(accountRepository.findById(id).get());
    }

    @Override
    public List<AccountDTO> listAllActiveAccounts() {
        //we need active accounts from repository
        List<Account> accountList = accountRepository.findAllByAccountStatus(AccountStatus.ACTIVE);
        //convert active accounts to accounts dto and return
        return accountList.stream().map(accountMapper::convertToDTO).collect(Collectors.toList());



    }
}
