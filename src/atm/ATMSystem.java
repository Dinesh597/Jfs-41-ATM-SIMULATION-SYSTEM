package atm;

import java.util.Map;
import java.util.Scanner;

public class ATMSystem {
    private static Map<String, UserAccount> bankDatabase;
    private static double atmVaultBalance = 50000.00; // Physical cash tracking metric
    private static final int MAX_PIN_ATTEMPTS = 3;

    public static void main(String[] args) {
        bankDatabase = ATMFileSystem.loadBankDatabase();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n====================================");
            System.out.println("    🏦 SECURE REGIONAL BANK ATM     ");
            System.out.println("====================================");
            System.out.print("Enter Account Number (or 'exit' to shut down): ");
            String accNum = scanner.nextLine().trim();

            if (accNum.equalsIgnoreCase("exit")) {
                System.out.println("Synchronizing background balances securely...");
                ATMFileSystem.saveBankDatabase(bankDatabase);
                System.out.println("ATM operational cycle offline. Goodbye.");
                break;
            }

            if (!bankDatabase.containsKey(accNum)) {
                System.out.println("❌ Invalid account number. Please try again.");
                continue;
            }

            UserAccount activeAccount = bankDatabase.get(accNum);

            if (activeAccount.isLocked()) {
                System.out.println("🔒 This account is locked due to prior security flags. Please contact support.");
                continue;
            }

            // Authentication Loop
            boolean authenticated = false;
            for (int attempt = 1; attempt <= MAX_PIN_ATTEMPTS; attempt++) {
                System.out.print("Enter secure 4-Digit PIN: ");
                String inputPin = scanner.nextLine().trim();

                if (activeAccount.checkPin(inputPin)) {
                    authenticated = true;
                    break;
                } else {
                    System.out.printf("❌ Incorrect PIN. Attempt %d/%d used.\n", attempt, MAX_PIN_ATTEMPTS);
                }
            }

            if (!authenticated) {
                System.out.println("🔒 Maximum login verification attempts reached. Account has been locked.");
                activeAccount.lockAccount();
                ATMFileSystem.saveBankDatabase(bankDatabase); // Save lock state immediately
                continue;
            }

            // User Session Loop
            System.out.printf("\n✔️ Welcome back, %s!\n", activeAccount.getCardholderName());
            runUserDashboard(activeAccount, scanner);
            
            // Post Session State Update Sync
            ATMFileSystem.saveBankDatabase(bankDatabase);
        }
        scanner.close();
    }

    private static void runUserDashboard(UserAccount account, Scanner scanner) {
        while (true) {
            System.out.println("\n--- 💼 ATM DASHBOARD ---");
            System.out.println("1. Check Balance");
            System.out.println("2. Cash Deposit");
            System.out.println("3. Cash Withdrawal");
            System.out.println("4. Mini Statement (Txn Log)");
            System.out.println("5. Change Security PIN");
            System.out.println("6. End Secure Session");
            System.out.print("Select operational node: ");

            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    System.out.printf("Available Balance: $%.2f\n", account.getBalance());
                    break;

                case "2":
                    System.out.print("Insert deposit bill value: $");
                    double depAmt = parseInputAmount(scanner);
                    if (depAmt > 0) {
                        account.deposit(depAmt);
                        atmVaultBalance += depAmt;
                        System.out.printf("💸 Success! New balance updated to: $%.2f\n", account.getBalance());
                    }
                    break;

                case "3":
                    System.out.print("Enter withdrawal cash amount: $");
                    double drawAmt = parseInputAmount(scanner);
                    if (drawAmt > 0) {
                        if (drawAmt > atmVaultBalance) {
                            System.out.println("❌ Hardware Failure: ATM vault has insufficient cash reserves.");
                        } else if (account.withdraw(drawAmt)) {
                            atmVaultBalance -= drawAmt;
                            System.out.printf("💸 Dispensing $%.2f. Remaining Account Balance: $%.2f\n", drawAmt, account.getBalance());
                        } else {
                            System.out.println("❌ Transaction Failed: Insufficient personal funds.");
                        }
                    }
                    break;

                case "4":
                    System.out.println("\n--- 📜 MINI STATEMENT ---");
                    if (account.getTransactionHistory().isEmpty()) {
                        System.out.println("No transactional actions found during this file ledger epoch.");
                    } else {
                        account.getTransactionHistory().forEach(System.out::println);
                    }
                    break;

                case "5":
                    System.out.print("Enter configuration PIN: ");
                    String pin1 = scanner.nextLine().trim();
                    System.out.print("Re-enter new configuration PIN: ");
                    String pin2 = scanner.nextLine().trim();

                    if (pin1.equals(pin2) && pin1.length() == 4 && pin1.matches("\\d+")) {
                        account.changePin(pin1);
                        System.out.println("✔️ Configuration Updated! PIN altered successfully.");
                    } else {
                        System.out.println("❌ Modification Failed: PINs must match and be exactly 4 digits long.");
                    }
                    break;

                case "6":
                    System.out.println("Terminating structural session... clearing terminal state caches.");
                    return;

                default:
                    System.out.println("❌ Invalid command parameter selected.");
            }
        }
    }

    private static double parseInputAmount(Scanner scanner) {
        try {
            double value = Double.parseDouble(scanner.nextLine().trim());
            if (value <= 0) {
                System.out.println("❌ Error: Financial input must be positive.");
                return -1;
            }
            return value;
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Numerical processing input parsing failed.");
            return -1;
        }
    }
}
