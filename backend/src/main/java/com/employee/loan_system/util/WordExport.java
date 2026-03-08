package com.employee.loan_system.util;

import com.employee.loan_system.dto.EmployeeDTO;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class WordExport {
    public byte[] export(List<EmployeeDTO> employees) {
        try (XWPFDocument document = new XWPFDocument();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("Employee List");
            titleRun.setBold(true);
            titleRun.setFontSize(18);

            XWPFTable table = document.createTable(employees.size() + 1, 7);

            String[] headers = { "ID", "Emp Code", "First Name", "Last Name", "Department", "Age", "Salary" };
            XWPFTableRow headerRow = table.getRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.getCell(i).setText(headers[i]);
            }

            for (int i = 0; i < employees.size(); i++) {
                EmployeeDTO emp = employees.get(i);
                XWPFTableRow row = table.getRow(i + 1);
                row.getCell(0).setText(String.valueOf(emp.getId()));
                row.getCell(1).setText(emp.getEmpCode());
                row.getCell(2).setText(emp.getFname());
                row.getCell(3).setText(emp.getLname());
                row.getCell(4).setText(emp.getDept());
                row.getCell(5).setText(String.valueOf(emp.getAge()));
                row.getCell(6).setText(String.valueOf(emp.getSalary()));
            }

            document.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Word document", e);
        }
    }
}
