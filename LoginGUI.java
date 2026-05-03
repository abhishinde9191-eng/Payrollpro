package Pay;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class LoginGUI extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private UserManager userManager = new UserManager();
    
    // --- Modern Deep Palette ---
    private Color glassPanelColor = new Color(255, 255, 255, 230); 
    private Color primaryTheme = new Color(24, 34, 45);           
    private Color actionGreen = new Color(39, 174, 96);           
    private Color softGray = new Color(149, 165, 166);            

    public LoginGUI() {
        showSplash(); 
        setTitle("Payroll Pro | Secure Authentication");
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setMinimumSize(new Dimension(1000, 700)); 
        
        setContentPane(new BackgroundPanel());
        setLayout(new GridBagLayout()); 
        
        initUI();
    }

    private class BackgroundPanel extends JPanel {
        private Image img;

        public BackgroundPanel() {
            setBackground(new Color(20, 25, 30)); 
            try {
                java.net.URL imgURL = getClass().getResource("/Pay/pay.png");
                if (imgURL != null) {
                    img = new ImageIcon(imgURL).getImage();
                }
            } catch (Exception e) {}
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (img != null) {
                g2d.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            } else {
                GradientPaint gp = new GradientPaint(0, 0, new Color(44, 62, 80), 0, getHeight(), new Color(20, 30, 48));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    private void showSplash() {
        JWindow splash = new JWindow();
        try {
            java.net.URL imgURL = getClass().getResource("/Pay/pg.png");
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image scaled = icon.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH);
                splash.getContentPane().add(new JLabel(new ImageIcon(scaled)));
                splash.pack();
                splash.setLocationRelativeTo(null);
                splash.setVisible(true);
                Thread.sleep(1500); 
            }
            splash.dispose();
        } catch (Exception e) { splash.dispose(); }
    }

    private void initUI() {
        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setPreferredSize(new Dimension(500, 750)); // Slightly larger for bigger text
        loginCard.setBackground(glassPanelColor);
        loginCard.setOpaque(true); 
        
        loginCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0, 0, 0, 30), 1),
            new EmptyBorder(60, 60, 60, 60)
        ));

        // Header Section - Increased Text
        JLabel lblHeader = new JLabel("WELCOME BACK");
        lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHeader.setFont(new Font("Segoe UI Light", Font.PLAIN, 42)); 
        lblHeader.setForeground(primaryTheme);

        JLabel lblSubHeader = new JLabel("Secure Payroll Management Portal");
        lblSubHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubHeader.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubHeader.setForeground(softGray);

        // Input Fields
        txtUser = new JTextField();
        styleField(txtUser, "USERNAME");

        txtPass = new JPasswordField();
        styleField(txtPass, "PASSWORD");

        // Buttons - Increased Text
        JButton btnLogin = new JButton("SIGN IN TO SYSTEM");
        styleButton(btnLogin, primaryTheme, true);
        btnLogin.addActionListener(e -> {
            if (userManager.validate(txtUser.getText(), new String(txtPass.getPassword()))) {
                PayrollGUI mainApp = new PayrollGUI(txtUser.getText());
                mainApp.setExtendedState(JFrame.MAXIMIZED_BOTH);
                mainApp.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Access Denied: Invalid Credentials", "Auth Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnReg = new JButton("CREATE NEW ACCOUNT");
        styleButton(btnReg, actionGreen, false);
        btnReg.addActionListener(e -> new RegisterGUI(userManager, this).setVisible(true));

        JButton btnForgot = new JButton("Forgot Security Credentials?");
        btnForgot.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnForgot.setBorderPainted(false);
        btnForgot.setContentAreaFilled(false);
        btnForgot.setForeground(primaryTheme);
        btnForgot.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        btnForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnForgot.addActionListener(e -> {
            String user = JOptionPane.showInputDialog(this, "Enter Username for Recovery:");
            String pass = userManager.recoverPassword(user);
            if (pass != null) JOptionPane.showMessageDialog(this, "Recovery Successful! Password: " + pass);
            else JOptionPane.showMessageDialog(this, "Account not identified.");
        });

        // Assembly
        loginCard.add(lblHeader);
        loginCard.add(Box.createRigidArea(new Dimension(0, 10)));
        loginCard.add(lblSubHeader);
        loginCard.add(Box.createRigidArea(new Dimension(0, 80)));
        loginCard.add(txtUser);
        loginCard.add(Box.createRigidArea(new Dimension(0, 35)));
        loginCard.add(txtPass);
        loginCard.add(Box.createRigidArea(new Dimension(0, 65)));
        loginCard.add(btnLogin);
        loginCard.add(Box.createRigidArea(new Dimension(0, 15)));
        loginCard.add(btnReg);
        loginCard.add(Box.createRigidArea(new Dimension(0, 45)));
        loginCard.add(btnForgot);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(loginCard, gbc);
    }

    private void styleField(JTextField field, String title) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70)); // Taller for bigger text
        
        // Fix for TitledBorder Error: Create Matte Border then wrap in TitledBorder
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(180, 180, 180)), title);
        
        titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
        titledBorder.setTitleColor(softGray);
        
        field.setBorder(titledBorder);
        field.setOpaque(false);
        field.setFont(new Font("Segoe UI Semilight", Font.PLAIN, 24)); // Larger input text
        field.setForeground(primaryTheme);
        field.setCaretColor(primaryTheme);
    }

    private void styleButton(JButton btn, Color bg, boolean isPrimary) {
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Larger button text
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(bg.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(bg); }
        });
    }
    
    public void setUsername(String user) { txtUser.setText(user); }
}