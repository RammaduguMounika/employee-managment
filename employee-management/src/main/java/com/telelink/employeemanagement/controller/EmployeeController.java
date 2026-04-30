package com.telelink.employeemanagement.controller;

import com.telelink.employeemanagement.entity.Employee;
import com.telelink.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    // GET all employees
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("GET /api/employees");
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // GET employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(
                                @PathVariable Long id) {
        log.info("GET /api/employees/{}", id);
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    // POST create employee
    @PostMapping
    public ResponseEntity<Employee> createEmployee(
                                @Valid @RequestBody Employee employee) {
        log.info("POST /api/employees");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employeeService.createEmployee(employee));
    }

    // PUT update employee
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
                                @PathVariable Long id,
                                @Valid @RequestBody Employee employee) {
        log.info("PUT /api/employees/{}", id);
        return ResponseEntity.ok(
                employeeService.updateEmployee(id, employee));
    }

    // DELETE employee
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(
                                @PathVariable Long id) {
        log.info("DELETE /api/employees/{}", id);
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(
                "Employee deleted successfully!");
    }

    // GET by department
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Employee>> getByDepartment(
                                @PathVariable String department) {
        log.info("GET /api/employees/department/{}", department);
        return ResponseEntity.ok(
                employeeService.getByDepartment(department));
    }

    // GET high salary employees
    @GetMapping("/salary/{amount}")
    public ResponseEntity<List<Employee>> getHighSalaryEmployees(
                                @PathVariable Double amount) {
        log.info("GET /api/employees/salary/{}", amount);
        return ResponseEntity.ok(
                employeeService.getHighSalaryEmployees(amount));
    }
}