package com.auca.abstracts;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import java.time.LocalDate;

public abstract class Person implements Identifiable, Registrable {

    private String personId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;

    public Person(String personId, String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    @Override public String getId() { return personId; }

    public String getPersonId() { return personId; }
    public void setPersonId(String personId) { this.personId = personId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    @Override
    public void updateFrom(Registrable source) throws ValidationException {
        if (!(source instanceof Person)) throw new ValidationException("Record type mismatch: expected Person.");
        Person other = (Person) source;
        this.personId = other.personId;
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.email = other.email;
        this.phoneNumber = other.phoneNumber;
        this.dateOfBirth = other.dateOfBirth;
    }

    @Override
    public void printDetails() {
        System.out.println("--- " + getClass().getSimpleName() + " (entity v" + ENTITY_VERSION + ") ---");
        System.out.println("  Person id: " + (personId == null ? "(none)" : personId));
        System.out.println("  First name: " + (firstName == null ? "(none)" : firstName));
        System.out.println("  Last name: " + (lastName == null ? "(none)" : lastName));
        System.out.println("  Email: " + (email == null ? "(none)" : email));
        System.out.println("  Phone number: " + (phoneNumber == null ? "(none)" : phoneNumber));
        System.out.println("  Date of birth: " + (dateOfBirth == null ? "(none)" : dateOfBirth));
    }

    @Override public String toString() { return getRegistrationSummary(); }


    /** Abstract: every concrete person type must describe its own role. */
    public abstract String getRoleDescription();

    @Override public String getDisplayName() { return firstName + " " + lastName; }

    @Override
    public String getRegistrationSummary() {
        return getRoleDescription() + " [" + personId + "] " + getDisplayName() + " <" + email + ">";
    }

    @Override
    public void validate() throws ValidationException {
        Registrable.id(personId, "Person ID");
        Registrable.text(firstName, "First name", MIN_NAME_LENGTH, MAX_NAME_LENGTH);
        Registrable.text(lastName, "Last name", MIN_NAME_LENGTH, MAX_NAME_LENGTH);
        Registrable.email(email, "Email");
        Registrable.phone(phoneNumber, "Phone");
        Registrable.pastDate(dateOfBirth, "Date of birth");
    }
}
