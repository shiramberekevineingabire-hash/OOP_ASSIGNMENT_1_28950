package com.auca.enrollment;

import com.auca.course.Course;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;
import java.time.LocalDate;
import java.util.List;

public class Enrollment implements Identifiable, Registrable {

    private String enrollmentId;
    private Student student;
    private Course course;
    private LocalDate enrollmentDate;
    private String semester;
    private String enrollmentStatus;

    public Enrollment(String enrollmentId, Student student, Course course, LocalDate enrollmentDate, String semester, String enrollmentStatus) {
        this.enrollmentId = enrollmentId;
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
        this.semester = semester;
        this.enrollmentStatus = enrollmentStatus;
    }

    @Override public String getId() { return enrollmentId; }

    public String getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(String enrollmentId) { this.enrollmentId = enrollmentId; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public String getEnrollmentStatus() { return enrollmentStatus; }
    public void setEnrollmentStatus(String enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }

    @Override
    public void updateFrom(Registrable source) throws ValidationException {
        if (!(source instanceof Enrollment)) throw new ValidationException("Record type mismatch: expected Enrollment.");
        Enrollment other = (Enrollment) source;
        this.enrollmentId = other.enrollmentId;
        this.student = other.student;
        this.course = other.course;
        this.enrollmentDate = other.enrollmentDate;
        this.semester = other.semester;
        this.enrollmentStatus = other.enrollmentStatus;
    }

    @Override
    public void printDetails() {
        System.out.println("--- " + getClass().getSimpleName() + " (entity v" + ENTITY_VERSION + ") ---");
        System.out.println("  Enrollment id: " + (enrollmentId == null ? "(none)" : enrollmentId));
        System.out.println("  Student: " + (student == null ? "(none)" : "[" + student.getId() + "] " + student.getDisplayName()));
        System.out.println("  Course: " + (course == null ? "(none)" : "[" + course.getId() + "] " + course.getDisplayName()));
        System.out.println("  Enrollment date: " + (enrollmentDate == null ? "(none)" : enrollmentDate));
        System.out.println("  Semester: " + (semester == null ? "(none)" : semester));
        System.out.println("  Enrollment status: " + (enrollmentStatus == null ? "(none)" : enrollmentStatus));
    }

    @Override public String toString() { return getRegistrationSummary(); }


    @Override
    public String getDisplayName() {
        return (student == null ? "?" : student.getDisplayName()) + " in " + (course == null ? "?" : course.getCourseCode()) + " (" + semester + ")";
    }
    @Override public boolean isActive() { return "ACTIVE".equals(enrollmentStatus); }

    @Override
    public String getRegistrationSummary() {
        return "Enrollment [" + enrollmentId + "] " + getDisplayName() + " | " + enrollmentStatus;
    }

    @Override
    public void validate() throws ValidationException {
        Registrable.id(enrollmentId, "Enrollment ID");
        Registrable.required(student, "Student");
        Registrable.required(course, "Course");
        Registrable.notFuture(enrollmentDate, "Enrollment date");
        if (!List.of("FALL", "SPRING", "SUMMER").contains(semester))
            throw new ValidationException("Semester must be FALL, SPRING or SUMMER.");
        if (!List.of("ACTIVE", "COMPLETED", "DROPPED").contains(enrollmentStatus))
            throw new ValidationException("Enrollment status must be ACTIVE, COMPLETED or DROPPED.");
    }
}
