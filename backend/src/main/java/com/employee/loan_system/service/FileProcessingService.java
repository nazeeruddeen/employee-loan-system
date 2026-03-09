package com.employee.loan_system.service;

import com.employee.loan_system.dto.PersonDetailsDTO;
import com.employee.loan_system.dto.SalesReportDTO;
import com.employee.loan_system.dto.TransactionDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FileProcessingService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<PersonDetailsDTO> processJsonFile(MultipartFile file) throws Exception {
        return objectMapper.readValue(file.getInputStream(), new TypeReference<List<PersonDetailsDTO>>() {
        });
    }

    public List<SalesReportDTO> processExcelFile(MultipartFile file) throws Exception {
        List<SalesReportDTO> reports = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            int headerRowNum = findSalesHeaderRow(sheet);
            if (headerRowNum < 0) {
                throw new Exception("Could not detect sales data header row in the uploaded Excel file");
            }

            Row headerRow = sheet.getRow(headerRowNum);
            int dateColumn = findHeaderColumnIndex(headerRow, "DATE");
            int orderColumn = findHeaderColumnIndex(headerRow, "ORDER");
            int invoiceColumn = findHeaderColumnIndex(headerRow, "INVOICE");
            int partyNameColumn = findHeaderColumnIndex(headerRow, "PARTY");
            int partyPhoneColumn = findHeaderColumnIndex(headerRow, "PHONE", "MOBILE", "CONTACT");
            int totalAmountColumn = findHeaderColumnIndex(headerRow, "TOTAL");
            int receivedAmountColumn = findHeaderColumnIndex(headerRow, "RECEIVED", "RECIEVED", "PAID");
            int balanceAmountColumn = findHeaderColumnIndex(headerRow, "BALANCE");

            if (dateColumn < 0 || orderColumn < 0 || invoiceColumn < 0 || partyNameColumn < 0 || totalAmountColumn < 0) {
                throw new Exception("Required sales columns not found in Excel header row");
            }

            int dataStartRow = headerRowNum + 1;

            for (int i = dataStartRow; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                try {
                    String dateValue = getDateCellValue(row.getCell(dateColumn));
                    String orderNo = getCellValueByIndex(row, orderColumn);
                    String invoiceNo = getCellValueByIndex(row, invoiceColumn);
                    String partyName = getCellValueByIndex(row, partyNameColumn);
                    String partyPhone = getCellValueByIndex(row, partyPhoneColumn);
                    String totalAmtStr = getCellValueByIndex(row, totalAmountColumn);
                    String receivedAmtStr = getCellValueByIndex(row, receivedAmountColumn);
                    String balanceAmtStr = getCellValueByIndex(row, balanceAmountColumn);

                    String rowText = String.join(" ", dateValue, orderNo, invoiceNo, partyName, partyPhone, totalAmtStr, receivedAmtStr, balanceAmtStr)
                            .trim()
                            .toUpperCase();

                    if (rowText.isEmpty()) {
                        continue;
                    }

                    if (rowText.contains("PHONE NO") || rowText.contains("EMAIL ID") || rowText.contains("SIGNATURE") ||
                            rowText.contains("SEAL") || rowText.contains("NOTE")) {
                        continue;
                    }

                    if (rowText.contains("TOTAL") && orderNo.isEmpty() && invoiceNo.isEmpty()) {
                        continue;
                    }

                    if (("DATE".equalsIgnoreCase(dateValue) || dateValue.toUpperCase().contains("DATE")) &&
                            orderNo.toUpperCase().contains("ORDER") && invoiceNo.toUpperCase().contains("INVOICE")) {
                        continue;
                    }

                    if (dateValue.isEmpty() && orderNo.isEmpty() && invoiceNo.isEmpty() && partyName.isEmpty()) {
                        continue;
                    }

                    SalesReportDTO dto = new SalesReportDTO();
                    dto.setDate(dateValue);
                    dto.setOrderno(orderNo);
                    dto.setInvoiceno(invoiceNo);
                    dto.setPartyName(partyName);
                    dto.setPartyPhoneNum(partyPhone);
                    dto.setTotalAmount(parseDoubleSafe(totalAmtStr));
                    dto.setRecievedOrPaidAmount(parseDoubleSafe(receivedAmtStr));
                    dto.setBalanceAmount(parseDoubleSafe(balanceAmtStr));

                    reports.add(dto);
                } catch (Exception e) {
                    errors.add("Error processing row " + (i + 1) + ": " + e.getMessage());
                }
            }
        }

        if (reports.isEmpty() && !errors.isEmpty()) {
            throw new Exception("Excel processing errors: " + String.join("; ", errors));
        }

        return reports;
    }

    private int findSalesHeaderRow(Sheet sheet) {
        int maxRows = Math.min(30, sheet.getLastRowNum());
        for (int i = 0; i <= maxRows; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            int matches = 0;
            if (rowContainsAny(row, "DATE")) matches++;
            if (rowContainsAny(row, "ORDER")) matches++;
            if (rowContainsAny(row, "INVOICE")) matches++;
            if (rowContainsAny(row, "PARTY")) matches++;
            if (rowContainsAny(row, "TOTAL")) matches++;

            if (matches >= 4) {
                return i;
            }
        }
        return -1;
    }

    private boolean rowContainsAny(Row row, String... tokens) {
        for (Cell cell : row) {
            String value = getCellValueAsString(cell).trim().toUpperCase();
            if (value.isEmpty()) {
                continue;
            }
            for (String token : tokens) {
                if (value.contains(token.toUpperCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    private int findHeaderColumnIndex(Row headerRow, String... tokens) {
        if (headerRow == null) {
            return -1;
        }

        short first = headerRow.getFirstCellNum();
        short last = headerRow.getLastCellNum();
        if (first < 0 || last < 0) {
            return -1;
        }

        for (int col = first; col < last; col++) {
            String value = getCellValueAsString(headerRow.getCell(col)).trim().toUpperCase();
            if (value.isEmpty()) {
                continue;
            }
            for (String token : tokens) {
                if (value.contains(token.toUpperCase())) {
                    return col;
                }
            }
        }

        return -1;
    }

    private String getCellValueByIndex(Row row, int index) {
        if (row == null || index < 0) {
            return "";
        }
        return getCellValueAsString(row.getCell(index)).trim();
    }

    public List<TransactionDTO> processCsvFile(MultipartFile file) throws Exception {
        List<TransactionDTO> transactions = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        List<String[]> parsedLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    parsedLines.add(splitCsvLine(line));
                }
            }
        }

        if (parsedLines.isEmpty()) {
            throw new Exception("CSV file is empty");
        }

        int headerRowIndex = findTransactionHeaderRow(parsedLines);
        String[] headerRow = headerRowIndex >= 0 ? parsedLines.get(headerRowIndex) : new String[0];
        int dataStartRow = headerRowIndex >= 0 ? headerRowIndex + 1 : 0;

        int dateColumn = findCsvColumnIndex(headerRow, "date", "transaction date", "date & time", "date/time");
        int activityColumn = findCsvColumnIndex(headerRow, "activity", "description", "transaction details", "narration", "remarks");
        int sourceDestColumn = findCsvColumnIndex(headerRow, "source/destination", "source", "destination");
        int instrumentColumn = findCsvColumnIndex(headerRow, "instrument", "mode", "payment mode", "channel", "source");
        int txnTypeColumn = findCsvColumnIndex(headerRow, "txn", "transaction type", "type");
        int idColumn = findCsvColumnIndex(headerRow, "id");
        int txnIdColumn = findCsvColumnIndex(headerRow, "wallet txn id", "txn id", "transaction id", "reference", "utr", "ref no");
        int commentColumn = findCsvColumnIndex(headerRow, "comment", "remarks", "note");
        int debitColumn = findCsvColumnIndex(headerRow, "debit", "dr", "withdrawal", "sent", "paid");
        int creditColumn = findCsvColumnIndex(headerRow, "credit", "cr", "deposit", "received", "added");
        int breakupColumn = findCsvColumnIndex(headerRow, "breakup", "details", "category");
        int statusColumn = findCsvColumnIndex(headerRow, "status", "txn status", "result");
        int amountColumn = findCsvColumnIndex(headerRow, "amount", "transaction amount", "value", "amt");

        long generatedTxnId = 1_000_000L;

        for (int i = dataStartRow; i < parsedLines.size(); i++) {
            String[] row = parsedLines.get(i);

            if (isCsvRowEmpty(row) || isLikelyTransactionHeader(row)) {
                continue;
            }

            try {
                String dateValue = firstNonBlank(
                        getCsvValue(row, dateColumn, 0),
                        getCsvValue(row, 0, -1)
                );
                LocalDateTime transactionDate = parseLocalDateTimeFlexible(dateValue);
                if (transactionDate == null) {
                    errors.add("Line " + (i + 1) + ": Invalid or missing transaction date");
                    continue;
                }

                String activity = firstNonBlank(
                        getCsvValue(row, activityColumn, 1),
                        getCsvValue(row, txnTypeColumn, -1),
                        getCsvValue(row, breakupColumn, -1),
                        "Transaction"
                );

                String instrumentRaw = firstNonBlank(
                        getCsvValue(row, sourceDestColumn, -1),
                        getCsvValue(row, instrumentColumn, 2),
                        getCsvValue(row, idColumn, -1),
                        file.getOriginalFilename()
                );

                String txnReference = firstNonBlank(
                        getCsvValue(row, txnIdColumn, 3),
                        getCsvValue(row, commentColumn, -1),
                        getCsvValue(row, idColumn, -1),
                        extractPotentialTxnReference(row)
                );
                Long txnId = parseTxnIdSafe(txnReference);
                if (txnId == null) {
                    txnId = generatedTxnId++;
                }

                Double debit = parseDoubleSafe(getCsvValue(row, debitColumn, 5));
                Double credit = parseDoubleSafe(getCsvValue(row, creditColumn, 6));
                if (debit == 0.0 && credit == 0.0) {
                    Double amount = parseDoubleSafe(getCsvValue(row, amountColumn, 5));
                    if (amount < 0) {
                        debit = Math.abs(amount);
                    } else {
                        credit = amount;
                    }
                }

                String comment = firstNonBlank(
                        getCsvValue(row, commentColumn, 4)
                );

                String statusRaw = firstNonBlank(
                        getCsvValue(row, statusColumn, 8),
                        findStatusToken(row),
                        "SUCCESS"
                );

                TransactionDTO dto = new TransactionDTO();
                dto.setTransactionDate(transactionDate);
                dto.setActivity(trimToLength(activity, 100));
                dto.setInstrument(normalizeInstrument(instrumentRaw));
                dto.setTxnId(txnId);
                dto.setComment(trimToLength(comment, 500));
                dto.setDebtAmt(Math.max(0.0, debit));
                dto.setCreditAmt(Math.max(0.0, credit));
                dto.setTransactionBreakup(trimToLength(firstNonBlank(getCsvValue(row, breakupColumn, 7)), 1000));
                dto.setTransactionStatus(normalizeStatus(statusRaw));

                transactions.add(dto);
            } catch (Exception e) {
                errors.add("Line " + (i + 1) + ": " + e.getMessage());
            }
        }

        if (transactions.isEmpty()) {
            if (!errors.isEmpty()) {
                throw new Exception("CSV parsing errors: " + String.join("; ", errors));
            }
            throw new Exception("No valid transaction rows found in CSV file");
        }

        return transactions;
    }

    private String[] splitCsvLine(String line) {
        String[] values;
        if (line.contains(",")) {
            values = line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
        } else if (line.contains(";")) {
            values = line.split(";", -1);
        } else if (line.contains("\t")) {
            values = line.split("\t", -1);
        } else {
            values = line.trim().split("\\s{2,}");
            if (values.length <= 1) {
                values = line.trim().split("\\s+");
            }
        }

        for (int i = 0; i < values.length; i++) {
            values[i] = cleanCsvValue(values[i]);
        }
        return values;
    }

    private String cleanCsvValue(String value) {
        if (value == null) {
            return "";
        }
        String cleaned = value.replace("\uFEFF", "").trim();
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"") && cleaned.length() >= 2) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        return cleaned.trim();
    }

    private boolean isCsvRowEmpty(String[] row) {
        if (row == null || row.length == 0) {
            return true;
        }
        for (String cell : row) {
            if (cell != null && !cell.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isLikelyTransactionHeader(String[] row) {
        String all = String.join(" ", row).toUpperCase();
        return all.contains("DATE") &&
                (all.contains("TXN") || all.contains("TRANSACTION") || all.contains("REFERENCE")) &&
                (all.contains("AMOUNT") || all.contains("DEBIT") || all.contains("CREDIT"));
    }

    private int findTransactionHeaderRow(List<String[]> lines) {
        int max = Math.min(25, lines.size());
        for (int i = 0; i < max; i++) {
            if (isLikelyTransactionHeader(lines.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private int findCsvColumnIndex(String[] header, String... tokens) {
        if (header == null || header.length == 0) {
            return -1;
        }

        for (int i = 0; i < header.length; i++) {
            String value = normalizeHeaderText(header[i]);
            for (String token : tokens) {
                if (value.contains(normalizeHeaderText(token))) {
                    return i;
                }
            }
        }

        return -1;
    }

    private String normalizeHeaderText(String value) {
        if (value == null) {
            return "";
        }
        return cleanCsvValue(value)
                .toUpperCase()
                .replaceAll("[^A-Z0-9]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String getCsvValue(String[] row, int preferredIndex, int fallbackIndex) {
        if (row == null) {
            return "";
        }

        if (preferredIndex >= 0 && preferredIndex < row.length) {
            String preferred = cleanCsvValue(row[preferredIndex]);
            if (!preferred.isEmpty()) {
                return preferred;
            }
        }

        if (fallbackIndex >= 0 && fallbackIndex < row.length) {
            return cleanCsvValue(row[fallbackIndex]);
        }

        return "";
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return "";
    }

    private String trimToLength(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        return trimmed.length() <= maxLength ? trimmed : trimmed.substring(0, maxLength);
    }

    private LocalDateTime parseLocalDateTimeFlexible(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String input = value.trim().replaceAll("\\s+", " ");

        DateTimeFormatter[] dateTimeFormatters = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        };

        for (DateTimeFormatter formatter : dateTimeFormatters) {
            try {
                return LocalDateTime.parse(input, formatter);
            } catch (Exception ignored) {
                // try next format
            }
        }

        DateTimeFormatter[] dateFormatters = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("dd-MMM-yyyy"),
                DateTimeFormatter.ofPattern("d/M/yyyy")
        };

        for (DateTimeFormatter formatter : dateFormatters) {
            try {
                return LocalDate.parse(input, formatter).atStartOfDay();
            } catch (Exception ignored) {
                // try next format
            }
        }

        return null;
    }

    private String extractDateToken(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        String[] tokens = text.trim().split("\\s+");
        for (String token : tokens) {
            if (token.matches("\\d{2}[-/]\\d{2}[-/]\\d{4}") || token.matches("\\d{4}[-/]\\d{2}[-/]\\d{2}")) {
                return token;
            }
        }

        return "";
    }

    private Long parseTxnIdSafe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String digits = value.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return null;
        }

        if (digits.length() > 18) {
            digits = digits.substring(digits.length() - 18);
        }

        try {
            return Long.parseLong(digits);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String extractPotentialTxnReference(String[] row) {
        if (row == null) {
            return "";
        }

        String bestCandidate = "";
        int bestLength = 0;

        for (String cell : row) {
            String value = cleanCsvValue(cell);
            String digits = value.replaceAll("[^0-9]", "");

            if (value.matches("\\d{2}[-/]\\d{2}[-/]\\d{4}") || value.matches("\\d{4}[-/]\\d{2}[-/]\\d{2}")) {
                continue;
            }

            if (digits.length() >= 6 && digits.length() > bestLength) {
                bestCandidate = value;
                bestLength = digits.length();
            }
        }

        return bestCandidate;
    }

    private String normalizeInstrument(String value) {
        String source = value == null ? "" : value.toUpperCase();

        if (source.contains("UPI")) return "UPI";
        if (source.contains("CREDIT")) return "CREDITCARD";
        if (source.contains("DEBIT")) return "DEBITCARD";
        if (source.contains("WALLET") || source.contains("PAYTM") || source.contains("PHONEPE")) return "WALLET";
        if (source.contains("CASH")) return "CASH";
        if (source.contains("CHEQUE") || source.contains("CHECK")) return "CHEQUE";
        if (source.contains("BANK") || source.contains("TRANSFER") || source.contains("IMPS") || source.contains("NEFT") || source.contains("RTGS")) return "BANK_TRANSFER";

        return "WALLET";
    }

    private String normalizeStatus(String value) {
        String source = value == null ? "" : value.trim().toUpperCase();

        if (source.contains("PENDING") || source.contains("PROCESS")) return "PENDING";
        if (source.contains("FAIL") || source.contains("ERROR") || source.contains("REJECT")) return "FAILED";
        if (source.contains("CANCEL") || source.contains("REVERSE")) return "CANCELLED";
        if (source.contains("COMPLETE")) return "COMPLETED";

        return "SUCCESS";
    }

    private String findStatusToken(String[] row) {
        if (row == null) {
            return "";
        }

        for (String cell : row) {
            String normalized = cell == null ? "" : cell.trim().toUpperCase();
            if (normalized.equals("SUCCESS") || normalized.equals("FAILED") || normalized.equals("PENDING")
                    || normalized.equals("CANCELLED") || normalized.equals("COMPLETED")
                    || normalized.equals("PROCESSING") || normalized.equals("ERROR")) {
                return normalized;
            }
        }

        return "";
    }

    private String getDateCellValue(Cell cell) {
        if (cell == null)
            return "";

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return new SimpleDateFormat("dd/MM/yyyy").format(date);
            }
            return normalizeDateString(getCellValueAsString(cell));
        } catch (Exception e) {
            return normalizeDateString(getCellValueAsString(cell));
        }
    }

    private String normalizeDateString(String rawValue) {
        if (rawValue == null) {
            return "";
        }

        String value = rawValue.trim();
        if (value.isEmpty()) {
            return "";
        }

        DateTimeFormatter output = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter[] inputFormatters = {
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("dd.M.yyyy"),
                DateTimeFormatter.ofPattern("d/M/yyyy")
        };

        for (DateTimeFormatter formatter : inputFormatters) {
            try {
                LocalDate parsed = LocalDate.parse(value, formatter);
                return parsed.format(output);
            } catch (Exception ignored) {
                // try next format
            }
        }

        return value;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null)
            return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    return new SimpleDateFormat("dd/MM/yyyy").format(date);
                }
                double numValue = cell.getNumericCellValue();
                if (numValue == Math.floor(numValue)) {
                    return String.valueOf((long) numValue);
                }
                return String.format("%.2f", numValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getStringCellValue();
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    private Double parseDoubleSafe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            String cleanValue = value.replaceAll("[^0-9.\\-]", "").trim();
            if (cleanValue.isEmpty() || "-".equals(cleanValue) || ".".equals(cleanValue) || "-.".equals(cleanValue)) {
                return 0.0;
            }
            return Double.parseDouble(cleanValue);
        } catch (Exception e) {
            return 0.0;
        }
    }
}

