package Pay;

import javax.swing.SwingUtilities;

/**
 * Main entry point for the Payroll Pro application.
 * This class handles the initial launch of the Login GUI.
 */
public class PayrollSystem {
    public static void main(String[] args) {
        // Ensure the GUI is created on the correct thread for Swing
        SwingUtilities.invokeLater(() -> {
            // This call initiates the LoginGUI which contains 
            // the 4-second splash screen logic in its constructor.
            LoginGUI login = new LoginGUI();
            login.setVisible(true);
        });
    }
}