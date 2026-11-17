package com.auca.bookloan;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import java.time.LocalDate;

public class BookLoan implements Identifiable, Registrable {

    private String loanId;
    private Student student;
    private LibraryBook book;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private LocalDate returnedDate;

    public BookLoan(String loanId, Student student, LibraryBook book, LocalDate borrowedDate, LocalDate dueDate, LocalDate returnedDate) {
        this.loanId = loanId;
        this.student = student;
        this.book = book;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.returnedDate = returnedDate;
    }

    @Override public String getId() { return loanId; }

    public String getLoanId() { return loanId; }
    public void setLoanId(String loanId) { this.loanId = loanId; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public LibraryBook getBook() { return book; }
    public void setBook(LibraryBook book) { this.book = book; }
    public LocalDate getBorrowedDate() { return borrowedDate; }
    public void setBorrowedDate(LocalDate borrowedDate) { this.borrowedDate = borrowedDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getReturnedDate() { return returnedDate; }
    public void setReturnedDate(LocalDate returnedDate) { this.returnedDate = returnedDate; }

    @Override
    public void updateFrom(Registrable source) throws ValidationException {
        if (!(source instanceof BookLoan)) throw new ValidationException("Record type mismatch: expected BookLoan.");
        BookLoan other = (BookLoan) source;
        this.loanId = other.loanId;
        this.student = other.student;
        this.book = other.book;
        this.borrowedDate = other.borrowedDate;
        this.dueDate = other.dueDate;
        this.returnedDate = other.returnedDate;
    }

    @Override
    public void printDetails() {
        System.out.println("--- " + getClass().getSimpleName() + " (entity v" + ENTITY_VERSION + ") ---");
        System.out.println("  Loan id: " + (loanId == null ? "(none)" : loanId));
        System.out.println("  Student: " + (student == null ? "(none)" : "[" + student.getId() + "] " + student.getDisplayName()));
        System.out.println("  Book: " + (book == null ? "(none)" : "[" + book.getId() + "] " + book.getDisplayName()));
        System.out.println("  Borrowed date: " + (borrowedDate == null ? "(none)" : borrowedDate));
        System.out.println("  Due date: " + (dueDate == null ? "(none)" : dueDate));
        System.out.println("  Returned date: " + (returnedDate == null ? "(none)" : returnedDate));
    }

    @Override public String toString() { return getRegistrationSummary(); }


    @Override
    public String getDisplayName() {
        return (book == null ? "?" : book.getTitle()) + " -> " + (student == null ? "?" : student.getDisplayName());
    }
    /** A loan is "active" while the book has not been returned. */
    @Override public boolean isActive() { return returnedDate == null; }

    @Override
    public String getRegistrationSummary() {
        return "Loan [" + loanId + "] " + getDisplayName() + " | Borrowed " + borrowedDate + " | Due " + dueDate
                + " | " + (returnedDate == null ? "NOT RETURNED" : "Returned " + returnedDate);
    }

    @Override
    public void validate() throws ValidationException {
        Registrable.id(loanId, "Loan ID");
        Registrable.required(student, "Student");
        Registrable.required(book, "Library book");
        Registrable.required(borrowedDate, "Borrowed date");
        Registrable.required(dueDate, "Due date");
        if (!dueDate.isAfter(borrowedDate)) throw new ValidationException("Due date must be after the borrowed date.");
        if (returnedDate != null && returnedDate.isBefore(borrowedDate))
            throw new ValidationException("Returned date cannot be before the borrowed date.");
    }
}
