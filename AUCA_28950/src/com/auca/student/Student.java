package com.auca.student;

import com.auca.abstracts.AcademicMember;
import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Registrable;
import java.time.LocalDate;

public class Student extends AcademicMember {

    private String studentNumber;
    private String programName;
    private LocalDate admissionDate;
    private int studyYear;
    private String guardianPhone;
    private boolean scholarshipStatus;

    public Student(String personId, String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth, String memberNumber, Department department, LocalDate joinDate, String academicStatus, String institutionalEmail, String advisorName, String studentNumber, String programName, LocalDate admissionDate, int studyYear, String guardianPhone, boolean scholarshipStatus) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth, memberNumber, department, joinDate, academicStatus, institutionalEmail, advisorName);
        this.studentNumber = studentNumber;
        this.programName = programName;
        this.admissionDate = admissionDate;
        this.studyYear = studyYear;
        this.guardianPhone = guardianPhone;
        this.scholarshipStatus = scholarshipStatus;
    }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }
    public LocalDate getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(LocalDate admissionDate) { this.admissionDate = admissionDate; }
    public int getStudyYear() { return studyYear; }
    public void setStudyYear(int studyYear) { this.studyYear = studyYear; }
    public String getGuardianPhone() { return guardianPhone; }
    public void setGuardianPhone(String guardianPhone) { this.guardianPhone = guardianPhone; }
    public boolean isScholarshipStatus() { return scholarshipStatus; }
    public void setScholarshipStatus(boolean scholarshipStatus) { this.scholarshipStatus = scholarshipStatus; }

    @Override
    public void updateFrom(Registrable source) throws ValidationException {
        if (!(source instanceof Student)) throw new ValidationException("Record type mismatch: expected Student.");
        Student other = (Student) source;
        super.updateFrom(source);
        this.studentNumber = other.studentNumber;
        this.programName = other.programName;
        this.admissionDate = other.admissionDate;
        this.studyYear = other.studyYear;
        this.guardianPhone = other.guardianPhone;
        this.scholarshipStatus = other.scholarshipStatus;
    }

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("  Student number: " + (studentNumber == null ? "(none)" : studentNumber));
        System.out.println("  Program name: " + (programName == null ? "(none)" : programName));
        System.out.println("  Admission date: " + (admissionDate == null ? "(none)" : admissionDate));
        System.out.println("  Study year: " + studyYear);
        System.out.println("  Guardian phone: " + (guardianPhone == null ? "(none)" : guardianPhone));
        System.out.println("  Scholarship status: " + scholarshipStatus);
    }


    @Override public String getRoleDescription() { return "Student of " + programName + " (year " + studyYear + ")"; }
    @Override public String getAcademicCategory() { return "STUDENT"; }
    @Override public String getDisplayName() { return super.getDisplayName() + " (" + studentNumber + ")"; }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Registrable.id(studentNumber, "Student number");
        Registrable.text(programName, "Program name", 2, 120);
        Registrable.notFuture(admissionDate, "Admission date");
        Registrable.range(studyYear, "Study year", 1, 6);
        Registrable.phone(guardianPhone, "Guardian phone");
    }
}
