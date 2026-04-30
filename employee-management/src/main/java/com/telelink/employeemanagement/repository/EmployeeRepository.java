package com.telelink.employeemanagement.repository;

import com.telelink.employeemanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository 
                extends JpaRepository<Employee, Long> {

    // find by department
    List<Employee> findByDepartment(String department);

    // find by email
    Optional<Employee> findByEmail(String email);

    // find by position
    List<Employee> findByPosition(String position);

    // find employees with salary greater than
    List<Employee> findBySalaryGreaterThan(Double salary);
}