package com.auca.abstracts;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Registrable;
import java.time.LocalDate;

public abstract class AcademicMember extends Person {

    private String memberNumber;
    private Department department;
    private LocalDate joinDate;
    private String academicStatus;
    private String institutionalEmail;
    private String advisorName;

    public AcademicMember(String personId, String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth, String memberNumber, Department department, LocalDate joinDate, String academicStatus, String institutionalEmail, String advisorName) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.memberNumber = memberNumber;
        this.department = department;
        this.joinDate = joinDate;
        this.academicStatus = academicStatus;
        this.institutionalEmail = institutionalEmail;
        this.advisorName = advisorName;
    }

    public String getMemberNumber() { return memberNumber; }
    public void setMemberNumber(String memberNumber) { this.memberNumber = memberNumber; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }
    public String getAcademicStatus() { return academicStatus; }
    public void setAcademicStatus(String academicStatus) { this.academicStatus = academicStatus; }
    public String getInstitutionalEmail() { return institutionalEmail; }
    public void setInstitutionalEmail(String institutionalEmail) { this.institutionalEmail = institutionalEmail; }
    public String getAdvisorName() { return advisorName; }
    public void setAdvisorName(String advisorName) { this.advisorName = advisorName; }

    @Override
    public void updateFrom(Registrable source) throws ValidationException {
        if (!(source instanceof AcademicMember)) throw new ValidationException("Record type mismatch: expected AcademicMember.");
        AcademicMember other = (AcademicMember) source;
        super.updateFrom(source);
        this.memberNumber = other.memberNumber;
        this.department = other.department;
        this.joinDate = other.joinDate;
        this.academicStatus = other.academicStatus;
        this.institutionalEmail = other.institutionalEmail;
        this.advisorName = other.advisorName;
    }

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("  Member number: " + (memberNumber == null ? "(none)" : memberNumber));
        System.out.println("  Department: " + (department == null ? "(none)" : "[" + department.getId() + "] " + department.getDisplayName()));
        System.out.println("  Join date: " + (joinDate == null ? "(none)" : joinDate));
        System.out.println("  Academic status: " + (academicStatus == null ? "(none)" : academicStatus));
        System.out.println("  Institutional email: " + (institutionalEmail == null ? "(none)" : institutionalEmail));
        System.out.println("  Advisor name: " + (advisorName == null ? "(none)" : advisorName));
    }


    /** Abstract: Student and Teacher define their own academic category. */
    public abstract String getAcademicCategory();

    @Override public boolean isActive() { return "ACTIVE".equals(academicStatus); }

    @Override
    public String getRegistrationSummary() {
        return super.getRegistrationSummary() + " | Member " + memberNumber + " | " + getAcademicCategory();
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Registrable.id(memberNumber, "Member number");
        Registrable.required(department, "Department");
        Registrable.notFuture(joinDate, "Join date");
        if (!"ACTIVE".equals(academicStatus) && !"INACTIVE".equals(academicStatus))
            throw new ValidationException("Academic status must be ACTIVE or INACTIVE.");
        Registrable.email(institutionalEmail, "Institutional email");
        Registrable.text(advisorName, "Advisor name", 2, 120);
    }
}
