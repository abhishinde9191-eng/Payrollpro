package Pay;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.border.TitledBorder;

// OpenPDF Library Imports
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class PayrollGUI extends JFrame {
    private ArrayList<Employee> empList = new ArrayList<>();
    private ArrayList<Payroll> payList = new ArrayList<>();
    private EmployeeManager manager = new EmployeeManager();
    private DefaultTableModel tableModel;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private String sessionPassword; 
    
    // Global Fixed Deduction Rate (e.g., 10% for Tax/PF as per global standards)
    private final double GLOBAL_DEDUCTION_RATE = 0.10; 

    private JTextField txtId, txtName, txtDept, txtDes, txtSalary, txtBank, txtIFSC, txtAccount, txtEmail;
    private JTextField txtSearchId, txtFilter, txtDeleteId, txtLeaves;

    private Color sideColor = new Color(26, 37, 47);          
    private Color panelColor = new Color(255, 255, 255, 215); 
    private Color accentColor = new Color(39, 174, 96);      
    private Color btnBlue = new Color(41, 128, 185);         
    private Color btnRed = new Color(192, 57, 43);           

    public PayrollGUI(String username) {
        this.sessionPassword = username; 
        setTitle("Payroll Management System Pro"); 
        setSize(1200, 850); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setLocationRelativeTo(null);
        
        setContentPane(new BackgroundPanel());
        setLayout(new BorderLayout());

        loadDataFromDatabase();
        initUI();
        refreshTable();
        setVisible(true);
    }

    private class BackgroundPanel extends JPanel {
        private Image img;
        public BackgroundPanel() {
            setBackground(new Color(235, 240, 245));
            try {
                java.net.URL imgURL = getClass().getResource("p.png");
                if (imgURL != null) img = new ImageIcon(imgURL).getImage();
            } catch (Exception e) {}
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void initUI() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(sideColor);
        sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JLabel lblLogo = new JLabel("PAYROLL PRO");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
        sidebar.add(lblLogo);

        JButton btnLogout = new JButton("Logout");
        styleBtn(btnLogout, btnRed);
        btnLogout.addActionListener(e -> this.dispose());
        sidebar.add(btnLogout);
        add(sidebar, BorderLayout.WEST);

        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setOpaque(false);
        workspace.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formContainer = new JPanel(new GridLayout(11, 2, 10, 10));
        formContainer.setBackground(panelColor);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(sideColor), "REGISTER NEW EMPLOYEE");
        titledBorder.setTitleFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        formContainer.setBorder(titledBorder);

        java.awt.Font fieldFont = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16);
        java.awt.Font labelFont = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 15);

        txtId = new JTextField(); txtName = new JTextField(); txtDept = new JTextField();
        txtDes = new JTextField(); txtSalary = new JTextField(); txtBank = new JTextField();
        txtIFSC = new JTextField(); txtAccount = new JTextField(); txtEmail = new JTextField();

        JTextField[] fields = {txtId, txtName, txtDept, txtDes, txtSalary, txtBank, txtIFSC, txtAccount, txtEmail};
        String[] labels = {"Employee ID:", "Full Name:", "Department:", "Designation:", "Salary:", "Bank Name:", "IFSC:", "A/C Number:", "Email:"};

        for (int i = 0; i < fields.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(labelFont);
            fields[i].setFont(fieldFont);
            formContainer.add(lbl);
            formContainer.add(fields[i]);
        }

        JButton btnAdd = new JButton("SAVE EMPLOYEE");
        styleBtn(btnAdd, accentColor);
        btnAdd.addActionListener(this::handleAddEmployee);
        formContainer.add(new JLabel(""));
        formContainer.add(btnAdd);
        workspace.add(formContainer, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Dept", "Email", "Basic", "Total Ded.", "Net Salary"}, 0);
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        styleTable(table);
        
        txtFilter = new JTextField();
        txtFilter.setBorder(BorderFactory.createTitledBorder("Search by Name or ID"));
        txtFilter.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = txtFilter.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.add(txtFilter, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        workspace.add(tablePanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new GridLayout(2, 1));
        footerPanel.setOpaque(false);
        JPanel payRow = new JPanel(new FlowLayout());
        payRow.setBackground(panelColor);
        
        // Removed Other Deduction Field - Now uses Global Fix
        txtSearchId = new JTextField(5); txtLeaves = new JTextField("0", 3);
        JButton btnPay = new JButton("Process Salary");
        JButton btnPrint = new JButton("PDF & Email Slip");
        styleBtn(btnPay, btnBlue); styleBtn(btnPrint, new Color(142, 68, 173));

        payRow.add(new JLabel("ID:")); payRow.add(txtSearchId);
        payRow.add(new JLabel("Leaves:")); payRow.add(txtLeaves);
        payRow.add(btnPay); payRow.add(btnPrint);

        JPanel deleteRow = new JPanel(new FlowLayout());
        deleteRow.setBackground(panelColor);
        txtDeleteId = new JTextField(10);
        JButton btnDelete = new JButton("DELETE");
        styleBtn(btnDelete, Color.ORANGE);
        deleteRow.add(new JLabel("Delete ID:")); deleteRow.add(txtDeleteId);
        deleteRow.add(btnDelete);

        footerPanel.add(payRow); footerPanel.add(deleteRow);
        workspace.add(footerPanel, BorderLayout.SOUTH);
        add(workspace, BorderLayout.CENTER);

        btnPay.addActionListener(this::handleProcessSalary);
        btnPrint.addActionListener(this::handlePrintAndEmail);
        btnDelete.addActionListener(this::handleDeleteEmployee);
    }

    private void handleProcessSalary(ActionEvent e) {
        try {
            int id = Integer.parseInt(txtSearchId.getText());
            Employee emp = empList.stream().filter(o -> o.getId() == id).findFirst().orElse(null);
            if (emp != null) {
                int leaveDays = Integer.parseInt(txtLeaves.getText());
                
                // Automatic Global Deduction Calculation
                double fixedDeduction = emp.getBasicSalary() * GLOBAL_DEDUCTION_RATE;
                double perDay = emp.getBasicSalary() / 30.0;
                double leaveCut = perDay * leaveDays;
                double totalDed = fixedDeduction + leaveCut;

                payList.removeIf(p -> p.getEmpId() == id);
                payList.add(new Payroll(id, emp.getBasicSalary(), totalDed));
                refreshTable();
                JOptionPane.showMessageDialog(this, "Salary Processed (Fix Ded + Leaves).");
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid Entry"); }
    }

    private void handlePrintAndEmail(ActionEvent e) {
        try {
            int id = Integer.parseInt(txtSearchId.getText());
            Employee emp = empList.stream().filter(o -> o.getId() == id).findFirst().orElse(null);
            Payroll pay = payList.stream().filter(p -> p.getEmpId() == id).findFirst().orElse(null);

            if (emp != null && pay != null) {
                String filename = emp.getName() + "_SalarySlip.pdf";
                generatePDF(filename, emp, pay);
                sendEmail(emp.getEmail(), emp.getName(), "Dear " + emp.getName() + ",\nPlease find your salary details attached.", filename);
            } else {
                JOptionPane.showMessageDialog(this, "Please process salary first!");
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error in inputs."); }
    }

    private void generatePDF(String filename, Employee emp, Payroll pay) {
        Document doc = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(filename));
            doc.open();

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            Paragraph title = new Paragraph("PAYROLL PRO - SALARY SLIP", headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);
            doc.add(new Paragraph("\n"));

            PdfPTable outerTable = new PdfPTable(1);
            outerTable.setWidthPercentage(100);

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.addCell(new Phrase("Date: " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()), normalFont));
            infoTable.addCell(new Phrase("Employee ID: " + emp.getId(), normalFont));
            infoTable.addCell(new Phrase("Name: " + emp.getName(), normalFont));
            infoTable.addCell(new Phrase("Department: " + emp.getDepartment(), normalFont));

            PdfPCell infoCell = new PdfPCell(infoTable);
            infoCell.setPadding(10);
            outerTable.addCell(infoCell);

            PdfPTable detailsTable = new PdfPTable(2);
            detailsTable.setWidthPercentage(100);
            
            int leaveDays = Integer.parseInt(txtLeaves.getText());
            double perDay = emp.getBasicSalary() / 30.0;
            double leaveCut = perDay * leaveDays;
            double fixedDed = emp.getBasicSalary() * GLOBAL_DEDUCTION_RATE;

            detailsTable.addCell("Basic Salary");
            detailsTable.addCell(String.format("%.2f", emp.getBasicSalary()));
            detailsTable.addCell("Standard Tax/Ded (Fix)");
            detailsTable.addCell(String.format("-%.2f", fixedDed));
            detailsTable.addCell("Unpaid Leaves (" + leaveDays + " days)");
            detailsTable.addCell(String.format("-%.2f", leaveCut));

            PdfPCell netCellLabel = new PdfPCell(new Phrase("NET PAYABLE", subHeaderFont));
            netCellLabel.setBackgroundColor(java.awt.Color.CYAN);
            detailsTable.addCell(netCellLabel);
            PdfPCell netCellVal = new PdfPCell(new Phrase(String.format("%.2f", pay.getNetSalary()), subHeaderFont));
            netCellVal.setBackgroundColor(java.awt.Color.CYAN);
            detailsTable.addCell(netCellVal);

            PdfPCell detailsCell = new PdfPCell(detailsTable);
            outerTable.addCell(detailsCell);
            outerTable.addCell(new PdfPCell(new Phrase("Bank: " + emp.getBankName() + " | A/C: " + emp.getAccountNo(), normalFont)));

            doc.add(outerTable);
            doc.close();
            JOptionPane.showMessageDialog(this, "PDF Saved: " + filename);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void sendEmail(String toEmail, String name, String content, String attachmentPath) {
        final String user = "yashrajturakne@gmail.com"; 
        final String pass = "xecambiofqxytapx"; 
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session sess = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });

        new Thread(() -> {
            try {
                Message msg = new MimeMessage(sess);
                msg.setFrom(new InternetAddress(user));
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                msg.setSubject("Salary Slip - " + name);

                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(content);
                MimeBodyPart filePart = new MimeBodyPart();
                filePart.attachFile(new File(attachmentPath));

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(textPart);
                multipart.addBodyPart(filePart);

                msg.setContent(multipart);
                Transport.send(msg);
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Email Sent!"));
            } catch (Exception ex) { 
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Email Failed"));
            }
        }).start();
    }

    private void handleDeleteEmployee(ActionEvent e) {
        try {
            int id = Integer.parseInt(txtDeleteId.getText());
            if (manager.deleteEmployee(id)) { 
                empList.removeIf(emp -> emp.getId() == id);
                payList.removeIf(p -> p.getEmpId() == id);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Record Deleted.");
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid ID"); }
    }

    private void handleAddEmployee(ActionEvent e) {
        try {
            Employee emp = new Employee(Integer.parseInt(txtId.getText()), txtName.getText(), txtDept.getText(), txtDes.getText(), Double.parseDouble(txtSalary.getText()), txtBank.getText(), txtIFSC.getText(), txtAccount.getText(), txtEmail.getText());
            if (manager.addEmployee(emp)) {
                empList.add(emp); refreshTable();
                JOptionPane.showMessageDialog(this, "Registration Successful!");
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Check inputs!"); }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Employee emp : empList) {
            Payroll pay = payList.stream().filter(p -> p.getEmpId() == emp.getId()).findFirst().orElse(null);
            double ded = (pay != null) ? (emp.getBasicSalary() - pay.getNetSalary()) : 0.0;
            double net = (pay != null) ? pay.getNetSalary() : 0.0;
            tableModel.addRow(new Object[]{ emp.getId(), emp.getName(), emp.getDepartment(), emp.getEmail(), emp.getBasicSalary(), String.format("%.2f", ded), (net > 0 ? String.format("%.2f", net) : "Pending") });
        }
    }

    private void styleBtn(JButton b, Color c) { 
        b.setBackground(c); b.setForeground(Color.WHITE); 
        b.setFocusPainted(false); b.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14)); 
    }
    
    private void styleTable(JTable t) { 
        t.setRowHeight(35); t.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        t.getTableHeader().setBackground(sideColor); t.getTableHeader().setForeground(Color.WHITE);
    }
    
    private void loadDataFromDatabase() {
        try {
            List<Employee> dbs = manager.getAllEmployees();
            if (dbs != null) empList = new ArrayList<>(dbs);
        } catch (Exception e) {}
    }
}