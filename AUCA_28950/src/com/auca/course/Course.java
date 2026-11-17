package com.auca.course;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;

public class Course implements Identifiable, Registrable {

    private String courseId;
    private String courseCode;
    private String courseTitle;
    private int creditHours;
    private Department department;
    private Teacher assignedTeacher;

    public Course(String courseId, String courseCode, String courseTitle, int creditHours, Department department, Teacher assignedTeacher) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.creditHours = creditHours;
        this.department = department;
        this.assignedTeacher = assignedTeacher;
    }

    @Override public String getId() { return courseId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
    public int getCreditHours() { return creditHours; }
    public void setCreditHours(int creditHours) { this.creditHours = creditHours; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public Teacher getAssignedTeacher() { return assignedTeacher; }
    public void setAssignedTeacher(Teacher assignedTeacher) { this.assignedTeacher = assignedTeacher; }

    @Override
    public void updateFrom(Registrable source) throws ValidationException {
        if (!(source instanceof Course)) throw new ValidationException("Record type mismatch: expected Course.");
        Course other = (Course) source;
        this.courseId = other.courseId;
        this.courseCode = other.courseCode;
        this.courseTitle = other.courseTitle;
        this.creditHours = other.creditHours;
        this.department = other.department;
        this.assignedTeacher = other.assignedTeacher;
    }

    @Override
    public void printDetails() {
        System.out.println("--- " + getClass().getSimpleName() + " (entity v" + ENTITY_VERSION + ") ---");
        System.out.println("  Course id: " + (courseId == null ? "(none)" : courseId));
        System.out.println("  Course code: " + (courseCode == null ? "(none)" : courseCode));
        System.out.println("  Course title: " + (courseTitle == null ? "(none)" : courseTitle));
        System.out.println("  Credit hours: " + creditHours);
        System.out.println("  Department: " + (department == null ? "(none)" : "[" + department.getId() + "] " + department.getDisplayName()));
        System.out.println("  Assigned teacher: " + (assignedTeacher == null ? "(none)" : "[" + assignedTeacher.getId() + "] " + assignedTeacher.getDisplayName()));
    }

    @Override public String toString() { return getRegistrationSummary(); }


    @Override public String getDisplayName() { return courseCode + " - " + courseTitle; }
    @Override public boolean isActive() { return true; }

    @Override
    public String getRegistrationSummary() {
        return "Course [" + courseId + "] " + courseCode + " - " + courseTitle + " | " + creditHours + " credits | "
                + (department == null ? "no department" : department.getDepartmentName()) + " | Teacher: "
                + (assignedTeacher == null ? "none" : assignedTeacher.getDisplayName());
    }

    @Override
    public void validate() throws ValidationException {
        Registrable.id(courseId, "Course ID");
        Registrable.text(courseCode, "Course code", 2, 20);
        Registrable.text(courseTitle, "Course title", 2, 120);
        Registrable.range(creditHours, "Credit hours", 1, 6);
        Registrable.required(department, "Department");
        Registrable.required(assignedTeacher, "Assigned teacher");
    }
}
