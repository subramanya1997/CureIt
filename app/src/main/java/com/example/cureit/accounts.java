package com.example.cureit;

public class accounts {

    private String username, accountType;

    public  accounts() {

    }

    public accounts(String username, String accountType) {
        this.username = username;
        this.accountType = accountType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

}
