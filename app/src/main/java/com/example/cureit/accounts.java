package com.example.cureit;

public class accounts {

    private String username;
    private String fullName;
    private String accountType_username;

    public  accounts() {

    }

    public accounts(String username, String accountType, String fullName, String accountType_username) {
        this.username = username;
        this.fullName = fullName;
        this.accountType_username = accountType_username;
    }

    public String getAccountType_username() {
        return accountType_username;
    }

    public void setAccountType_username(String accountType_username) {
        this.accountType_username = accountType_username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
