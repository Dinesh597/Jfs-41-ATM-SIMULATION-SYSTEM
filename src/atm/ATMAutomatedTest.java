package atm;

import java.io.ByteArrayInputStream;

public class ATMAutomatedTest {
    public static void main(String[] args) {
        // Your automated input sequence simulating a user session
        String simulatedInput = "111222\n" +  // Account Number
                                "1234\n" +    // PIN
                                "1\n" +       // Check Balance
                                "3\n" +       // Choose Withdraw
                                "2000\n" +    // Withdraw $2000
                                "4\n" +       // View Mini Statement
                                "6\n" +       // Log out of session
                                "exit\n";     // Shut down system

        // Redirect standard system input to read our script string
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        System.out.println("🚀 Launching Automated ATM I/O Test Stream in Eclipse...\n");
        
        // Triggers the main entry point of your ATM application
        ATMSystem.main(new String[0]); 
    }
}
