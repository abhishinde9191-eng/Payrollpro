package Pay;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterGUI extends JFrame {
    public RegisterGUI(UserManager userManager, LoginGUI parent) {
        setTitle("Create Account - Payroll Pro");
        setSize(400, 500);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);
        setResizable(false);

        // Main Panel with Padding
        JPanel panel = new JPanel(new GridLayout(6, 1, 15, 15));
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        panel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Join Payroll Pro", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(41, 128, 185));
        panel.add(lblTitle);

        JTextField userField = new JTextField();
        userField.setBorder(BorderFactory.createTitledBorder("Enter New Username"));
        panel.add(userField);

        JPasswordField passField = new JPasswordField();
        passField.setBorder(BorderFactory.createTitledBorder("Enter New Password"));
        panel.add(passField);

        JPasswordField confirmField = new JPasswordField();
        confirmField.setBorder(BorderFactory.createTitledBorder("Confirm Password"));
        panel.add(confirmField);

        JButton btnRegister = new JButton("CREATE ACCOUNT");
        btnRegister.setBackground(new Color(46, 204, 113)); // Professional Green
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnRegister.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());
            String confirm = new String(confirmField.getPassword());
            
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fields cannot be empty!");
            } else if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!");
            } else {
                if (userManager.register(user, pass)) {
                    JOptionPane.showMessageDialog(this, "Registration Successful!");
                    parent.setUsername(user); // Pass back to login screen
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Username already exists.");
                }
            }
        });

        panel.add(btnRegister);
        add(panel);
    }
}