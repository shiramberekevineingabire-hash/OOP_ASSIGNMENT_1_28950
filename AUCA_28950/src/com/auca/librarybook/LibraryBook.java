package com.auca.librarybook;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

public class LibraryBook implements Identifiable, Registrable {

    private String bookId;
    private String isbn;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;

    public LibraryBook(String bookId, String isbn, String title, String author, int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    @Override public String getId() { return bookId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    @Override
    public void updateFrom(Registrable source) throws ValidationException {
        if (!(source instanceof LibraryBook)) throw new ValidationException("Record type mismatch: expected LibraryBook.");
        LibraryBook other = (LibraryBook) source;
        this.bookId = other.bookId;
        this.isbn = other.isbn;
        this.title = other.title;
        this.author = other.author;
        this.totalCopies = other.totalCopies;
        this.availableCopies = other.availableCopies;
    }

    @Override
    public void printDetails() {
        System.out.println("--- " + getClass().getSimpleName() + " (entity v" + ENTITY_VERSION + ") ---");
        System.out.println("  Book id: " + (bookId == null ? "(none)" : bookId));
        System.out.println("  Isbn: " + (isbn == null ? "(none)" : isbn));
        System.out.println("  Title: " + (title == null ? "(none)" : title));
        System.out.println("  Author: " + (author == null ? "(none)" : author));
        System.out.println("  Total copies: " + totalCopies);
        System.out.println("  Available copies: " + availableCopies);
    }

    @Override public String toString() { return getRegistrationSummary(); }


    @Override public String getDisplayName() { return title + " by " + author; }
    @Override public boolean isActive() { return availableCopies > 0; }

    @Override
    public String getRegistrationSummary() {
        return "Book [" + bookId + "] " + title + " by " + author + " | ISBN " + isbn
                + " | Available " + availableCopies + "/" + totalCopies;
    }

    @Override
    public void validate() throws ValidationException {
        Registrable.id(bookId, "Book ID");
        String digits = isbn == null ? "" : isbn.replace("-", "");
        if (!digits.matches("\\d{10}|\\d{13}")) throw new ValidationException("ISBN must contain 10 or 13 digits.");
        Registrable.text(title, "Title", 1, 120);
        Registrable.text(author, "Author", 2, 120);
        if (totalCopies < 0) throw new ValidationException("Total copies cannot be negative.");
        if (availableCopies < 0) throw new ValidationException("Available copies cannot be negative.");
        if (availableCopies > totalCopies) throw new ValidationException("Available copies cannot exceed total copies.");
    }
}
