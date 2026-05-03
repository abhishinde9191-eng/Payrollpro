
package Pay;
import java.io.Serializable;

public class Payroll implements Serializable {
    private int empId;
    private double netSalary;

    public Payroll(int empId, double basic, double deductions) {
        this.empId = empId;
        this.netSalary = basic - deductions;
    }

    public int getEmpId() { return empId; }
    public double getNetSalary() { return netSalary; }
}