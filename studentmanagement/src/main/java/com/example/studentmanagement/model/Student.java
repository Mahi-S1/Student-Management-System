
package com.example.studentmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

    // Attributes
    @Id
    private int studentId;

    @NotBlank(message = "Student name is required")
    private String studentName;

    @Email(message = "Enter a valid email")
    private String email;

    private String courseName;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private double programmingScore;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private double mathematicsScore;

    @ElementCollection
    @CollectionTable(
            name = "student_skills",
            joinColumns = @JoinColumn(name = "student_id")
    )
    @Column(name = "skill")
    private List<String> technicalSkills = new ArrayList<>();

    // Default constructor (required by JPA)
    public Student() {
    }

    // Parameterized constructor
    public Student(int studentId, String studentName,
                   String email, String courseName,
                   double programmingScore,
                   double mathematicsScore,
                   List<String> technicalSkills) {

        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.courseName = courseName;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;
        this.technicalSkills = new ArrayList<>(technicalSkills);
    }

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public double getProgrammingScore() {
        return programmingScore;
    }

    public void setProgrammingScore(double programmingScore) {
        this.programmingScore = programmingScore;
    }

    public double getMathematicsScore() {
        return mathematicsScore;
    }

    public void setMathematicsScore(double mathematicsScore) {
        this.mathematicsScore = mathematicsScore;
    }

    public List<String> getTechnicalSkills() {
        return new ArrayList<>(technicalSkills);
    }

    public void setTechnicalSkills(List<String> technicalSkills) {
        this.technicalSkills = new ArrayList<>(technicalSkills);
    }

    // Calculate average score
    public double calculateAverage() {
        return (programmingScore + mathematicsScore) / 2.0;
    }

    // toString method
    @Override
    public String toString() {
        return "Student ID: " + studentId +
                "\nName: " + studentName +
                "\nEmail: " + email +
                "\nCourse: " + courseName +
                "\nProgramming Score: " + programmingScore +
                "\nMathematics Score: " + mathematicsScore +
                "\nTechnical Skills: " + technicalSkills;
    }

    // equals method
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        Student s = (Student) obj;
        return studentId == s.studentId;
    }

    // hashCode method
    @Override
    public int hashCode() {
        return Integer.hashCode(studentId);
    }
}

