package com.telelink.employeemanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required!")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters!")
    private String firstName;

    @NotBlank(message = "Last name is required!")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters!")
    private String lastName;

    @NotBlank(message = "Email is required!")
    @Email(message = "Please provide valid email!")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Department is required!")
    private String department;

    @NotNull(message = "Salary is required!")
    @Min(value = 1000, message = "Salary must be at least 1000!")
    @Max(value = 1000000, message = "Salary cannot exceed 1000000!")
    private Double salary;

    @NotBlank(message = "Position is required!")
    private String position;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}