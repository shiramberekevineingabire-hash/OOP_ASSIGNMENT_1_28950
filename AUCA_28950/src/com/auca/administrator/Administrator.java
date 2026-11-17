package com.auca.administrator;

import com.auca.abstracts.Person;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Registrable;
import java.time.LocalDate;

public class Administrator extends Person {

    private String staffNumber;
    private String jobTitle;
    private String assignedOffice;
    private LocalDate employmentDate;
    private int accessLevel;
    private boolean active;

    public Administrator(String personId, String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth, String staffNumber, String jobTitle, String assignedOffice, LocalDate employmentDate, int accessLevel, boolean active) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.staffNumber = staffNumber;
        this.jobTitle = jobTitle;
        this.assignedOffice = assignedOffice;
        this.employmentDate = employmentDate;
        this.accessLevel = accessLevel;
        this.active = active;
    }

    public String getStaffNumber() { return staffNumber; }
    public void setStaffNumber(String staffNumber) { this.staffNumber = staffNumber; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getAssignedOffice() { return assignedOffice; }
    public void setAssignedOffice(String assignedOffice) { this.assignedOffice = assignedOffice; }
    public LocalDate getEmploymentDate() { return employmentDate; }
    public void setEmploymentDate(LocalDate employmentDate) { this.employmentDate = employmentDate; }
    public int getAccessLevel() { return accessLevel; }
    public void setAccessLevel(int accessLevel) { this.accessLevel = accessLevel; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public void updateFrom(Registrable source) throws ValidationException {
        if (!(source instanceof Administrator)) throw new ValidationException("Record type mismatch: expected Administrator.");
        Administrator other = (Administrator) source;
        super.updateFrom(source);
        this.staffNumber = other.staffNumber;
        this.jobTitle = other.jobTitle;
        this.assignedOffice = other.assignedOffice;
        this.employmentDate = other.employmentDate;
        this.accessLevel = other.accessLevel;
        this.active = other.active;
    }

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("  Staff number: " + (staffNumber == null ? "(none)" : staffNumber));
        System.out.println("  Job title: " + (jobTitle == null ? "(none)" : jobTitle));
        System.out.println("  Assigned office: " + (assignedOffice == null ? "(none)" : assignedOffice));
        System.out.println("  Employment date: " + (employmentDate == null ? "(none)" : employmentDate));
        System.out.println("  Access level: " + accessLevel);
        System.out.println("  Active: " + active);
    }


    @Override public String getRoleDescription() { return "Administrator - " + jobTitle + " (access level " + accessLevel + ")"; }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Registrable.id(staffNumber, "Staff number");
        Registrable.text(jobTitle, "Job title", 2, 60);
        Registrable.text(assignedOffice, "Assigned office", 1, 30);
        Registrable.notFuture(employmentDate, "Employment date");
        Registrable.range(accessLevel, "Access level", 1, 5);
    }
}
