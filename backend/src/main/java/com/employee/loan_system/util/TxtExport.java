package com.employee.loan_system.util;

import com.employee.loan_system.dto.EmployeeDTO;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

@Component
public class TxtExport {
    public byte[] export(List<EmployeeDTO> employees) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintWriter writer = new PrintWriter(baos)) {

            writer.println("EMPLOYEE LIST");
            writer.println("=".repeat(100));
            writer.printf("%-5s %-12s %-15s %-15s %-20s %-5s %-12s%n",
                    "ID", "Emp Code", "First Name", "Last Name", "Department", "Age", "Salary");
            writer.println("-".repeat(100));

            for (EmployeeDTO emp : employees) {
                writer.printf("%-5d %-12s %-15s %-15s %-20s %-5d %-12.2f%n",
                        emp.getId(),
                        emp.getEmpCode(),
                        emp.getFname(),
                        emp.getLname(),
                        emp.getDept(),
                        emp.getAge(),
                        emp.getSalary());
            }

            writer.println("=".repeat(100));
            writer.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating TXT file", e);
        }
    }
}
