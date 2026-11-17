package com.auca.assessment;

import com.auca.enrollment.Enrollment;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import java.time.LocalDate;
import java.util.List;

public class Assessment implements Identifiable, Registrable {

    private String assessmentId;
    private Enrollment enrollment;
    private String assessmentType;
    private double maximumScore;
    private double obtainedScore;
    private LocalDate assessmentDate;

    public Assessment(String assessmentId, Enrollment enrollment, String assessmentType, double maximumScore, double obtainedScore, LocalDate assessmentDate) {
        this.assessmentId = assessmentId;
        this.enrollment = enrollment;
        this.assessmentType = assessmentType;
        this.maximumScore = maximumScore;
        this.obtainedScore = obtainedScore;
        this.assessmentDate = assessmentDate;
    }

    @Override public String getId() { return assessmentId; }

    public String getAssessmentId() { return assessmentId; }
    public void setAssessmentId(String assessmentId) { this.assessmentId = assessmentId; }
    public Enrollment getEnrollment() { return enrollment; }
    public void setEnrollment(Enrollment enrollment) { this.enrollment = enrollment; }
    public String getAssessmentType() { return assessmentType; }
    public void setAssessmentType(String assessmentType) { this.assessmentType = assessmentType; }
    public double getMaximumScore() { return maximumScore; }
    public void setMaximumScore(double maximumScore) { this.maximumScore = maximumScore; }
    public double getObtainedScore() { return obtainedScore; }
    public void setObtainedScore(double obtainedScore) { this.obtainedScore = obtainedScore; }
    public LocalDate getAssessmentDate() { return assessmentDate; }
    public void setAssessmentDate(LocalDate assessmentDate) { this.assessmentDate = assessmentDate; }

    @Override
    public void updateFrom(Registrable source) throws ValidationException {
        if (!(source instanceof Assessment)) throw new ValidationException("Record type mismatch: expected Assessment.");
        Assessment other = (Assessment) source;
        this.assessmentId = other.assessmentId;
        this.enrollment = other.enrollment;
        this.assessmentType = other.assessmentType;
        this.maximumScore = other.maximumScore;
        this.obtainedScore = other.obtainedScore;
        this.assessmentDate = other.assessmentDate;
    }

    @Override
    public void printDetails() {
        System.out.println("--- " + getClass().getSimpleName() + " (entity v" + ENTITY_VERSION + ") ---");
        System.out.println("  Assessment id: " + (assessmentId == null ? "(none)" : assessmentId));
        System.out.println("  Enrollment: " + (enrollment == null ? "(none)" : "[" + enrollment.getId() + "] " + enrollment.getDisplayName()));
        System.out.println("  Assessment type: " + (assessmentType == null ? "(none)" : assessmentType));
        System.out.println("  Maximum score: " + maximumScore);
        System.out.println("  Obtained score: " + obtainedScore);
        System.out.println("  Assessment date: " + (assessmentDate == null ? "(none)" : assessmentDate));
    }

    @Override public String toString() { return getRegistrationSummary(); }


    @Override public String getDisplayName() { return assessmentType + " " + obtainedScore + "/" + maximumScore; }
    @Override public boolean isActive() { return true; }

    @Override
    public String getRegistrationSummary() {
        return "Assessment [" + assessmentId + "] " + assessmentType + " | " + obtainedScore + "/" + maximumScore
                + " | " + (enrollment == null ? "?" : enrollment.getDisplayName());
    }

    @Override
    public void validate() throws ValidationException {
        Registrable.id(assessmentId, "Assessment ID");
        Registrable.required(enrollment, "Enrollment");
        if (!List.of("QUIZ", "ASSIGNMENT", "MIDTERM", "FINAL_EXAM", "PROJECT").contains(assessmentType))
            throw new ValidationException("Assessment type must be QUIZ, ASSIGNMENT, MIDTERM, FINAL_EXAM or PROJECT.");
        if (!(maximumScore > 0)) throw new ValidationException("Maximum score must be greater than zero.");
        if (obtainedScore < 0 || obtainedScore > maximumScore)
            throw new ValidationException("Obtained score must be between 0 and " + maximumScore + ".");
        Registrable.notFuture(assessmentDate, "Assessment date");
    }
}
