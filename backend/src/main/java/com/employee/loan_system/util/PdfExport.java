package com.employee.loan_system.util;

import com.employee.loan_system.dto.EmployeeDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class PdfExport {
    public byte[] export(List<EmployeeDTO> employees) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            Paragraph title = new Paragraph("Employee List", font);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);

            String[] headers = { "ID", "Emp Code", "First Name", "Last Name", "Department", "Age", "Salary" };
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header));
                cell.setBackgroundColor(new Color(0, 125, 197));
                table.addCell(cell);
            }

            for (EmployeeDTO emp : employees) {
                table.addCell(String.valueOf(emp.getId()));
                table.addCell(emp.getEmpCode());
                table.addCell(emp.getFname());
                table.addCell(emp.getLname());
                table.addCell(emp.getDept());
                table.addCell(String.valueOf(emp.getAge()));
                table.addCell(String.valueOf(emp.getSalary()));
            }

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
