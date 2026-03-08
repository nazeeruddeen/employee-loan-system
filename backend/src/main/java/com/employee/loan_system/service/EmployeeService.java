package com.employee.loan_system.service;

import com.employee.loan_system.dto.EmployeeDTO;
import com.employee.loan_system.entity.Employee;
import com.employee.loan_system.repository.EmployeeRepository;
import com.employee.loan_system.util.ExportFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ExportFactory exportFactory;

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EmployeeDTO saveEmployee(EmployeeDTO dto) {
        Employee employee = convertToEntity(dto);
        Employee saved = employeeRepository.save(employee);
        return convertToDTO(saved);
    }

    public EmployeeDTO updateEmployee(EmployeeDTO dto) {
        Employee existing = employeeRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + dto.getId()));

        Employee employee = convertToEntity(dto);
        employee.setCreatedAt(existing.getCreatedAt());
        Employee updated = employeeRepository.save(employee);
        return convertToDTO(updated);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return convertToDTO(employee);
    }

    public List<EmployeeDTO> searchEmployees(String searchTerm) {
        return employeeRepository.searchEmployees(searchTerm).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EmployeeDTO> sortEmployees(String property, String orderType) {
        List<Employee> employees = employeeRepository.findAll();

        employees.sort((e1, e2) -> {
            int comparison = 0;
            switch (property.toLowerCase()) {
                case "id":
                    comparison = e1.getId().compareTo(e2.getId());
                    break;
                case "fname":
                    comparison = compareStrings(e1.getFname(), e2.getFname());
                    break;
                case "fullname":
                    comparison = compareStrings(e1.getFullname(), e2.getFullname());
                    break;
                case "dept":
                    comparison = compareStrings(e1.getDept(), e2.getDept());
                    break;
                case "salary":
                    comparison = compareDoubles(e1.getSalary(), e2.getSalary());
                    break;
                case "age":
                    comparison = compareIntegers(e1.getAge(), e2.getAge());
                    break;
                default:
                    comparison = e1.getId().compareTo(e2.getId());
            }
            return "DESC".equalsIgnoreCase(orderType) ? -comparison : comparison;
        });

        return employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private int compareStrings(String s1, String s2) {
        if (s1 == null && s2 == null)
            return 0;
        if (s1 == null)
            return -1;
        if (s2 == null)
            return 1;
        return s1.compareToIgnoreCase(s2);
    }

    private int compareDoubles(Double d1, Double d2) {
        if (d1 == null && d2 == null)
            return 0;
        if (d1 == null)
            return -1;
        if (d2 == null)
            return 1;
        return d1.compareTo(d2);
    }

    private int compareIntegers(Integer i1, Integer i2) {
        if (i1 == null && i2 == null)
            return 0;
        if (i1 == null)
            return -1;
        if (i2 == null)
            return 1;
        return i1.compareTo(i2);
    }

    public List<EmployeeDTO> filterEmployees(String filterType, String empCode) {
        List<Employee> employees;
        String normalizedFilter = filterType.toLowerCase().trim();

        switch (normalizedFilter) {
            case "startswith":
                employees = employeeRepository.findByEmpCodeStartingWith(empCode);
                break;
            case "endswith":
                employees = employeeRepository.findByEmpCodeEndingWith(empCode);
                break;
            case "contains":
                employees = employeeRepository.findByEmpCodeContaining(empCode);
                break;
            case "notcontains":
                employees = employeeRepository.findByEmpCodeNotContaining(empCode);
                break;
            case "equals":
                employees = employeeRepository.findByEmpCodeEquals(empCode);
                break;
            case "notequals":
                employees = employeeRepository.findByEmpCodeNotEquals(empCode);
                break;
            default:
                employees = employeeRepository.findAll();
        }

        return employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public byte[] exportEmployees(String type) {
        List<EmployeeDTO> employees = getAllEmployees();
        return exportFactory.exportData(employees, type);
    }

    private EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setEmpCode(employee.getEmpCode());
        dto.setFname(employee.getFname());
        dto.setLname(employee.getLname());
        dto.setFullname(employee.getFullname());
        dto.setDept(employee.getDept());
        dto.setAge(employee.getAge());
        dto.setSalary(employee.getSalary());
        return dto;
    }

    private Employee convertToEntity(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setId(dto.getId());
        employee.setEmpCode(dto.getEmpCode());
        employee.setFname(dto.getFname());
        employee.setLname(dto.getLname());
        employee.setDept(dto.getDept());
        employee.setAge(dto.getAge());
        employee.setSalary(dto.getSalary());
        return employee;
    }
}
