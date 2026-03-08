package com.employee.loan_system.repository;

import com.employee.loan_system.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e WHERE " +
            "(e.fname IS NOT NULL AND LOWER(e.fname) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "(e.lname IS NOT NULL AND LOWER(e.lname) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "(e.fullname IS NOT NULL AND LOWER(e.fullname) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "(e.empCode IS NOT NULL AND LOWER(e.empCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "(e.dept IS NOT NULL AND LOWER(e.dept) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Employee> searchEmployees(@Param("searchTerm") String searchTerm);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.empCode) LIKE LOWER(CONCAT(:filterType, '%'))")
    List<Employee> findByEmpCodeStartingWith(@Param("filterType") String filterType);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.empCode) LIKE LOWER(CONCAT('%', :filterType))")
    List<Employee> findByEmpCodeEndingWith(@Param("filterType") String filterType);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.empCode) LIKE LOWER(CONCAT('%', :filterType, '%'))")
    List<Employee> findByEmpCodeContaining(@Param("filterType") String filterType);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.empCode) NOT LIKE LOWER(CONCAT('%', :filterType, '%'))")
    List<Employee> findByEmpCodeNotContaining(@Param("filterType") String filterType);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.empCode) = LOWER(:filterType)")
    List<Employee> findByEmpCodeEquals(@Param("filterType") String filterType);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.empCode) != LOWER(:filterType)")
    List<Employee> findByEmpCodeNotEquals(@Param("filterType") String filterType);
}
