package com.employee.loan_system.util;

import com.employee.loan_system.dto.EmployeeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExportFactory {
    @Autowired
    private PdfExport pdfExport;

    @Autowired
    private ExcelExport excelExport;

    @Autowired
    private WordExport wordExport;

    @Autowired
    private TxtExport txtExport;

    public byte[] exportData(List<EmployeeDTO> employees, String type) {
        return switch (type.toLowerCase()) {
            case "pdf" -> pdfExport.export(employees);
            case "xlsx" -> excelExport.export(employees);
            case "docx" -> wordExport.export(employees);
            case "txt" -> txtExport.export(employees);
            default -> throw new RuntimeException("Unsupported export type: " + type);
        };
    }
}
