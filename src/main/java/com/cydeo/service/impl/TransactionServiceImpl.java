package com.cydeo.service.impl;

import com.cydeo.enums.AccountType;
import com.cydeo.exception.AccountOwnershipException;
import com.cydeo.exception.BadRequestException;
import com.cydeo.exception.BalanceNotSufficientException;
import com.cydeo.model.Account;
import com.cydeo.model.Transaction;
import com.cydeo.repository.AccountRepository;
import com.cydeo.service.TransactionService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class TransactionServiceImpl implements TransactionService {

    AccountRepository accountRepository;
    @Override
    public Transaction makeTransfer(Account sender, Account receiver, BigDecimal amount, Date creationDate, String message) {

        validateAccount(sender, receiver);
        checkAccountOwnership(sender, receiver);
        executeBalanceAndUpdateIfRequired(amount, sender, receiver);
        /*
        after all validations completed, and money is transferred 
         */
        return null;
    }

    private void executeBalanceAndUpdateIfRequired(BigDecimal amount, Account sender, Account receiver) {
        if(checkSenderBalance(sender,amount)){
            //make transaction
            sender.setBalance(sender.getBalance().subtract(amount));
            receiver.setBalance(receiver.getBalance().add(amount));
        }else{
            //not enough balance
            throw new BalanceNotSufficientException("Balance is not enough for this transfer" );
        }
    }

    private boolean checkSenderBalance(Account sender, BigDecimal amount) {
        //verify that sender has enough balance
        return sender.getBalance().subtract(amount).compareTo(BigDecimal.ZERO)>=0;
    }

    private void checkAccountOwnership(Account sender, Account receiver){
        /*
        write an if statement that checks if one of the account is saving,
        and user of sender or receiver is not the same, throw AccountOwnershipException
         */
        if((sender.getAccountType().equals(AccountType.SAVING) ||
                receiver.getAccountType().equals(AccountType.SAVING)) && !sender.getUserID().equals(receiver.getUserID())){
            throw new AccountOwnershipException("One of the accounts is Savings." +
                    " Transactions between savings and checking account are allowed between same user accounts only." +
                    " User Id's dont match.");
        }


    }

    private void validateAccount(Account sender, Account receiver) {
        /*
            -if any of the account is null
            -if account ids are the same(same account)
            -if the account exist in the database(repository)
         */
        if (sender == null || receiver == null) {
            throw new BadRequestException("Sender or Receiver cannot be null");

        }

        if (sender.getId().equals(receiver.getId())) {
            throw new BadRequestException("Sender account needs to be different than receiver");
        }
        
        findAccountById(sender.getId());
        findAccountById(receiver.getId());

    }

    public TransactionServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    private Account findAccountById(UUID id) {
        return accountRepository.findById(id);
    }

    @Override
    public List<Transaction> findAllTransaction() {
        return null;
    }
}
