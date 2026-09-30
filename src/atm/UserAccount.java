package atm;

import java.util.ArrayList;
import java.util.List;

public class UserAccount {
    private final String accountNumber;
    private String pin;
    private final String cardholderName;
    private double balance;
    private final List<Transaction> transactionHistory;
    private boolean isLocked;

    public UserAccount(String accountNumber, String pin, String cardholderName, double initialBalance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.cardholderName = cardholderName;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        this.isLocked = false;
    }

    // Encapsulation: Access and modify data safely through clear rules
    public String getAccountNumber() { return accountNumber; }
    public String getCardholderName() { return cardholderName; }
    public double getBalance() { return balance; }
    public boolean isLocked() { return isLocked; }
    public void lockAccount() { this.isLocked = true; }
    public List<Transaction> getTransactionHistory() { return new ArrayList<>(transactionHistory); }

    public boolean checkPin(String inputPin) {
        return this.pin.equals(inputPin);
    }

    public void changePin(String newPin) {
        this.pin = newPin;
        addTransaction("PIN_CHANGE", 0, this.balance);
    }

    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            addTransaction("DEPOSIT", amount, this.balance);
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= this.balance) {
            this.balance -= amount;
            addTransaction("WITHDRAWAL", amount, this.balance);
            return true;
        }
        return false;
    }

    public void addTransaction(String type, double amount, double postBalance) {
        this.transactionHistory.add(new Transaction(type, amount, postBalance));
    }

    public void addLoadedTransaction(Transaction transaction) {
        this.transactionHistory.add(transaction);
    }
}
