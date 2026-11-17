package com.auca.teacher;

import com.auca.abstracts.AcademicMember;
import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Registrable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Teacher extends AcademicMember {

    private String employeeNumber;
    private String specialization;
    private LocalDate hireDate;
    private BigDecimal salary;
    private String officeNumber;
    private String employmentType;

    public Teacher(String personId, String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth, String memberNumber, Department department, LocalDate joinDate, String academicStatus, String institutionalEmail, String advisorName, String employeeNumber, String specialization, LocalDate hireDate, BigDecimal salary, String officeNumber, String employmentType) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth, memberNumber, department, joinDate, academicStatus, institutionalEmail, advisorName);
        this.employeeNumber = employeeNumber;
        this.specialization = specialization;
        this.hireDate = hireDate;
        this.salary = salary;
        this.officeNumber = officeNumber;
        this.employmentType = employmentType;
    }

    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public String getOfficeNumber() { return officeNumber; }
    public void setOfficeNumber(String officeNumber) { this.officeNumber = officeNumber; }
    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    @Override
    public void updateFrom(Registrable source) throws ValidationException {
        if (!(source instanceof Teacher)) throw new ValidationException("Record type mismatch: expected Teacher.");
        Teacher other = (Teacher) source;
        super.updateFrom(source);
        this.employeeNumber = other.employeeNumber;
        this.specialization = other.specialization;
        this.hireDate = other.hireDate;
        this.salary = other.salary;
        this.officeNumber = other.officeNumber;
        this.employmentType = other.employmentType;
    }

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("  Employee number: " + (employeeNumber == null ? "(none)" : employeeNumber));
        System.out.println("  Specialization: " + (specialization == null ? "(none)" : specialization));
        System.out.println("  Hire date: " + (hireDate == null ? "(none)" : hireDate));
        System.out.println("  Salary: " + (salary == null ? "(none)" : salary));
        System.out.println("  Office number: " + (officeNumber == null ? "(none)" : officeNumber));
        System.out.println("  Employment type: " + (employmentType == null ? "(none)" : employmentType));
    }


    @Override public String getRoleDescription() { return "Teacher specialised in " + specialization + " (" + employmentType + ")"; }
    @Override public String getAcademicCategory() { return "TEACHING STAFF"; }
    @Override public String getDisplayName() { return super.getDisplayName() + " (" + employeeNumber + ")"; }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Registrable.id(employeeNumber, "Employee number");
        Registrable.text(specialization, "Specialization", 2, 120);
        Registrable.notFuture(hireDate, "Hire date");
        if (salary == null || salary.signum() <= 0) throw new ValidationException("Salary must be strictly positive.");
        Registrable.text(officeNumber, "Office number", 1, 20);
        if (!List.of("FULL_TIME", "PART_TIME", "CONTRACT").contains(employmentType))
            throw new ValidationException("Employment type must be FULL_TIME, PART_TIME or CONTRACT.");
    }
}
