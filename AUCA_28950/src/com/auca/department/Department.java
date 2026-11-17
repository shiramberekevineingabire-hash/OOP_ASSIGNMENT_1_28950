package com.auca.department;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;

public class Department implements Identifiable, Registrable {

    private String departmentId;
    private String departmentName;
    private String departmentCode;
    private String officeLocation;
    private String contactEmail;
    private Teacher headTeacher;

    public Department(String departmentId, String departmentName, String departmentCode, String officeLocation, String contactEmail, Teacher headTeacher) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.officeLocation = officeLocation;
        this.contactEmail = contactEmail;
        this.headTeacher = headTeacher;
    }

    @Override public String getId() { return departmentId; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }
    public String getOfficeLocation() { return officeLocation; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public Teacher getHeadTeacher() { return headTeacher; }
    public void setHeadTeacher(Teacher headTeacher) { this.headTeacher = headTeacher; }

    @Override
    public void updateFrom(Registrable source) throws ValidationException {
        if (!(source instanceof Department)) throw new ValidationException("Record type mismatch: expected Department.");
        Department other = (Department) source;
        this.departmentId = other.departmentId;
        this.departmentName = other.departmentName;
        this.departmentCode = other.departmentCode;
        this.officeLocation = other.officeLocation;
        this.contactEmail = other.contactEmail;
        this.headTeacher = other.headTeacher;
    }

    @Override
    public void printDetails() {
        System.out.println("--- " + getClass().getSimpleName() + " (entity v" + ENTITY_VERSION + ") ---");
        System.out.println("  Department id: " + (departmentId == null ? "(none)" : departmentId));
        System.out.println("  Department name: " + (departmentName == null ? "(none)" : departmentName));
        System.out.println("  Department code: " + (departmentCode == null ? "(none)" : departmentCode));
        System.out.println("  Office location: " + (officeLocation == null ? "(none)" : officeLocation));
        System.out.println("  Contact email: " + (contactEmail == null ? "(none)" : contactEmail));
        System.out.println("  Head teacher: " + (headTeacher == null ? "(none)" : "[" + headTeacher.getId() + "] " + headTeacher.getDisplayName()));
    }

    @Override public String toString() { return getRegistrationSummary(); }


    @Override public String getDisplayName() { return departmentName + " (" + departmentCode + ")"; }
    @Override public boolean isActive() { return true; }

    @Override
    public String getRegistrationSummary() {
        return "Department [" + departmentId + "] " + departmentName + " | Code " + departmentCode
                + " | Head: " + (headTeacher == null ? "not assigned" : headTeacher.getDisplayName());
    }

    @Override
    public void validate() throws ValidationException {
        Registrable.id(departmentId, "Department ID");
        Registrable.text(departmentName, "Department name", MIN_NAME_LENGTH, MAX_NAME_LENGTH);
        Registrable.text(departmentCode, "Department code", 2, 10);
        Registrable.text(officeLocation, "Office location", 2, 60);
        Registrable.email(contactEmail, "Contact email");
        // headTeacher is optional: it may be assigned after a Teacher exists
    }
}
