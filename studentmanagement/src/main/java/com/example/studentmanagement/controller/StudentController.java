
package com.example.studentmanagement.controller;

import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.repository.StudentRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Comparator;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // 1. Add a new student
    @PostMapping
    public ResponseEntity<Student> addStudent(
            @RequestBody Student student) {

        if (studentRepository.existsById(student.getStudentId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Student savedStudent = studentRepository.save(student);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedStudent);
    }

    // 2. Retrieve all students
    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // 3. Search student by ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(
            @PathVariable int id) {

        return studentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. Update student scores and technical skills
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable int id,
            @RequestBody Student updatedStudent) {

        return studentRepository.findById(id).map(student -> {

            student.setProgrammingScore(
                    updatedStudent.getProgrammingScore());

            student.setMathematicsScore(
                    updatedStudent.getMathematicsScore());

            student.setTechnicalSkills(
                    updatedStudent.getTechnicalSkills());

            return ResponseEntity.ok(
                    studentRepository.save(student));

        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. Delete student
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable int id) {

        if (!studentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        studentRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // 6. Display merit list
    @GetMapping("/merit-list")
    public List<Student> getMeritList() {

        return studentRepository.findAll()
                .stream()
                .sorted(Comparator.comparingDouble(
                        Student::calculateAverage).reversed())
                .toList();
    }
}

