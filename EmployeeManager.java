package Pay;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeManager {
    
    public boolean addEmployee(Employee emp) throws Exception {
        // Specifying column names explicitly ensures data goes to the right place
        String sql = "INSERT INTO employees (id, name, department, designation, salary, bank_name, ifsc, account_no, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, emp.getId());
            pstmt.setString(2, emp.getName());
            pstmt.setString(3, emp.getDepartment());
            pstmt.setString(4, emp.getDesignation());
            pstmt.setDouble(5, emp.getBasicSalary());
            pstmt.setString(6, emp.getBankName());
            pstmt.setString(7, emp.getIFSC());
            // FIXED: Changed from getBankDetails() to getAccountNo() to match Employee class
            pstmt.setString(8, emp.getAccountNo()); 
            pstmt.setString(9, emp.getEmail());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteEmployee(int id) throws Exception {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Employee> getAllEmployees() throws Exception {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                // Mapping ResultSet columns to the Employee constructor
                list.add(new Employee(
                    rs.getInt("id"), 
                    rs.getString("name"), 
                    rs.getString("department"),
                    rs.getString("designation"), 
                    rs.getDouble("salary"), 
                    rs.getString("bank_name"),
                    rs.getString("ifsc"), 
                    rs.getString("account_no"), // Ensure this matches your MySQL column name
                    rs.getString("email")
                ));
            }
        }
        return list;
    }
}