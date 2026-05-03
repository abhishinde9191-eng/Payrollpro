package Pay;

import java.io.Serializable;

/**
 * Standardized Employee Class for Payroll Management System
 */
public class Employee implements Serializable {
    private int id;
    private String name;
    private String department;
    private String designation;
    private double basicSalary;
    private String bankName;
    private String ifsc;
    private String accountNo; // Variable name used for bank account
    private String email;

    // Full Constructor
    public Employee(int id, String name, String department, String designation, 
                    double basicSalary, String bankName, String ifsc, 
                    String accountNo, String email) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.designation = designation;
        this.basicSalary = basicSalary;
        this.bankName = bankName;
        this.ifsc = ifsc;
        this.accountNo = accountNo;
        this.email = email;
    }

    // Standard Getters
    public int getId() { 
        return id; 
    }

    public String getName() { 
        return name; 
    }

    public String getDepartment() { 
        return department; 
    }

    public String getDesignation() { 
        return designation; 
    }

    public double getBasicSalary() { 
        return basicSalary; 
    }

    public String getBankName() { 
        return bankName; 
    }

    public String getIFSC() { 
        return ifsc; 
    }

    // IMPORTANT: This method must be named exactly getAccountNo() 
    // to match the calls in PayrollGUI and EmployeeManager
    public String getAccountNo() { 
        return accountNo; 
    }

    public String getEmail() { 
        return email; 
    }

    // Setters (Useful for updates)
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDepartment(String department) { this.department = department; }
    public void setDesignation(String designation) { this.designation = designation; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public void setIFSC(String ifsc) { this.ifsc = ifsc; }
    public void setAccountNo(String accountNo) { this.accountNo = accountNo; }
    public void setEmail(String email) { this.email = email; }
}