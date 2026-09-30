package atm;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ATMFileSystem {
    private static final String DATA_FILE = "atm_bank_database.txt";

    // Reads user accounts and histories from local storage
    public static Map<String, UserAccount> loadBankDatabase() {
        Map<String, UserAccount> database = new HashMap<>();
        File file = new File(DATA_FILE);

        if (!file.exists()) {
            return generateMockDatabase();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                // Parse base user info
                String[] parts = line.split("\\|");
                String accNum = parts[0];
                String pin = parts[1];
                String name = parts[2];
                double balance = Double.parseDouble(parts[3]);
                boolean isLocked = Boolean.parseBoolean(parts[4]);

                UserAccount account = new UserAccount(accNum, pin, name, balance);
                if (isLocked) account.lockAccount();

                // Parse transaction strings if they exist
                if (parts.length > 5 && !parts[5].equals("NONE")) {
                    String[] txns = parts[5].split(";");
                    for (String txnStr : txns) {
                        String[] tData = txnStr.split(",");
                        account.addLoadedTransaction(new Transaction(tData[1], Double.parseDouble(tData[2]), Double.parseDouble(tData[3]), tData[0]));
                    }
                }
                database.put(accNum, account);
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("⚠️ Warning: Data corrupted. Loading fallback simulation environment.");
            return generateMockDatabase();
        }
        return database;
    }

    // Persists live states back into file system on exit
    public static void saveBankDatabase(Map<String, UserAccount> database) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (UserAccount acc : database.values()) {
                StringBuilder sb = new StringBuilder();
                sb.append(acc.getAccountNumber()).append("|")
                  .append(getPrivatePinField(acc)).append("|") // Reflection fallback simulation
                  .append(acc.getCardholderName()).append("|")
                  .append(acc.getBalance()).append("|")
                  .append(acc.isLocked()).append("|");

                if (acc.getTransactionHistory().isEmpty()) {
                    sb.append("NONE");
                } else {
                    ArrayList<String> tStrings = new ArrayList<>();
                    for (Transaction t : acc.getTransactionHistory()) {
                        tStrings.add(t.toFileFormat());
                    }
                    sb.append(String.join(";", tStrings));
                }
                bw.write(sb.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("❌ Critical: Error persisting banking database safely.");
        }
    }

    private static Map<String, UserAccount> generateMockDatabase() {
        Map<String, UserAccount> mock = new HashMap<>();
        mock.put("111222", new UserAccount("111222", "1234", "Rajesh Kumar", 25000.00));
        mock.put("333444", new UserAccount("333444", "4321", "Priya Sharma", 8500.50));
        mock.put("555666", new UserAccount("555666", "9999", "Amit Patel", 120.00));
        return mock;
    }

    // Helper to gain programmatic field trick cleanly for internal simulation serialization
    private static String getPrivatePinField(UserAccount account) {
        try {
            java.lang.reflect.Field field = UserAccount.class.getDeclaredField("pin");
            field.setAccessible(true);
            return (String) field.get(account);
        } catch (Exception e) {
            return "1234";
        }
    }
}
