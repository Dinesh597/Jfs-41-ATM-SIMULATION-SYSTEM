package atm;



import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final String type;
    private final double amount;
    private final double postBalance;
    private final String timestamp;

    public Transaction(String type, double amount, double postBalance) {
        this.type = type;
        this.amount = amount;
        this.postBalance = postBalance;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Secondary constructor used when parsing back from file logs
    public Transaction(String type, double amount, double postBalance, String timestamp) {
        this.type = type;
        this.amount = amount;
        this.postBalance = postBalance;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: $%.2f | Balance: $%.2f", timestamp, type, amount, postBalance);
    }

    public String toFileFormat() {
        return String.format("%s,%s,%.2f,%.2f", timestamp, type, amount, postBalance);
    }
}
