package com.employee.loan_system.util;

import com.employee.loan_system.dto.EmployeeDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class ExcelExport {
    public byte[] export(List<EmployeeDTO> employees) {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Employees");

            Row headerRow = sheet.createRow(0);
            String[] columns = { "ID", "Emp Code", "First Name", "Last Name", "Full Name", "Department", "Age",
                    "Salary" };

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (EmployeeDTO emp : employees) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(emp.getId());
                row.createCell(1).setCellValue(emp.getEmpCode());
                row.createCell(2).setCellValue(emp.getFname());
                row.createCell(3).setCellValue(emp.getLname());
                row.createCell(4).setCellValue(emp.getFullname());
                row.createCell(5).setCellValue(emp.getDept());
                row.createCell(6).setCellValue(emp.getAge());
                row.createCell(7).setCellValue(emp.getSalary());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel", e);
        }
    }
}
