package com.bits.sreg.student.dto;

import com.bits.sreg.student.entity.Student;

import java.time.LocalDateTime;

public record StudentResponse(
        Long id,
        String name,
        String email,
        String department,
        LocalDateTime createdAt) {

    public static StudentResponse from(Student s) {
        return new StudentResponse(s.getId(), s.getName(), s.getEmail(),
                s.getDepartment(), s.getCreatedAt());
    }
}
