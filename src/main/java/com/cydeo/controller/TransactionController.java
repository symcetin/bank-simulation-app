package com.cydeo.controller;

import com.cydeo.dto.AccountDTO;
import com.cydeo.dto.TransactionDTO;
import com.cydeo.service.AccountService;
import com.cydeo.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Date;

@AllArgsConstructor
@Controller
public class TransactionController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @GetMapping("/make-transfer")
    public String makeTransfer(Model model){

        //we need all accounts to provide them as sender, receiver
        model.addAttribute("accounts",accountService.listAllAccount());
        //we need empty transaction object to get info from UI
        model.addAttribute("transaction", new TransactionDTO());
        //we need list of last 10 transactions
        model.addAttribute("lastTransactions",transactionService.lastTransactionsList());

        return "transaction/make-transfer";
    }

    //TASK
    //write a post method, that takes transaction object from the method above,
    //complete the make transfer and return the same page.
    @PostMapping("/transfer")
    public String postMakeTransfer(@Valid @ModelAttribute("transaction") TransactionDTO transactionDTO, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()){

            model.addAttribute("accounts",accountService.listAllAccount());
            return "transaction/make-transfer";
        }
        //I have UUID but I need to provide Account to make transfer method.
        AccountDTO sender = accountService.retrieveById(transactionDTO.getSender().getId());
        AccountDTO receiver = accountService.retrieveById(transactionDTO.getReceiver().getId());

        transactionService.makeTransfer(sender,receiver, transactionDTO.getAmount(),new Date(), transactionDTO.getMessage());

        return "redirect:/make-transfer";
    }

    //write a method, that gets the id from index.html and print on the console.
    //(work on index.html and here)
    @GetMapping("/transaction/{id}")
    public String getTransactionList(@PathVariable("id") Long id, Model model){

        System.out.println(id);
        //get the list of transactions based on id and return as a model attribute
        //TASK - Complete the method (service &repository)
        model.addAttribute("transactions",transactionService.findTransactionListById(id));




        return "transaction/transactions";

    }


}











