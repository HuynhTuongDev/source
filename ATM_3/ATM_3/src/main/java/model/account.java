package model;

public class account {
     String accountNumber;
     String password;
    private double balance;

    public account(String accountNumber, String password, double balance) {
        this.accountNumber = accountNumber;
        this.password = password;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
    public String getPassword() {
        return password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "accountNumber=" + accountNumber + ", password=" + password + ", balance=" + balance + '}';
    }
    
}