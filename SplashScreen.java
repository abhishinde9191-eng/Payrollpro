package Pay;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {
    private JProgressBar progressBar;
    private JLabel lblStatus;
    private float opacity = 0f;

    public SplashScreen() {
        // 1. Fullscreen Setup
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize);
        setLocation(0, 0);
        setBackground(new Color(0, 0, 0, 0)); 

        // 2. High-End Rendering Panel
        JPanel content = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Enable Highest Quality Rendering
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Deep Professional Gradient
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 22, 33), 0, getHeight(), new Color(32, 45, 66));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // --- TEXT BASED LOGO REPLACEMENT ---
                String title = "PAYROLL PRO";
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 110)); // Large standard form
                
                // Calculate position for center
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(title)) / 2;
                int y = (getHeight() / 2);

                // Apply Animation Opacity
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                
                // Subtle Text Shadow
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.drawString(title, x + 5, y + 5);

                // Main White Text
                g2d.setColor(Color.WHITE);
                g2d.drawString(title, x, y);
                
                g2d.dispose();
            }
        };
        content.setOpaque(false);

        // 3. Modern UI Components
        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        bottomContainer.setOpaque(false);
        bottomContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 0));

        progressBar = new JProgressBar(0, 100);
        progressBar.setMaximumSize(new Dimension(600, 10)); // Slightly thicker
        progressBar.setForeground(new Color(52, 152, 219)); 
        progressBar.setBackground(new Color(255, 255, 255, 15));
        progressBar.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,30), 1));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblStatus = new JLabel("Initializing Application...");
        lblStatus.setForeground(new Color(200, 210, 220));
        lblStatus.setFont(new Font("Segoe UI Light", Font.PLAIN, 22)); // Increased text size
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomContainer.add(lblStatus);
        bottomContainer.add(Box.createRigidArea(new Dimension(0, 25)));
        bottomContainer.add(progressBar);

        content.add(bottomContainer, BorderLayout.SOUTH);
        setContentPane(content);

        startLoading();
    }

    private void startLoading() {
        new Thread(() -> {
            try {
                // Smooth Text Fade In
                for (int i = 0; i <= 100; i++) {
                    opacity = i / 100f;
                    repaint();
                    Thread.sleep(12);
                }

                String[] sequence = {
                    "Loading core resources...",
                    "Checking database integrity...",
                    "Configuring secure gateway...",
                    "Loading dashboard modules...",
                    "System operational"
                };

                for (int i = 0; i <= 100; i++) {
                    if (i < 30) Thread.sleep(40);
                    else if (i < 70) Thread.sleep(20);
                    else Thread.sleep(50);
                    
                    progressBar.setValue(i);

                    if (i == 10) lblStatus.setText(sequence[0]);
                    if (i == 40) lblStatus.setText(sequence[1]);
                    if (i == 65) lblStatus.setText(sequence[2]);
                    if (i == 85) lblStatus.setText(sequence[3]);
                    if (i == 98) lblStatus.setText(sequence[4]);
                }

                Thread.sleep(700);
                dispose();

                SwingUtilities.invokeLater(() -> {
                    LoginGUI login = new LoginGUI();
                    login.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    login.setVisible(true);
                });

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }).start();
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new SplashScreen().setVisible(true));
    }
}