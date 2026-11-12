package com.bits.sreg.student.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentRequest(
        @NotBlank(message = "name is required")
        @Size(max = 100, message = "name must be at most 100 characters")
        String name,

        @NotBlank(message = "email is required")
        @Email(message = "email is not valid")
        @Size(max = 150, message = "email must be at most 150 characters")
        String email,

        @NotBlank(message = "department is required")
        @Size(max = 100, message = "department must be at most 100 characters")
        String department) {
}
