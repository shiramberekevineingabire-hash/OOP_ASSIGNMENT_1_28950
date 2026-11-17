package com.auca.feepayment;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class FeePayment implements Identifiable, Registrable {

    private String paymentId;
    private Student student;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String receiptNumber;

    public FeePayment(String paymentId, Student student, BigDecimal amount, LocalDate paymentDate, String paymentMethod, String receiptNumber) {
        this.paymentId = paymentId;
        this.student = student;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.receiptNumber = receiptNumber;
    }

    @Override public String getId() { return paymentId; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }

    @Override
    public void updateFrom(Registrable source) throws ValidationException {
        if (!(source instanceof FeePayment)) throw new ValidationException("Record type mismatch: expected FeePayment.");
        FeePayment other = (FeePayment) source;
        this.paymentId = other.paymentId;
        this.student = other.student;
        this.amount = other.amount;
        this.paymentDate = other.paymentDate;
        this.paymentMethod = other.paymentMethod;
        this.receiptNumber = other.receiptNumber;
    }

    @Override
    public void printDetails() {
        System.out.println("--- " + getClass().getSimpleName() + " (entity v" + ENTITY_VERSION + ") ---");
        System.out.println("  Payment id: " + (paymentId == null ? "(none)" : paymentId));
        System.out.println("  Student: " + (student == null ? "(none)" : "[" + student.getId() + "] " + student.getDisplayName()));
        System.out.println("  Amount: " + (amount == null ? "(none)" : amount));
        System.out.println("  Payment date: " + (paymentDate == null ? "(none)" : paymentDate));
        System.out.println("  Payment method: " + (paymentMethod == null ? "(none)" : paymentMethod));
        System.out.println("  Receipt number: " + (receiptNumber == null ? "(none)" : receiptNumber));
    }

    @Override public String toString() { return getRegistrationSummary(); }


    @Override public String getDisplayName() { return "Receipt " + receiptNumber + " - " + amount; }
    @Override public boolean isActive() { return true; }

    @Override
    public String getRegistrationSummary() {
        return "Payment [" + paymentId + "] " + amount + " via " + paymentMethod + " on " + paymentDate
                + " | " + (student == null ? "?" : student.getDisplayName()) + " | Receipt " + receiptNumber;
    }

    @Override
    public void validate() throws ValidationException {
        Registrable.id(paymentId, "Payment ID");
        Registrable.required(student, "Student");
        if (amount == null || amount.signum() <= 0) throw new ValidationException("Amount must be strictly positive.");
        Registrable.notFuture(paymentDate, "Payment date");
        if (!List.of("CASH", "MOBILE_MONEY", "BANK_TRANSFER", "CARD").contains(paymentMethod))
            throw new ValidationException("Payment method must be CASH, MOBILE_MONEY, BANK_TRANSFER or CARD.");
        Registrable.text(receiptNumber, "Receipt number", 1, 30);
    }
}
