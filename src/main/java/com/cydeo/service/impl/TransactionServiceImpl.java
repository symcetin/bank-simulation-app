package com.cydeo.service.impl;

import com.cydeo.dto.AccountDTO;
import com.cydeo.dto.TransactionDTO;
import com.cydeo.entity.Transaction;
import com.cydeo.enums.AccountType;
import com.cydeo.exception.*;
import com.cydeo.mapper.TransactionMapper;
import com.cydeo.repository.AccountRepository;
import com.cydeo.repository.TransactionRepository;
import com.cydeo.service.AccountService;
import com.cydeo.service.TransactionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionServiceImpl implements TransactionService {

    @Value("${under_construction}")
    private boolean underConstruction;

    AccountService accountService;
    TransactionRepository transactionRepository;
    TransactionMapper transactionMapper;

    public TransactionServiceImpl(AccountService accountService, TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public void makeTransfer(AccountDTO sender, AccountDTO receiver, BigDecimal amount, Date creationDate, String message) {

        if(!underConstruction) {
            validateAccount(sender, receiver);
            checkAccountOwnership(sender, receiver);
            executeBalanceAndUpdateIfRequired(amount, sender, receiver);
        /*
        after all validations are completed, and money is transferred, we need to create Transaction object and save/return it
         */

            //we need to create dto after all validations, then convert it to entity and save to the db
            TransactionDTO transactionDTO = new TransactionDTO(sender,receiver,amount,message,creationDate);

             transactionRepository.save(transactionMapper.convertToEntity(transactionDTO));
        }else {
            throw new UnderConstructionException("App is under construction, try again later");
        }
    }

    private void executeBalanceAndUpdateIfRequired(BigDecimal amount, AccountDTO sender, AccountDTO receiver) {
        if(checkSenderBalance(sender,amount)){
            sender.setBalance(sender.getBalance().subtract(amount));
            receiver.setBalance(receiver.getBalance().add(amount));

            /*
                get the dto from database for both sender and receiver, update balance and save it.
                create accountService updateAccount method to save it.
             */
            //retrieve the object from database for sender
            AccountDTO senderAcc = accountService.retrieveById(sender.getId());
            senderAcc.setBalance(sender.getBalance());
            //save again to database
            accountService.updateAccount(senderAcc);

            AccountDTO receiverAcc = accountService.retrieveById(receiver.getId());
            receiverAcc.setBalance(receiver.getBalance());

            accountService.updateAccount(receiverAcc);


        }else{
            throw new BalanceNotSufficientException("Balance is not enough for this transfer");
        }
    }

    private boolean checkSenderBalance(AccountDTO sender, BigDecimal amount) {
            //verify that sender has enough balance
        return sender.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) >=0;
    }

    private void checkAccountOwnership(AccountDTO sender, AccountDTO receiver) {

        /*
         write an if statement that checks if one of the account is saving,
         and user if of sender or receiver is not the same, throw AccountOwnershipException
         */

        if((sender.getAccountType().equals(AccountType.SAVING)
                ||receiver.getAccountType().equals(AccountType.SAVING))&& !sender.getUserId().equals(receiver.getUserId()))
        {
            throw new AccountOwnershipException("One of the accounts is Savings." +
                    " Transactions between savings and checking account are allowed between same user accounts only." +
                    " User Id's dont match.");
        }
    }

    private void validateAccount(AccountDTO sender, AccountDTO receiver) {
        /*
            -if any of the account is null
            -if account ids are the same(same account)
            -if the account exist in the database(repository)
         */

        if(sender==null || receiver==null){
            throw new BadRequestException("Sender or Receiver cannot be null");
        }

        if(sender.getId().equals(receiver.getId())){
            throw new BadRequestException("Sender account needs to be different than receiver");
        }

        findAccountById(sender.getId());
        findAccountById(receiver.getId());

    }

    private AccountDTO findAccountById(Long id) {

        return accountService.retrieveById(id);

    }

    @Override
    public List<TransactionDTO> findAllTransaction() {

        return transactionRepository.findAll().stream().map(transactionMapper::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> lastTransactionsList() {
        //we want to list latest 10 transaction
        //write a native query to get the result for last 10 transaction
        //then convert it to dto and return it
        //5MIN 10:47

        List<Transaction> lastTenTransactions = transactionRepository.findLastTenTransactions();

        return lastTenTransactions.stream().map(transactionMapper::convertToDTO).collect(Collectors.toList());

    }

    @Override
    public List<TransactionDTO> findTransactionListById(Long id) {
            // write a JPQL query to retrieve list of transactions by id

            return transactionRepository.findTransactionListById(id).stream()
                    .map(transactionMapper::convertToDTO).collect(Collectors.toList());
    }
}
