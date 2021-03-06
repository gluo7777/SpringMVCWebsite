package org.app.controller;

import org.app.entity.Account;
import org.app.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Collection<Account>> getAccounts() {
        Collection<Account> accounts = accountService.getAccounts();
        if (accounts == null || accounts.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<Account> getAccountById(@PathVariable("userId") Long id) {
        Account account = accountService.getAccountById(id);
        if(account == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> addAccount(@RequestBody Account account, UriComponentsBuilder ucBuilder) {
        if(accountService.exists(account)){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Account new_account = new Account(account.getUserName(),account.getPassword());
        accountService.addAccount(new_account);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/accounts/{userId}").buildAndExpand(new_account.getUserId()).toUri());
        accountService.addAccount(new_account);
        return new ResponseEntity<>(headers,HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAccountById(@PathVariable("userId") Long id) {
        Account account = accountService.getAccountById(id);
        if(account == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        accountService.deleteAccountById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
    public ResponseEntity<Account> updateAccount(@PathVariable("userId") Long id, @RequestBody Account account) {
        Account currentAccount = accountService.getAccountById(id);
        if(currentAccount == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        accountService.updateAccount(account);
        return new ResponseEntity<>(currentAccount,HttpStatus.OK);
    }
}
