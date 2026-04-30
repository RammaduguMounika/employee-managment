package com.telelink.employeemanagement.service;

import com.telelink.employeemanagement.entity.Employee;
import com.telelink.employeemanagement.exception.EmployeeNotFoundException;
import com.telelink.employeemanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // Get all employees
    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        log.info("Fetching all employees");
        return employeeRepository.findAll();
    }

    // Get employee by ID
    @Transactional(readOnly = true)
    public Employee getEmployeeById(Long id) {
        log.info("Fetching employee with id: {}", id);
        return employeeRepository.findById(id)
                .orElseThrow(() ->
                    new EmployeeNotFoundException(id));
    }

    // Create new employee
    @Transactional
    public Employee createEmployee(Employee employee) {
        log.info("Creating new employee: {}", employee.getEmail());

        // check if email already exists!
        if (employeeRepository.findByEmail(
                employee.getEmail()).isPresent()) {
            throw new RuntimeException(
                "Employee already exists with email: "
                + employee.getEmail());
        }

        return employeeRepository.save(employee);
    }

    // Update employee
    @Transactional
    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        log.info("Updating employee with id: {}", id);

        // check if employee exists first!
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() ->
                    new EmployeeNotFoundException(id));

        // update fields
        existing.setFirstName(updatedEmployee.getFirstName());
        existing.setLastName(updatedEmployee.getLastName());
        existing.setDepartment(updatedEmployee.getDepartment());
        existing.setSalary(updatedEmployee.getSalary());
        existing.setPosition(updatedEmployee.getPosition());

        return employeeRepository.save(existing);
    }

    // Delete employee
    @Transactional
    public void deleteEmployee(Long id) {
        log.info("Deleting employee with id: {}", id);

        // check if exists first!
        if (!employeeRepository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }

        employeeRepository.deleteById(id);
    }

    // Get by department
    @Transactional(readOnly = true)
    public List<Employee> getByDepartment(String department) {
        log.info("Fetching employees by department: {}", department);
        return employeeRepository.findByDepartment(department);
    }

    // Get high salary employees
    @Transactional(readOnly = true)
    public List<Employee> getHighSalaryEmployees(Double salary) {
        log.info("Fetching employees with salary > {}", salary);
        return employeeRepository.findBySalaryGreaterThan(salary);
    }
}