package com.bits.sreg.student.service;

import com.bits.sreg.student.dto.StudentRequest;
import com.bits.sreg.student.dto.StudentResponse;
import com.bits.sreg.student.entity.Student;
import com.bits.sreg.student.exception.ConflictException;
import com.bits.sreg.student.exception.ResourceNotFoundException;
import com.bits.sreg.student.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    // ---------------- Commands ----------------

    @Transactional
    public StudentResponse create(StudentRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new ConflictException("A student with email " + request.email() + " already exists");
        }
        Student saved = repository.save(new Student(request.name(), request.email(), request.department()));
        log.info("Created student id={}", saved.getId());
        return StudentResponse.from(saved);
    }

    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = findOrThrow(id);
        repository.findByEmail(request.email()).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new ConflictException("Email " + request.email() + " is already used by another student");
            }
        });
        student.setName(request.name());
        student.setEmail(request.email());
        student.setDepartment(request.department());
        Student saved = repository.save(student);
        log.info("Updated student id={}", id);
        return StudentResponse.from(saved);
    }

    @Transactional
    public void delete(Long id) {
        Student student = findOrThrow(id);
        repository.delete(student);
        log.info("Deleted student id={}", id);
    }

    // ---------------- Queries ----------------

    @Transactional(readOnly = true)
    public StudentResponse getById(Long id) {
        return StudentResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAll() {
        return repository.findAll().stream().map(StudentResponse::from).toList();
    }

    private Student findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));
    }
}
