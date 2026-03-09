package com.employee.loan_system.service;

import com.employee.loan_system.dto.*;
import com.employee.loan_system.entity.*;
import com.employee.loan_system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {
    @Autowired
    private LoanApplicationRepository loanRepository;

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    @Autowired
    private CompanyAddressRepository companyAddressRepository;

    @Autowired
    private BusinessProductRepository businessProductRepository;

    @Autowired
    private PersonDetailsRepository personDetailsRepository;

    @Autowired
    private SalesReportRepository salesReportRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // Loan Application
    public LoanApplicationDTO applyLoan(LoanApplicationDTO dto) {
        LoanApplication loan = new LoanApplication();
        loan.setFname(dto.getFname());
        loan.setLname(dto.getLname());
        loan.setMailId(dto.getMailId());
        loan.setMobile(dto.getMobile());
        loan.setCity(dto.getCity());

        LoanApplication saved = loanRepository.save(loan);
        return convertLoanToDTO(saved);
    }

    public List<LoanApplicationDTO> getLoanTaskboardData() {
        return loanRepository.findAll().stream()
                .map(this::convertLoanToDTO)
                .collect(Collectors.toList());
    }

    // Overview
    public OverviewDTO getOverviewDetails(Long appId) {
        LoanApplication loan = loanRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("Loan application not found with id: " + appId));

        OverviewDTO dto = new OverviewDTO();
        dto.setAppId(loan.getAppId());
        dto.setMobile(loan.getMobile());
        dto.setMail(loan.getMailId());

        CompanyDetails company = companyDetailsRepository.findByLoanApplication_AppId(appId).orElse(null);
        if (company != null) {
            dto.setCompanyName(company.getCompanyName());
            dto.setCompanyPan(company.getCompanyPan());
        }

        BusinessProduct product = businessProductRepository.findByLoanApplication_AppId(appId).orElse(null);
        if (product != null) {
            dto.setTenure(product.getTenure());
            dto.setLoanAmt(product.getLoanAmount());
        }

        return dto;
    }

    // Company Details
    public CompanyDetailsDTO getCompanyDetails(Long appId) {
        CompanyDetails details = companyDetailsRepository.findByLoanApplication_AppId(appId)
                .orElse(new CompanyDetails());
        return convertCompanyDetailsToDTO(details);
    }

    @Transactional
    public CompanyDetailsDTO saveCompanyDetails(Long appId, CompanyDetailsDTO dto) {
        LoanApplication loan = loanRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("Loan application not found with id: " + appId));

        CompanyDetails details = companyDetailsRepository.findByLoanApplication_AppId(appId)
                .orElse(new CompanyDetails());

        details.setLoanApplication(loan);
        details.setCompanyName(dto.getCompanyName());
        details.setIndustryType(dto.getIndustryType());
        details.setDateOfEstablish(dto.getDateOfEstablish());
        details.setTurnover(dto.getTurnover());
        details.setCompanyPan(dto.getCompanyPan());
        details.setGstin(dto.getGstin());

        CompanyDetails saved = companyDetailsRepository.save(details);
        return convertCompanyDetailsToDTO(saved);
    }

    // Company Address
    public CompanyAddressDTO getCompanyAddress(Long appId) {
        CompanyAddress address = companyAddressRepository.findByLoanApplication_AppId(appId)
                .orElse(new CompanyAddress());
        return convertCompanyAddressToDTO(address);
    }

    @Transactional
    public CompanyAddressDTO saveCompanyAddress(Long appId, CompanyAddressDTO dto) {
        LoanApplication loan = loanRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("Loan application not found with id: " + appId));

        CompanyAddress address = companyAddressRepository.findByLoanApplication_AppId(appId)
                .orElse(new CompanyAddress());

        address.setLoanApplication(loan);
        address.setFlatnum(dto.getFlatnum());
        address.setBuilding(dto.getBuilding());
        address.setLine(dto.getLine());
        address.setArea(dto.getArea());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setLandmark(dto.getLandmark());
        address.setPincode(dto.getPincode());

        CompanyAddress saved = companyAddressRepository.save(address);
        return convertCompanyAddressToDTO(saved);
    }

    // Business Product
    public BusinessProductDTO getProductDetails(Long appId) {
        BusinessProduct product = businessProductRepository.findByLoanApplication_AppId(appId)
                .orElse(new BusinessProduct());
        return convertBusinessProductToDTO(product);
    }

    @Transactional
    public BusinessProductDTO saveProductDetails(Long appId, BusinessProductDTO dto) {
        LoanApplication loan = loanRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("Loan application not found with id: " + appId));

        BusinessProduct product = businessProductRepository.findByLoanApplication_AppId(appId)
                .orElse(new BusinessProduct());

        product.setLoanApplication(loan);
        product.setPurposeOfLoan(dto.getPurposeOfLoan());
        product.setNatureOfBusiness(dto.getNatureOfBusiness());
        product.setLoanAmount(dto.getLoanAmount());
        product.setTenure(dto.getTenure());

        BusinessProduct saved = businessProductRepository.save(product);
        return convertBusinessProductToDTO(saved);
    }

    // Person Details (Assurance)
    public List<PersonDetailsDTO> getPersonDetails(Long appId) {
        return personDetailsRepository.findByLoanApplication_AppId(appId).stream()
                .map(this::convertPersonDetailsToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PersonDetailsDTO> savePersonDetails(Long appId, List<PersonDetailsDTO> dtos) {
        LoanApplication loan = loanRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("Loan application not found with id: " + appId));

        // Validate input data
        if (dtos == null || dtos.isEmpty()) {
            throw new IllegalArgumentException("Person details list cannot be null or empty");
        }

        // Delete existing records
        List<PersonDetails> existing = personDetailsRepository.findByLoanApplication_AppId(appId);
        personDetailsRepository.deleteAll(existing);

        // Validate and save new records
        List<PersonDetails> entities = dtos.stream().map(dto -> {
            // Manual validation for better error messages
            validatePersonDetailsDTO(dto);
            
            PersonDetails entity = new PersonDetails();
            entity.setLoanApplication(loan);
            entity.setEname(dto.getEname());
            entity.setNationality(dto.getNationality());
            entity.setAge(dto.getAge());
            entity.setMail(dto.getMail());
            entity.setGender(dto.getGender());
            return entity;
        }).collect(Collectors.toList());

        List<PersonDetails> saved = personDetailsRepository.saveAll(entities);
        return saved.stream()
                .map(this::convertPersonDetailsToDTO)
                .collect(Collectors.toList());
    }

    private void validatePersonDetailsDTO(PersonDetailsDTO dto) {
        if (dto.getEname() == null || dto.getEname().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name is required");
        }
        if (dto.getEname().length() > 100) {
            throw new IllegalArgumentException("Employee name must not exceed 100 characters");
        }
        if (dto.getNationality() != null && dto.getNationality().length() > 50) {
            throw new IllegalArgumentException("Nationality must not exceed 50 characters");
        }
        if (dto.getAge() != null && (dto.getAge() < 18 || dto.getAge() > 100)) {
            throw new IllegalArgumentException("Age must be between 18 and 100");
        }
        if (dto.getMail() != null && !dto.getMail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Please provide a valid email address");
        }
        if (dto.getGender() != null && !dto.getGender().matches("^(male|female|others)$")) {
            throw new IllegalArgumentException("Gender must be 'male', 'female', or 'others'");
        }
    }

    // Sales Report
    public List<SalesReportDTO> getSalesReportDetails(Long appId) {
        return salesReportRepository.findByLoanApplication_AppId(appId).stream()
                .map(this::convertSalesReportToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<SalesReportDTO> saveSalesReport(Long appId, List<SalesReportDTO> dtos) {
        LoanApplication loan = loanRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("Loan application not found with id: " + appId));

        // Validate input data
        if (dtos == null || dtos.isEmpty()) {
            throw new IllegalArgumentException("Sales report list cannot be null or empty");
        }

        // Delete existing records
        List<SalesReport> existing = salesReportRepository.findByLoanApplication_AppId(appId);
        salesReportRepository.deleteAll(existing);

        // Validate and save new records
        List<SalesReport> entities = dtos.stream().map(dto -> {
            // Manual validation for better error messages
            validateSalesReportDTO(dto);
            
            SalesReport entity = new SalesReport();
            entity.setLoanApplication(loan);
            entity.setDate(dto.getDate());
            entity.setOrderno(dto.getOrderno());
            entity.setInvoiceno(dto.getInvoiceno());
            entity.setPartyName(dto.getPartyName());
            entity.setPartyPhoneNum(dto.getPartyPhoneNum());
            entity.setTotalAmount(dto.getTotalAmount());
            entity.setRecievedOrPaidAmount(dto.getRecievedOrPaidAmount());
            entity.setBalanceAmount(dto.getBalanceAmount());
            return entity;
        }).collect(Collectors.toList());

        List<SalesReport> saved = salesReportRepository.saveAll(entities);
        return saved.stream()
                .map(this::convertSalesReportToDTO)
                .collect(Collectors.toList());
    }

    private void validateSalesReportDTO(SalesReportDTO dto) {
        if (dto.getDate() == null || dto.getDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Date is required");
        }
        if (!dto.getDate().matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            throw new IllegalArgumentException("Date must be in DD/MM/YYYY format");
        }
        if (dto.getOrderno() == null || dto.getOrderno().trim().isEmpty()) {
            throw new IllegalArgumentException("Order number is required");
        }
        if (dto.getOrderno().length() > 50) {
            throw new IllegalArgumentException("Order number must not exceed 50 characters");
        }
        if (dto.getInvoiceno() == null || dto.getInvoiceno().trim().isEmpty()) {
            throw new IllegalArgumentException("Invoice number is required");
        }
        if (dto.getInvoiceno().length() > 50) {
            throw new IllegalArgumentException("Invoice number must not exceed 50 characters");
        }
        if (dto.getPartyName() == null || dto.getPartyName().trim().isEmpty()) {
            throw new IllegalArgumentException("Party name is required");
        }
        if (dto.getPartyName().length() > 100) {
            throw new IllegalArgumentException("Party name must not exceed 100 characters");
        }
        if (dto.getPartyPhoneNum() != null && !dto.getPartyPhoneNum().matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException("Party phone number must be exactly 10 digits");
        }
        if (dto.getTotalAmount() == null || dto.getTotalAmount() <= 0) {
            throw new IllegalArgumentException("Total amount must be greater than 0");
        }
        if (dto.getRecievedOrPaidAmount() != null && dto.getRecievedOrPaidAmount() < 0) {
            throw new IllegalArgumentException("Received/Paid amount must be non-negative");
        }
        if (dto.getBalanceAmount() != null && dto.getBalanceAmount() < 0) {
            throw new IllegalArgumentException("Balance amount must be non-negative");
        }
    }

    // Transactions
    public List<TransactionDTO> getTransactions(Long appId, String duration, String startDate, String endDate) {
        if (duration != null && !duration.isEmpty()) {
            LocalDate end = LocalDate.now();
            LocalDate start;

            switch (duration.toLowerCase()) {
                case "last month":
                    start = end.minusMonths(1);
                    break;
                case "last 3 months":
                    start = end.minusMonths(3);
                    break;
                case "last 6 months":
                    start = end.minusMonths(6);
                    break;
                case "last year":
                    start = end.minusYears(1);
                    break;
                default:
                    start = end.minusMonths(1);
            }

            return transactionRepository.findByAppIdAndDateRange(appId, start.atStartOfDay(), end.atTime(LocalTime.MAX)).stream()
                    .map(this::convertTransactionToDTO)
                    .collect(Collectors.toList());
        } else if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate sDate = LocalDate.parse(startDate, formatter);
            LocalDate eDate = LocalDate.parse(endDate, formatter);

            return transactionRepository.findByAppIdAndDateRange(appId, sDate.atStartOfDay(), eDate.atTime(LocalTime.MAX)).stream()
                    .map(this::convertTransactionToDTO)
                    .collect(Collectors.toList());
        }

        return transactionRepository.findByLoanApplication_AppId(appId).stream()
                .map(this::convertTransactionToDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> filterTransactions(Long appId, String statusOrInstrument, List<String> types) {
        List<Transaction> transactions;

        if ("status".equalsIgnoreCase(statusOrInstrument)) {
            transactions = transactionRepository.findByAppIdAndStatusIn(appId, types);
        } else {
            transactions = transactionRepository.findByAppIdAndInstrumentIn(appId, types);
        }

        return transactions.stream()
                .map(this::convertTransactionToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<TransactionDTO> saveTransactions(Long appId, List<TransactionDTO> dtos) {
        LoanApplication loan = loanRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("Loan application not found with id: " + appId));

        // Delete existing records
        List<Transaction> existing = transactionRepository.findByLoanApplication_AppId(appId);
        transactionRepository.deleteAll(existing);

        // Save new records
        List<Transaction> entities = dtos.stream().map(dto -> {
            Transaction entity = new Transaction();
            entity.setLoanApplication(loan);
            entity.setTransactionDate(dto.getTransactionDate());
            entity.setActivity(dto.getActivity());
            entity.setTxnId(dto.getTxnId());
            entity.setComment(dto.getComment());
            entity.setDebtAmt(dto.getDebtAmt());
            entity.setCreditAmt(dto.getCreditAmt());
            entity.setTransactionBreakup(dto.getTransactionBreakup());
            entity.setTransactionStatus(dto.getTransactionStatus());
            entity.setInstrument(dto.getInstrument());
            return entity;
        }).collect(Collectors.toList());

        List<Transaction> saved = transactionRepository.saveAll(entities);
        return saved.stream()
                .map(this::convertTransactionToDTO)
                .collect(Collectors.toList());
    }

    // Converter methods
    private LoanApplicationDTO convertLoanToDTO(LoanApplication loan) {
        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setAppId(loan.getAppId());
        dto.setFname(loan.getFname());
        dto.setLname(loan.getLname());
        dto.setCustomerName(loan.getCustomerName());
        dto.setMailId(loan.getMailId());
        dto.setMobile(loan.getMobile());
        dto.setCity(loan.getCity());
        dto.setStatus(loan.getStatus());
        return dto;
    }

    private CompanyDetailsDTO convertCompanyDetailsToDTO(CompanyDetails details) {
        CompanyDetailsDTO dto = new CompanyDetailsDTO();
        dto.setCompanyName(details.getCompanyName());
        dto.setIndustryType(details.getIndustryType());
        dto.setDateOfEstablish(details.getDateOfEstablish());
        dto.setTurnover(details.getTurnover());
        dto.setCompanyPan(details.getCompanyPan());
        dto.setGstin(details.getGstin());
        return dto;
    }

    private CompanyAddressDTO convertCompanyAddressToDTO(CompanyAddress address) {
        CompanyAddressDTO dto = new CompanyAddressDTO();
        dto.setFlatnum(address.getFlatnum());
        dto.setBuilding(address.getBuilding());
        dto.setLine(address.getLine());
        dto.setArea(address.getArea());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setLandmark(address.getLandmark());
        dto.setPincode(address.getPincode());
        return dto;
    }

    private BusinessProductDTO convertBusinessProductToDTO(BusinessProduct product) {
        BusinessProductDTO dto = new BusinessProductDTO();
        dto.setPurposeOfLoan(product.getPurposeOfLoan());
        dto.setNatureOfBusiness(product.getNatureOfBusiness());
        dto.setLoanAmount(product.getLoanAmount());
        dto.setTenure(product.getTenure());
        return dto;
    }

    private PersonDetailsDTO convertPersonDetailsToDTO(PersonDetails person) {
        PersonDetailsDTO dto = new PersonDetailsDTO();
        dto.setId(person.getId());
        dto.setEname(person.getEname());
        dto.setNationality(person.getNationality());
        dto.setAge(person.getAge());
        dto.setMail(person.getMail());
        dto.setGender(person.getGender());
        return dto;
    }

    private SalesReportDTO convertSalesReportToDTO(SalesReport report) {
        SalesReportDTO dto = new SalesReportDTO();
        dto.setDate(report.getDate());
        dto.setOrderno(report.getOrderno());
        dto.setInvoiceno(report.getInvoiceno());
        dto.setPartyName(report.getPartyName());
        dto.setPartyPhoneNum(report.getPartyPhoneNum());
        dto.setTotalAmount(report.getTotalAmount());
        dto.setRecievedOrPaidAmount(report.getRecievedOrPaidAmount());
        dto.setBalanceAmount(report.getBalanceAmount());
        return dto;
    }

    private TransactionDTO convertTransactionToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setAppid(transaction.getLoanApplication().getAppId());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setActivity(transaction.getActivity());
        dto.setTxnId(transaction.getTxnId());
        dto.setComment(transaction.getComment());
        dto.setDebtAmt(transaction.getDebtAmt());
        dto.setCreditAmt(transaction.getCreditAmt());
        dto.setTransactionBreakup(transaction.getTransactionBreakup());
        dto.setTransactionStatus(transaction.getTransactionStatus());
        dto.setInstrument(transaction.getInstrument());
        return dto;
    }

    public TransactionDTO updateTransaction(TransactionDTO dto) {
        // Find the existing transaction
        Transaction existingTransaction = transactionRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + dto.getId()));
        
        // Update fields
        existingTransaction.setTransactionDate(dto.getTransactionDate());
        existingTransaction.setActivity(dto.getActivity());
        existingTransaction.setTxnId(dto.getTxnId());
        existingTransaction.setComment(dto.getComment());
        existingTransaction.setDebtAmt(dto.getDebtAmt());
        existingTransaction.setCreditAmt(dto.getCreditAmt());
        existingTransaction.setTransactionBreakup(dto.getTransactionBreakup());
        existingTransaction.setTransactionStatus(dto.getTransactionStatus());
        existingTransaction.setInstrument(dto.getInstrument());
        
        // Save the updated transaction
        Transaction updatedTransaction = transactionRepository.save(existingTransaction);
        
        // Convert to DTO and return
        return convertTransactionToDTO(updatedTransaction);
    }
}

