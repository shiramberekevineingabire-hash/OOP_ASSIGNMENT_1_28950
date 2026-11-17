package com.auca.main;

import com.auca.abstracts.AcademicMember;
import com.auca.abstracts.Person;
import com.auca.administrator.Administrator;
import com.auca.assessment.Assessment;
import com.auca.bookloan.BookLoan;
import com.auca.classroom.Classroom;
import com.auca.course.Course;
import com.auca.department.Department;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.BookUnavailableException;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.PrerequisiteMissingException;
import com.auca.exceptions.RecordNotFoundException;
import com.auca.exceptions.ValidationException;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.teacher.Teacher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * AUCA_ID School Management System - console application.
 * All input comes from ONE shared Scanner on System.in, read with nextLine() only.
 * The menu, the smart multi-step forms (with nested prerequisite registration) and all
 * reports live here. Records are kept in typed in-memory ArrayLists.
 */
public class Main {

    // ------------------------------------------------------------------ storage
    private static final Scanner IN = new Scanner(System.in);
    private static final List<Department> departments = new ArrayList<>();
    private static final List<Student> students = new ArrayList<>();
    private static final List<Teacher> teachers = new ArrayList<>();
    private static final List<Administrator> admins = new ArrayList<>();
    private static final List<Course> courses = new ArrayList<>();
    private static final List<Classroom> classrooms = new ArrayList<>();
    private static final List<Enrollment> enrollments = new ArrayList<>();
    private static final List<Assessment> assessments = new ArrayList<>();
    private static final List<FeePayment> payments = new ArrayList<>();
    private static final List<LibraryBook> books = new ArrayList<>();
    private static final List<BookLoan> loans = new ArrayList<>();

    private static final int ID_MIN = Identifiable.MIN_ID_LENGTH;
    private static final int ID_MAX = Identifiable.MAX_ID_LENGTH;

    // ------------------------------------------------------------------ entry point
    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            int choice;
            try {
                choice = Integer.parseInt(readLine("Select an option: ").trim());
            } catch (NumberFormatException e) {
                System.out.println("! Invalid input. Please enter a number from 0 to 19.");
                continue;
            }
            try {
                switch (choice) {
                    case 1 -> flow(Main::registerDepartment);
                    case 2 -> flow(Main::registerStudent);
                    case 3 -> flow(Main::registerTeacher);
                    case 4 -> flow(Main::registerAdministrator);
                    case 5 -> flow(Main::registerCourse);
                    case 6 -> flow(Main::registerClassroom);
                    case 7 -> flow(Main::enrollStudent);
                    case 8 -> flow(Main::recordAssessment);
                    case 9 -> flow(Main::recordPayment);
                    case 10 -> flow(Main::registerBook);
                    case 11 -> flow(Main::borrowBook);
                    case 12 -> returnBook();
                    case 13 -> viewAllRecords();
                    case 14 -> searchById();
                    case 15 -> updateRecord();
                    case 16 -> studentSummary();
                    case 17 -> paymentHistory();
                    case 18 -> availableBooks();
                    case 19 -> demonstratePolymorphism();
                    case 0 -> {
                        System.out.println("Goodbye from AUCA_ID.");
                        running = false;
                    }
                    default -> System.out.println("! Invalid option. Choose a number from 0 to 19.");
                }
            } catch (ValidationException | DuplicateRecordException | RecordNotFoundException
                     | PrerequisiteMissingException | BookUnavailableException e) {
                System.out.println("! " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("! Something unexpected went wrong (" + e.getClass().getSimpleName()
                        + ": " + e.getMessage() + "). Returning to the main menu.");
            }
        }
    }

    private static void printMenu() {
        String[] left = {"Register Department", "Register Student", "Register Teacher", "Register Administrator",
            "Register Course", "Register Classroom", "Enroll Student", "Record Assessment",
            "Record Fee Payment", "Register Library Book"};
        String[] right = {"Borrow Library Book", "Return Library Book", "View All Records", "Search Record by ID",
            "Update Existing Record", "Student Academic Summary", "Student Payment History",
            "View Available Books", "Demonstrate Polymorphism", "Exit"};
        System.out.println();
        System.out.println("============== AUCA_28950 SCHOOL MANAGEMENT ==============");
        for (int i = 0; i < 10; i++) {
            int rn = (i == 9) ? 0 : i + 11;
            System.out.println(String.format("%2d. %-26s %2d. %s", i + 1, left[i], rn, right[i]));
        }
    }

    /** Runs a registration, then offers the "register another / view / main menu" follow-up. */
    private static void flow(Supplier<Registrable> registration) {
        while (true) {
            Registrable r = registration.get();
            if (r == null) return;
            System.out.println("  [1] Register another   [2] View record   [0] Main menu");
            String c = readLine("Select option: ").trim();
            if (c.equals("1")) continue;
            if (c.equals("2")) r.printDetails();
            return;
        }
    }

    // ------------------------------------------------------------------ console helpers
    private static String readLine(String prompt) {
        System.out.print(prompt);
        if (!IN.hasNextLine()) {
            System.out.println("\nInput closed. Exiting AUCA_28950.");
            System.exit(0);
        }
        return IN.nextLine();
    }

    private static boolean confirm(String question) {
        while (true) {
            String c = readLine(question + " [1] Yes  [0] No : ").trim();
            if (c.equals("1")) return true;
            if (c.equals("0")) return false;
            System.out.println("! Enter 1 or 0.");
        }
    }

    private static void border() { System.out.println("+" + "-".repeat(60) + "+"); }
    private static void row(String s) { System.out.println(String.format("| %-58s |", s)); }

    private static String label(Object o) {
        if (o instanceof Identifiable i) return "[" + i.getId() + "] " + i.getDisplayName();
        return String.valueOf(o);
    }

    private static String s(Object o) { return (String) o; }
    private static LocalDate dt(Object o) { return (LocalDate) o; }

    // ------------------------------------------------------------------ parsers / validators
    private static final int ANY = 0, NOT_FUTURE = 1, PAST = 2;

    private static void dupCheck(String label, String value, Supplier<Stream<String>> taken) {
        if (taken.get().anyMatch(x -> x.equalsIgnoreCase(value)))
            throw new DuplicateRecordException(label + " '" + value + "' already exists.");
    }

    private static Function<String, Object> txt(String label, int min, int max) {
        return in -> Registrable.text(in, label, min, max);
    }

    private static Function<String, Object> uTxt(String label, int min, int max, Supplier<Stream<String>> taken) {
        return in -> {
            String t = Registrable.text(in, label, min, max);
            dupCheck(label, t, taken);
            return t;
        };
    }

    private static Function<String, Object> email(String label) {
        return in -> { Registrable.email(in, label); return in.trim(); };
    }

    private static Function<String, Object> uEmail(String label, Supplier<Stream<String>> taken) {
        return in -> { Registrable.email(in, label); dupCheck(label, in.trim(), taken); return in.trim(); };
    }

    private static Function<String, Object> phone(String label) {
        return in -> { Registrable.phone(in, label); return in.trim(); };
    }

    private static Function<String, Object> date(String label, int rule) {
        return in -> {
            LocalDate d = LocalDate.parse(in.trim());          // DateTimeParseException if invalid
            if (rule == PAST) Registrable.pastDate(d, label);
            else if (rule == NOT_FUTURE) Registrable.notFuture(d, label);
            return d;
        };
    }

    private static Function<String, Object> intR(String label, int min, int max) {
        return in -> Registrable.range(Integer.parseInt(in.trim()), label, min, max);   // NumberFormatException
    }

    private static Function<String, Object> money(String label) {
        return in -> {
            BigDecimal b = new BigDecimal(in.trim());
            if (b.signum() <= 0) throw new ValidationException(label + " must be strictly positive.");
            if (b.scale() > 2) throw new ValidationException(label + " can have at most 2 decimal places.");
            return b.setScale(2, RoundingMode.UNNECESSARY);
        };
    }

    // ------------------------------------------------------------------ smart-form engine
    private static final int TEXT = 0, CHOICE = 1, SELECT = 2, DERIVED = 3;
    private static final Object CANCEL = new Object(), BACK = new Object(), NONE = new Object();
    /** Values of the form currently being filled (lets a parser look at earlier fields). */
    private static Object[] cur;

    private static class Field {
        final int kind;
        final String label, hint;
        int step;
        Function<String, Object> parser;
        boolean recheck;
        String[] opts;
        Supplier<List<?>> items;
        Runnable registrar;
        String noun, emptyMsg, derivedText;
        boolean optional;
        Consumer<Object> check;
        Function<Object, String> describer;

        Field(int kind, String label, String hint) { this.kind = kind; this.label = label; this.hint = hint; }
        Field optional() { this.optional = true; return this; }
        Field empty(String m) { this.emptyMsg = m; return this; }
        Field check(Consumer<Object> c) { this.check = c; return this; }
        Field describe(Function<Object, String> d) { this.describer = d; return this; }
    }

    private static Field tf(String label, String hint, Function<String, Object> p) {
        Field f = new Field(TEXT, label, hint); f.parser = p; return f;
    }

    private static Field uf(String label, String hint, Function<String, Object> p) {
        Field f = tf(label, hint, p); f.recheck = true; return f;
    }

    private static Field cf(String label, String... opts) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < opts.length; i++) sb.append(i == 0 ? "" : " / ").append(i + 1).append(' ').append(opts[i]);
        Field f = new Field(CHOICE, label, sb.toString()); f.opts = opts; return f;
    }

    private static Field sf(String label, String noun, Supplier<List<?>> items, Runnable registrar) {
        Field f = new Field(SELECT, label, noun + " | select"); f.noun = noun; f.items = items; f.registrar = registrar; return f;
    }

    private static Field df(String label, String text) {
        Field f = new Field(DERIVED, label, "derived"); f.derivedText = text; return f;
    }

    private static String show(Field f, Object v) {
        if (f.kind == DERIVED) return f.derivedText;
        if (v == null) return "(none)";
        return label(v);
    }

    private static class Form {
        final String title;
        final List<String> stepTitles = new ArrayList<>();
        final List<Field> fields = new ArrayList<>();
        Function<Object[], Registrable> maker;
        Consumer<Registrable> rule = r -> { };   // extra cross-record rules checked on Save

        Form(String title) { this.title = title; }
        Form step(String t) { stepTitles.add(t); return this; }
        Form add(Field f) { f.step = stepTitles.size(); fields.add(f); return this; }

        /** Runs the form. initial == null means "create"; otherwise "update" starting at the review screen. */
        Registrable run(Object[] initial, Consumer<Registrable> commit) {
            int n = fields.size();
            Object[] v = initial != null ? initial.clone() : new Object[n];
            try {
                if (initial == null) {
                    banner();
                    int i = 0, shown = 0;
                    while (i < n) {
                        Field f = fields.get(i);
                        if (f.step != shown) {
                            shown = f.step;
                            System.out.println();
                            System.out.println("[STEP " + shown + "/" + (stepTitles.size() + 1) + "] "
                                    + stepTitles.get(shown - 1).toUpperCase());
                            System.out.println("  [BACK] previous field   [CANCEL] discard this record");
                        }
                        if (f.kind == DERIVED) { v[i] = null; i++; continue; }
                        cur = v;
                        Object r = ask(f, i, v[i], false);
                        if (r == CANCEL) { System.out.println("Cancelled. Nothing was saved."); return null; }
                        if (r == BACK) {
                            int j = i - 1;
                            while (j > 0 && fields.get(j).kind == DERIVED) j--;
                            i = Math.max(0, j);
                            continue;
                        }
                        v[i] = (r == NONE) ? null : r;
                        i++;
                    }
                }
                return reviewLoop(v, initial != null, commit);
            } catch (PrerequisiteMissingException e) {
                System.out.println("! " + e.getMessage());
                System.out.println("Registration cancelled. Nothing was saved.");
                return null;
            }
        }

        private void banner() {
            System.out.println();
            border();
            row("AUCA_28950 / SCHOOL MANAGEMENT");
            row(title.toUpperCase() + "   (" + (stepTitles.size() + 1) + " steps)");
            border();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < stepTitles.size(); i++) sb.append('[').append(i + 1).append("] ").append(stepTitles.get(i)).append("  ");
            sb.append('[').append(stepTitles.size() + 1).append("] Review & confirm");
            row(sb.toString());
            row("Type BACK to return; CANCEL to discard the unsaved record.");
            border();
        }

        private void review(Object[] v) {
            System.out.println();
            border();
            row("REVIEW " + title.toUpperCase());
            border();
            int last = 0;
            for (int i = 0; i < fields.size(); i++) {
                Field f = fields.get(i);
                if (f.step != last) { row(stepTitles.get(f.step - 1).toUpperCase()); last = f.step; }
                row(String.format("%02d %-20s: %s", i + 1, f.label, show(f, v[i])));
            }
            border();
            row("[1] Save   [2] Edit field   [0] Cancel");
            border();
        }

        private Registrable reviewLoop(Object[] v, boolean updating, Consumer<Registrable> commit) {
            while (true) {
                review(v);
                String c = readLine("Select option: ").trim();
                switch (c) {
                    case "1" -> {
                        try {
                            cur = v;
                            for (int i = 0; i < fields.size(); i++) {      // uniqueness re-check before saving
                                Field f = fields.get(i);
                                if (f.recheck) f.parser.apply((String) v[i]);
                            }
                            Registrable r = maker.apply(v);
                            r.validate();
                            rule.accept(r);
                            if (updating && !confirm("Apply these changes to the existing record?")) {
                                System.out.println("Update not applied.");
                                continue;
                            }
                            commit.accept(r);
                            Identifiable id = (Identifiable) r;
                            System.out.println("+ " + title + " " + id.getId() + " (" + id.getDisplayName()
                                    + ") " + (updating ? "updated" : "registered") + " successfully.");
                            return r;
                        } catch (ValidationException | DuplicateRecordException | BookUnavailableException
                                 | RecordNotFoundException e) {
                            System.out.println("! " + e.getMessage() + "  Use [2] Edit field to correct it.");
                        }
                    }
                    case "2" -> editField(v);
                    case "0" -> {
                        System.out.println(updating ? "Cancelled. Nothing was changed." : "Cancelled. Nothing was saved.");
                        return null;
                    }
                    default -> System.out.println("! Choose 1, 2 or 0.");
                }
            }
        }

        private void editField(Object[] v) {
            int n = fields.size();
            String in = readLine(String.format("Field number to edit (01-%02d): ", n)).trim();
            try {
                int k = Integer.parseInt(in);
                if (k < 1 || k > n) throw new ValidationException(String.format("Field number must be between 01 and %02d.", n));
                Field f = fields.get(k - 1);
                if (f.kind == DERIVED) throw new ValidationException("Field " + k + " is derived automatically and cannot be edited.");
                cur = v;
                Object r = ask(f, k - 1, v[k - 1], true);
                if (r == CANCEL || r == BACK) System.out.println("Edit cancelled; value unchanged.");
                else v[k - 1] = (r == NONE) ? null : r;
            } catch (NumberFormatException e) {
                System.out.println("! Please enter a field number such as 06.");
            } catch (ValidationException e) {
                System.out.println("! " + e.getMessage());
            }
        }
    }

    /** Asks one field. Returns the parsed value, CANCEL, BACK or NONE (optional reference left empty). */
    private static Object ask(Field f, int idx, Object old, boolean editing) {
        String num = String.format("%02d", idx + 1);
        String keep = editing ? "  (current: " + show(f, old) + ", ENTER keeps it)" : "";
        if (f.kind == SELECT) return askSelect(f, num, editing, old);
        while (true) {
            String line = readLine(String.format("%s %-20s [%s]%s : ", num, f.label, f.hint, keep)).trim();
            if (line.equalsIgnoreCase("CANCEL")) return CANCEL;
            if (line.equalsIgnoreCase("BACK")) return BACK;
            if (editing && line.isEmpty()) return old;
            try {
                if (f.kind == CHOICE) {
                    try {
                        int k = Integer.parseInt(line);
                        if (k >= 1 && k <= f.opts.length) return f.opts[k - 1];
                    } catch (NumberFormatException e) {
                        for (String o : f.opts) if (o.equalsIgnoreCase(line)) return o;
                    }
                    throw new ValidationException("Choose a number from 1 to " + f.opts.length + " (" + f.hint + ").");
                }
                return f.parser.apply(line);
            } catch (ValidationException | DuplicateRecordException e) {
                System.out.println("! " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("! Invalid number '" + line + "'. Enter digits only (money example: 1500.50).");
            } catch (DateTimeParseException e) {
                System.out.println("! Invalid date: use YYYY-MM-DD (example: 2004-05-16).");
            }
            System.out.println("  Please re-enter field " + num + ":");
        }
    }

    private static Object askSelect(Field f, String num, boolean editing, Object old) {
        while (true) {
            List<?> items = f.items.get();
            String missing = f.emptyMsg != null ? f.emptyMsg
                    : "No " + f.noun + " exists. Register a " + f.noun + " before continuing.";
            if (items.isEmpty()) {
                if (f.optional) {
                    System.out.println(num + " " + f.label + ": no " + f.noun + " is registered yet - left empty "
                            + "(it can be assigned later with Update Record).");
                    return NONE;
                }
                border();
                row("CHOOSE " + f.noun.toUpperCase() + "  (field " + num + ")");
                border();
                row(missing);
                row("1  Register " + f.noun + " now");
                row("0  Cancel");
                border();
                String c = readLine("Select option: ").trim();
                if (c.equals("1")) { f.registrar.run(); continue; }
                if (c.equals("0")) throw new PrerequisiteMissingException(missing);
                System.out.println("! Enter 1 or 0.");
                continue;
            }
            border();
            row("CHOOSE " + f.noun.toUpperCase() + "  (field " + num + ")");
            border();
            for (int i = 0; i < items.size(); i++) {
                Object it = items.get(i);
                row((i + 1) + "  " + (f.describer != null ? f.describer.apply(it) : label(it)));
            }
            row((items.size() + 1) + "  [+] Register a new " + f.noun);
            if (f.optional) row("S  Skip (leave empty)");
            if (editing) row("ENTER  keep current: " + show(f, old));
            row("0  Cancel");
            border();
            String line = readLine("Select option: ").trim();
            if (line.equalsIgnoreCase("BACK")) return BACK;
            if (editing && line.isEmpty()) return old == null ? NONE : old;
            if (f.optional && line.equalsIgnoreCase("S")) return NONE;
            try {
                int k = Integer.parseInt(line);
                if (k == 0) return CANCEL;
                if (k == items.size() + 1) { f.registrar.run(); continue; }
                if (k >= 1 && k <= items.size()) {
                    Object chosen = items.get(k - 1);
                    if (f.check != null) f.check.accept(chosen);
                    return chosen;
                }
            } catch (NumberFormatException e) {
                // fall through to the message below
            } catch (BookUnavailableException e) {
                System.out.println("! " + e.getMessage());
                continue;
            }
            System.out.println("! Invalid selection. Pick one of the numbers shown.");
        }
    }

    // ------------------------------------------------------------------ record collections
    private static List<Person> people() {
        List<Person> l = new ArrayList<>(students);
        l.addAll(teachers);
        l.addAll(admins);
        return l;
    }

    private static List<AcademicMember> members() {
        List<AcademicMember> l = new ArrayList<>(students);
        l.addAll(teachers);
        return l;
    }

    private static List<Identifiable> allRecords() {
        List<Identifiable> l = new ArrayList<>();
        l.addAll(departments); l.addAll(students); l.addAll(teachers); l.addAll(admins);
        l.addAll(courses); l.addAll(classrooms); l.addAll(enrollments); l.addAll(assessments);
        l.addAll(payments); l.addAll(books); l.addAll(loans);
        return l;
    }

    // ------------------------------------------------------------------ forms (one per concrete class)
    private static void personFields(Form f, Person ed) {
        Supplier<Stream<String>> ids = () -> people().stream().filter(p -> p != ed).map(Person::getPersonId);
        Supplier<Stream<String>> mails = () -> people().stream().filter(p -> p != ed).map(Person::getEmail);
        f.step("Personal details")
                .add(uf("Person ID", "String | unique | 4-30 chars", uTxt("Person ID", ID_MIN, ID_MAX, ids)))
                .add(tf("First name", "String | 2-60 chars", txt("First name", 2, 60)))
                .add(tf("Last name", "String | 2-60 chars", txt("Last name", 2, 60)))
                .add(uf("Email", "String | unique | email", uEmail("Email", mails)))
                .add(tf("Phone", "String | 10-15 digits, optional +", phone("Phone")))
                .add(tf("Date of birth", "LocalDate | YYYY-MM-DD | past", date("Date of birth", PAST)));
    }

    private static void memberFields(Form f, AcademicMember ed) {
        Supplier<Stream<String>> nums = () -> members().stream().filter(m -> m != ed).map(AcademicMember::getMemberNumber);
        Supplier<Stream<String>> mails = () -> members().stream().filter(m -> m != ed).map(AcademicMember::getInstitutionalEmail);
        f.step("Academic details")
                .add(uf("Member number", "String | unique | 4-30 chars", uTxt("Member number", ID_MIN, ID_MAX, nums)))
                .add(sf("Department", "Department", () -> departments, Main::registerDepartment))
                .add(tf("Join date", "LocalDate | YYYY-MM-DD | not future", date("Join date", NOT_FUTURE)))
                .add(cf("Academic status", "ACTIVE", "INACTIVE"))
                .add(uf("Institutional email", "String | unique | email", uEmail("Institutional email", mails)))
                .add(tf("Advisor name", "String | 2-120 chars", txt("Advisor name", 2, 120)));
    }

    private static Form departmentForm(Department ed) {
        Supplier<Stream<String>> ids = () -> departments.stream().filter(x -> x != ed).map(Department::getDepartmentId);
        Supplier<Stream<String>> codes = () -> departments.stream().filter(x -> x != ed).map(Department::getDepartmentCode);
        Form f = new Form("Department");
        f.step("Department identity")
                .add(uf("Department ID", "String | unique | 4-30 chars", uTxt("Department ID", ID_MIN, ID_MAX, ids)))
                .add(tf("Department name", "String | 2-60 chars", txt("Department name", 2, 60)))
                .add(uf("Department code", "String | unique | 2-10 chars", uTxt("Department code", 2, 10, codes)))
                .step("Contact and head")
                .add(tf("Office location", "String | 2-60 chars", txt("Office location", 2, 60)))
                .add(tf("Contact email", "String | email", email("Contact email")))
                .add(sf("Head teacher", "Teacher", () -> teachers, Main::registerTeacher).optional());
        f.maker = v -> new Department(s(v[0]), s(v[1]), s(v[2]), s(v[3]), s(v[4]), (Teacher) v[5]);
        return f;
    }

    private static Form studentForm(Student ed) {
        Supplier<Stream<String>> nums = () -> students.stream().filter(x -> x != ed).map(Student::getStudentNumber);
        Form f = new Form("Student");
        personFields(f, ed);
        memberFields(f, ed);
        f.step("Student details")
                .add(uf("Student number", "String | unique | 4-30 chars", uTxt("Student number", ID_MIN, ID_MAX, nums)))
                .add(tf("Program name", "String | 2-120 chars", txt("Program name", 2, 120)))
                .add(tf("Admission date", "LocalDate | YYYY-MM-DD | not future", date("Admission date", NOT_FUTURE)))
                .add(tf("Study year", "int | 1-6", intR("Study year", 1, 6)))
                .add(tf("Guardian phone", "String | 10-15 digits", phone("Guardian phone")))
                .add(cf("Scholarship", "YES", "NO"));
        f.maker = v -> new Student(s(v[0]), s(v[1]), s(v[2]), s(v[3]), s(v[4]), dt(v[5]),
                s(v[6]), (Department) v[7], dt(v[8]), s(v[9]), s(v[10]), s(v[11]),
                s(v[12]), s(v[13]), dt(v[14]), (Integer) v[15], s(v[16]), "YES".equals(v[17]));
        return f;
    }

    private static Form teacherForm(Teacher ed) {
        Supplier<Stream<String>> nums = () -> teachers.stream().filter(x -> x != ed).map(Teacher::getEmployeeNumber);
        Form f = new Form("Teacher");
        personFields(f, ed);
        memberFields(f, ed);
        f.step("Teacher details")
                .add(uf("Employee number", "String | unique | 4-30 chars", uTxt("Employee number", ID_MIN, ID_MAX, nums)))
                .add(tf("Specialization", "String | 2-120 chars", txt("Specialization", 2, 120)))
                .add(tf("Hire date", "LocalDate | YYYY-MM-DD | not future", date("Hire date", NOT_FUTURE)))
                .add(tf("Salary", "BigDecimal | > 0, e.g. 850000.00", money("Salary")))
                .add(tf("Office number", "String | 1-20 chars", txt("Office number", 1, 20)))
                .add(cf("Employment type", "FULL_TIME", "PART_TIME", "CONTRACT"));
        f.maker = v -> new Teacher(s(v[0]), s(v[1]), s(v[2]), s(v[3]), s(v[4]), dt(v[5]),
                s(v[6]), (Department) v[7], dt(v[8]), s(v[9]), s(v[10]), s(v[11]),
                s(v[12]), s(v[13]), dt(v[14]), (BigDecimal) v[15], s(v[16]), s(v[17]));
        return f;
    }

    private static Form administratorForm(Administrator ed) {
        Supplier<Stream<String>> nums = () -> admins.stream().filter(x -> x != ed).map(Administrator::getStaffNumber);
        Form f = new Form("Administrator");
        personFields(f, ed);
        f.step("Administrator details")
                .add(uf("Staff number", "String | unique | 4-30 chars", uTxt("Staff number", ID_MIN, ID_MAX, nums)))
                .add(tf("Job title", "String | 2-60 chars", txt("Job title", 2, 60)))
                .add(tf("Assigned office", "String | 1-30 chars", txt("Assigned office", 1, 30)))
                .add(tf("Employment date", "LocalDate | YYYY-MM-DD | not future", date("Employment date", NOT_FUTURE)))
                .add(tf("Access level", "int | 1-5", intR("Access level", 1, 5)))
                .add(cf("Active", "YES", "NO"));
        f.maker = v -> new Administrator(s(v[0]), s(v[1]), s(v[2]), s(v[3]), s(v[4]), dt(v[5]),
                s(v[6]), s(v[7]), s(v[8]), dt(v[9]), (Integer) v[10], "YES".equals(v[11]));
        return f;
    }

    private static Form courseForm(Course ed) {
        Supplier<Stream<String>> ids = () -> courses.stream().filter(x -> x != ed).map(Course::getCourseId);
        Supplier<Stream<String>> codes = () -> courses.stream().filter(x -> x != ed).map(Course::getCourseCode);
        Form f = new Form("Course");
        f.step("Course identity")
                .add(uf("Course ID", "String | unique | 4-30 chars", uTxt("Course ID", ID_MIN, ID_MAX, ids)))
                .add(uf("Course code", "String | unique | 2-20 chars", uTxt("Course code", 2, 20, codes)))
                .add(tf("Course title", "String | 2-120 chars", txt("Course title", 2, 120)))
                .step("Course relations")
                .add(tf("Credit hours", "int | 1-6", intR("Credit hours", 1, 6)))
                .add(sf("Department", "Department", () -> departments, Main::registerDepartment))
                .add(sf("Assigned teacher", "Teacher", () -> teachers, Main::registerTeacher));
        f.maker = v -> new Course(s(v[0]), s(v[1]), s(v[2]), (Integer) v[3], (Department) v[4], (Teacher) v[5]);
        return f;
    }

    private static Form classroomForm(Classroom ed) {
        Supplier<Stream<String>> ids = () -> classrooms.stream().filter(x -> x != ed).map(Classroom::getClassroomId);
        Form f = new Form("Classroom");
        f.step("Location")
                .add(uf("Classroom ID", "String | unique | 4-30 chars", uTxt("Classroom ID", ID_MIN, ID_MAX, ids)))
                .add(tf("Building name", "String | 2-60 chars", txt("Building name", 2, 60)))
                .add(tf("Room number", "String | 1-20 chars", txt("Room number", 1, 20)))
                .step("Capacity")
                .add(tf("Capacity", "int | greater than 0", in -> {
                    int c = Integer.parseInt(in.trim());
                    if (c <= 0) throw new ValidationException("Capacity must be greater than zero.");
                    return c;
                }))
                .add(tf("Floor number", "int | 0 or greater", intR("Floor number", 0, 200)))
                .add(cf("Available", "YES", "NO"));
        f.maker = v -> new Classroom(s(v[0]), s(v[1]), s(v[2]), (Integer) v[3], (Integer) v[4], "YES".equals(v[5]));
        return f;
    }

    private static Form enrollmentForm(Enrollment ed) {
        Supplier<Stream<String>> ids = () -> enrollments.stream().filter(x -> x != ed).map(Enrollment::getEnrollmentId);
        Form f = new Form("Enrollment");
        f.step("Enrollment identity")
                .add(uf("Enrollment ID", "String | unique | 4-30 chars", uTxt("Enrollment ID", ID_MIN, ID_MAX, ids)))
                .add(sf("Student", "Student", () -> students, Main::registerStudent))
                .add(sf("Course", "Course", () -> courses, Main::registerCourse)
                        .empty("No course exists. Register a course before enrolling a student."))
                .step("Enrollment details")
                .add(tf("Enrollment date", "LocalDate | YYYY-MM-DD | not future", date("Enrollment date", NOT_FUTURE)))
                .add(cf("Semester", "FALL", "SPRING", "SUMMER"))
                .add(cf("Enrollment status", "ACTIVE", "COMPLETED", "DROPPED"));
        f.maker = v -> new Enrollment(s(v[0]), (Student) v[1], (Course) v[2], dt(v[3]), s(v[4]), s(v[5]));
        f.rule = r -> {
            Enrollment e = (Enrollment) r;
            if (!e.isActive()) return;
            for (Enrollment x : enrollments)
                if (x != ed && x.isActive() && x.getStudent() == e.getStudent() && x.getCourse() == e.getCourse()
                        && x.getSemester().equals(e.getSemester()))
                    throw new DuplicateRecordException("This student already has an active enrollment in "
                            + e.getCourse().getCourseCode() + " for " + e.getSemester() + ".");
        };
        return f;
    }

    private static Form assessmentForm(Assessment ed) {
        Supplier<Stream<String>> ids = () -> assessments.stream().filter(x -> x != ed).map(Assessment::getAssessmentId);
        Form f = new Form("Assessment");
        f.step("Assessment identity")
                .add(uf("Assessment ID", "String | unique | 4-30 chars", uTxt("Assessment ID", ID_MIN, ID_MAX, ids)))
                .add(sf("Enrollment", "Enrollment", () -> enrollments.stream().filter(Enrollment::isActive).toList(),
                        Main::enrollStudent)
                        .empty("No active enrollment exists. Register an enrollment before recording an assessment."))
                .add(cf("Assessment type", "QUIZ", "ASSIGNMENT", "MIDTERM", "FINAL_EXAM", "PROJECT"))
                .step("Scores")
                .add(tf("Maximum score", "double | greater than 0", in -> {
                    double d = Double.parseDouble(in.trim());
                    if (!(d > 0) || Double.isInfinite(d)) throw new ValidationException("Maximum score must be greater than zero.");
                    return d;
                }))
                .add(tf("Obtained score", "double | 0 to maximum score", in -> {
                    double d = Double.parseDouble(in.trim());
                    double max = cur[3] == null ? Double.MAX_VALUE : (Double) cur[3];
                    if (Double.isNaN(d) || d < 0 || d > max)
                        throw new ValidationException("Obtained score must be between 0 and the maximum score ("
                                + (cur[3] == null ? "?" : cur[3]) + ").");
                    return d;
                }))
                .add(tf("Assessment date", "LocalDate | YYYY-MM-DD | not future", date("Assessment date", NOT_FUTURE)));
        f.maker = v -> new Assessment(s(v[0]), (Enrollment) v[1], s(v[2]), (Double) v[3], (Double) v[4], dt(v[5]));
        f.rule = r -> {
            if (!((Assessment) r).getEnrollment().isActive())
                throw new ValidationException("Only an active enrollment can be assessed.");
        };
        return f;
    }

    private static Form paymentForm(FeePayment ed) {
        Supplier<Stream<String>> ids = () -> payments.stream().filter(x -> x != ed).map(FeePayment::getPaymentId);
        Supplier<Stream<String>> receipts = () -> payments.stream().filter(x -> x != ed).map(FeePayment::getReceiptNumber);
        Form f = new Form("Fee payment");
        f.step("Payment identity")
                .add(uf("Payment ID", "String | unique | 4-30 chars", uTxt("Payment ID", ID_MIN, ID_MAX, ids)))
                .add(sf("Student", "Student", () -> students, Main::registerStudent))
                .add(tf("Amount", "BigDecimal | > 0, e.g. 250000.00", money("Amount")))
                .step("Payment details")
                .add(tf("Payment date", "LocalDate | YYYY-MM-DD | not future", date("Payment date", NOT_FUTURE)))
                .add(cf("Payment method", "CASH", "MOBILE_MONEY", "BANK_TRANSFER", "CARD"))
                .add(uf("Receipt number", "String | unique | 1-30 chars", uTxt("Receipt number", 1, 30, receipts)));
        f.maker = v -> new FeePayment(s(v[0]), (Student) v[1], (BigDecimal) v[2], dt(v[3]), s(v[4]), s(v[5]));
        return f;
    }

    private static Form bookForm(LibraryBook ed) {
        Supplier<Stream<String>> ids = () -> books.stream().filter(x -> x != ed).map(LibraryBook::getBookId);
        Supplier<Stream<String>> isbns = () -> books.stream().filter(x -> x != ed).map(LibraryBook::getIsbn);
        Form f = new Form("Library book");
        f.step("Book identity")
                .add(uf("Book ID", "String | unique | 4-30 chars", uTxt("Book ID", ID_MIN, ID_MAX, ids)))
                .add(uf("ISBN", "String | unique | 10 or 13 digits", in -> {
                    String t = in.trim();
                    if (!t.replace("-", "").matches("\\d{10}|\\d{13}"))
                        throw new ValidationException("ISBN must contain 10 or 13 digits (hyphens allowed).");
                    dupCheck("ISBN", t, isbns);
                    return t;
                }))
                .add(tf("Title", "String | 1-120 chars", txt("Title", 1, 120)))
                .step("Authorship and stock")
                .add(tf("Author", "String | 2-120 chars", txt("Author", 2, 120)))
                .add(tf("Total copies", "int | 0 or greater", intR("Total copies", 0, 100000)))
                .add(tf("Available copies", "int | 0 to total copies", in -> {
                    int a = Integer.parseInt(in.trim());
                    int total = cur[4] == null ? Integer.MAX_VALUE : (Integer) cur[4];
                    if (a < 0 || a > total)
                        throw new ValidationException("Available copies must be between 0 and total copies (" + total + ").");
                    return a;
                }));
        f.maker = v -> new LibraryBook(s(v[0]), s(v[1]), s(v[2]), s(v[3]), (Integer) v[4], (Integer) v[5]);
        return f;
    }

    private static Form loanForm() {
        Supplier<Stream<String>> ids = () -> loans.stream().map(BookLoan::getLoanId);
        Form f = new Form("Book loan");
        f.step("Loan identity")
                .add(uf("Loan ID", "String | unique | 4-30 chars", uTxt("Loan ID", ID_MIN, ID_MAX, ids)))
                .add(sf("Student", "Student", () -> students, Main::registerStudent))
                .add(sf("Library book", "Library book", () -> books, Main::registerBook)
                        .describe(o -> {
                            LibraryBook b = (LibraryBook) o;
                            return "[" + b.getBookId() + "] " + b.getTitle() + " - " + b.getAvailableCopies()
                                    + "/" + b.getTotalCopies() + " available";
                        })
                        .check(o -> {
                            LibraryBook b = (LibraryBook) o;
                            if (b.getAvailableCopies() <= 0)
                                throw new BookUnavailableException("No copies of '" + b.getTitle() + "' are available right now.");
                        }))
                .step("Loan dates")
                .add(tf("Borrowed date", "LocalDate | YYYY-MM-DD | not future", date("Borrowed date", NOT_FUTURE)))
                .add(tf("Due date", "LocalDate | YYYY-MM-DD | after borrowed date", in -> {
                    LocalDate d = LocalDate.parse(in.trim());
                    LocalDate b = (LocalDate) cur[3];
                    if (b != null && !d.isAfter(b)) throw new ValidationException("Due date must be after the borrowed date (" + b + ").");
                    return d;
                }))
                .add(df("Returned date", "(not returned yet)"));
        f.maker = v -> new BookLoan(s(v[0]), (Student) v[1], (LibraryBook) v[2], dt(v[3]), dt(v[4]), null);
        f.rule = r -> {
            LibraryBook b = ((BookLoan) r).getBook();
            if (b.getAvailableCopies() <= 0)
                throw new BookUnavailableException("No copies of '" + b.getTitle() + "' are available right now.");
        };
        return f;
    }

    // ------------------------------------------------------------------ registration entry points
    private static Department registerDepartment() { return (Department) departmentForm(null).run(null, r -> departments.add((Department) r)); }
    private static Student registerStudent() { return (Student) studentForm(null).run(null, r -> students.add((Student) r)); }
    private static Teacher registerTeacher() { return (Teacher) teacherForm(null).run(null, r -> teachers.add((Teacher) r)); }
    private static Administrator registerAdministrator() { return (Administrator) administratorForm(null).run(null, r -> admins.add((Administrator) r)); }
    private static Course registerCourse() { return (Course) courseForm(null).run(null, r -> courses.add((Course) r)); }
    private static Classroom registerClassroom() { return (Classroom) classroomForm(null).run(null, r -> classrooms.add((Classroom) r)); }
    private static Enrollment enrollStudent() { return (Enrollment) enrollmentForm(null).run(null, r -> enrollments.add((Enrollment) r)); }
    private static Assessment recordAssessment() { return (Assessment) assessmentForm(null).run(null, r -> assessments.add((Assessment) r)); }
    private static FeePayment recordPayment() { return (FeePayment) paymentForm(null).run(null, r -> payments.add((FeePayment) r)); }
    private static LibraryBook registerBook() { return (LibraryBook) bookForm(null).run(null, r -> books.add((LibraryBook) r)); }

    /** Borrowing: stock is decremented exactly once, only when the loan is really saved. */
    private static BookLoan borrowBook() {
        return (BookLoan) loanForm().run(null, r -> {
            BookLoan loan = (BookLoan) r;
            LibraryBook b = loan.getBook();
            b.setAvailableCopies(b.getAvailableCopies() - 1);
            loans.add(loan);
            System.out.println("  Stock updated: " + b.getTitle() + " now has " + b.getAvailableCopies() + " available copies.");
        });
    }

    /** Return: updates the EXISTING loan (no second loan) and increments stock exactly once. */
    private static void returnBook() {
        List<BookLoan> active = loans.stream().filter(BookLoan::isActive).toList();
        if (active.isEmpty())
            throw new PrerequisiteMissingException("No active loan exists. Borrow a library book before returning one.");
        System.out.println("\n--- RETURN LIBRARY BOOK ---");
        for (int i = 0; i < active.size(); i++) System.out.println("  " + (i + 1) + ". " + active.get(i).getRegistrationSummary());
        System.out.println("  0. Cancel");
        BookLoan loan = null;
        while (loan == null) {
            String in = readLine("Select loan to return: ").trim();
            try {
                int k = Integer.parseInt(in);
                if (k == 0) { System.out.println("Return cancelled. Nothing was changed."); return; }
                if (k >= 1 && k <= active.size()) loan = active.get(k - 1);
                else System.out.println("! Pick one of the numbers shown.");
            } catch (NumberFormatException e) {
                System.out.println("! Please enter a number.");
            }
        }
        LocalDate returned = null;
        while (returned == null) {
            String in = readLine("Return date [LocalDate | YYYY-MM-DD | not before " + loan.getBorrowedDate() + "] (or CANCEL): ").trim();
            if (in.equalsIgnoreCase("CANCEL")) { System.out.println("Return cancelled. Nothing was changed."); return; }
            try {
                LocalDate d = LocalDate.parse(in);
                Registrable.notFuture(d, "Return date");
                if (d.isBefore(loan.getBorrowedDate()))
                    throw new ValidationException("Returned date cannot be before the borrowed date (" + loan.getBorrowedDate() + ").");
                returned = d;
            } catch (DateTimeParseException e) {
                System.out.println("! Invalid date: use YYYY-MM-DD (example: 2026-11-08).");
            } catch (ValidationException e) {
                System.out.println("! " + e.getMessage());
            }
        }
        if (!confirm("Confirm return of '" + loan.getBook().getTitle() + "' by " + loan.getStudent().getDisplayName() + "?")) {
            System.out.println("Return cancelled. Nothing was changed.");
            return;
        }
        LibraryBook b = loan.getBook();
        if (b.getAvailableCopies() >= b.getTotalCopies())
            throw new ValidationException("Stock error: every copy of this book is already in the library.");
        loan.setReturnedDate(returned);
        b.setAvailableCopies(b.getAvailableCopies() + 1);
        System.out.println("+ Loan " + loan.getLoanId() + " returned on " + returned + ". "
                + b.getTitle() + " now has " + b.getAvailableCopies() + "/" + b.getTotalCopies() + " copies available.");
    }

    // ------------------------------------------------------------------ view / search / update
    private static void section(String title, List<? extends Registrable> list) {
        System.out.println("\n== " + title + " (" + list.size() + ") ==");
        if (list.isEmpty()) System.out.println("  (no records)");
        for (Registrable r : list) System.out.println("  " + r.getRegistrationSummary());
    }

    private static void viewAllRecords() {
        section("DEPARTMENTS", departments);
        section("STUDENTS", students);
        section("TEACHERS", teachers);
        section("ADMINISTRATORS", admins);
        section("COURSES", courses);
        section("CLASSROOMS", classrooms);
        section("ENROLLMENTS", enrollments);
        section("ASSESSMENTS", assessments);
        section("FEE PAYMENTS", payments);
        section("LIBRARY BOOKS", books);
        section("BOOK LOANS", loans);
        readLine("\nPress ENTER to return to the main menu...");
    }

    private static void searchById() {
        String id = readLine("Enter the ID to search for (or ENTER to go back): ").trim();
        if (id.isEmpty()) return;
        List<Identifiable> found = allRecords().stream().filter(x -> x.getId().equalsIgnoreCase(id)).toList();
        if (found.isEmpty()) throw new RecordNotFoundException("No record found with ID '" + id + "'.");
        for (Identifiable x : found) {
            System.out.println("\nFound " + x.getClass().getSimpleName() + ":");
            ((Registrable) x).printDetails();
        }
    }

    private static Object[] valuesOf(Registrable r) {
        if (r instanceof Student s)
            return new Object[]{s.getPersonId(), s.getFirstName(), s.getLastName(), s.getEmail(), s.getPhoneNumber(), s.getDateOfBirth(),
                s.getMemberNumber(), s.getDepartment(), s.getJoinDate(), s.getAcademicStatus(), s.getInstitutionalEmail(), s.getAdvisorName(),
                s.getStudentNumber(), s.getProgramName(), s.getAdmissionDate(), s.getStudyYear(), s.getGuardianPhone(), s.isScholarshipStatus() ? "YES" : "NO"};
        if (r instanceof Teacher t)
            return new Object[]{t.getPersonId(), t.getFirstName(), t.getLastName(), t.getEmail(), t.getPhoneNumber(), t.getDateOfBirth(),
                t.getMemberNumber(), t.getDepartment(), t.getJoinDate(), t.getAcademicStatus(), t.getInstitutionalEmail(), t.getAdvisorName(),
                t.getEmployeeNumber(), t.getSpecialization(), t.getHireDate(), t.getSalary(), t.getOfficeNumber(), t.getEmploymentType()};
        if (r instanceof Administrator a)
            return new Object[]{a.getPersonId(), a.getFirstName(), a.getLastName(), a.getEmail(), a.getPhoneNumber(), a.getDateOfBirth(),
                a.getStaffNumber(), a.getJobTitle(), a.getAssignedOffice(), a.getEmploymentDate(), a.getAccessLevel(), a.isActive() ? "YES" : "NO"};
        if (r instanceof Department d)
            return new Object[]{d.getDepartmentId(), d.getDepartmentName(), d.getDepartmentCode(), d.getOfficeLocation(), d.getContactEmail(), d.getHeadTeacher()};
        if (r instanceof Course c)
            return new Object[]{c.getCourseId(), c.getCourseCode(), c.getCourseTitle(), c.getCreditHours(), c.getDepartment(), c.getAssignedTeacher()};
        if (r instanceof Classroom c)
            return new Object[]{c.getClassroomId(), c.getBuildingName(), c.getRoomNumber(), c.getCapacity(), c.getFloorNumber(), c.isAvailable() ? "YES" : "NO"};
        if (r instanceof Enrollment e)
            return new Object[]{e.getEnrollmentId(), e.getStudent(), e.getCourse(), e.getEnrollmentDate(), e.getSemester(), e.getEnrollmentStatus()};
        if (r instanceof Assessment a)
            return new Object[]{a.getAssessmentId(), a.getEnrollment(), a.getAssessmentType(), a.getMaximumScore(), a.getObtainedScore(), a.getAssessmentDate()};
        if (r instanceof FeePayment p)
            return new Object[]{p.getPaymentId(), p.getStudent(), p.getAmount(), p.getPaymentDate(), p.getPaymentMethod(), p.getReceiptNumber()};
        if (r instanceof LibraryBook b)
            return new Object[]{b.getBookId(), b.getIsbn(), b.getTitle(), b.getAuthor(), b.getTotalCopies(), b.getAvailableCopies()};
        throw new ValidationException("This record type cannot be edited here.");
    }

    private static void updateRecord() {
        String[] types = {"Department", "Student", "Teacher", "Administrator", "Course", "Classroom",
            "Enrollment", "Assessment", "Fee payment", "Library book"};
        System.out.println("\n--- UPDATE EXISTING RECORD ---");
        for (int i = 0; i < types.length; i++) System.out.println("  " + (i + 1) + ". " + types[i]);
        System.out.println("  0. Cancel   (book loans change only through Borrow / Return so stock stays correct)");
        int k;
        try {
            k = Integer.parseInt(readLine("Select record type: ").trim());
        } catch (NumberFormatException e) {
            System.out.println("! Please enter a number.");
            return;
        }
        if (k == 0) return;
        List<? extends Registrable> list = switch (k) {
            case 1 -> departments; case 2 -> students; case 3 -> teachers; case 4 -> admins; case 5 -> courses;
            case 6 -> classrooms; case 7 -> enrollments; case 8 -> assessments; case 9 -> payments; case 10 -> books;
            default -> null;
        };
        if (list == null) { System.out.println("! Invalid record type."); return; }
        if (list.isEmpty()) { System.out.println("No " + types[k - 1].toLowerCase() + " records exist yet. Nothing to update."); return; }
        for (int i = 0; i < list.size(); i++) System.out.println("  " + (i + 1) + ". " + list.get(i).getRegistrationSummary());
        System.out.println("  0. Cancel");
        int n;
        try {
            n = Integer.parseInt(readLine("Select record to update: ").trim());
        } catch (NumberFormatException e) {
            System.out.println("! Please enter a number.");
            return;
        }
        if (n == 0) return;
        if (n < 1 || n > list.size()) throw new RecordNotFoundException("No record is listed under number " + n + ".");
        Registrable rec = list.get(n - 1);
        Form form = switch (k) {
            case 1 -> departmentForm((Department) rec);
            case 2 -> studentForm((Student) rec);
            case 3 -> teacherForm((Teacher) rec);
            case 4 -> administratorForm((Administrator) rec);
            case 5 -> courseForm((Course) rec);
            case 6 -> classroomForm((Classroom) rec);
            case 7 -> enrollmentForm((Enrollment) rec);
            case 8 -> assessmentForm((Assessment) rec);
            case 9 -> paymentForm((FeePayment) rec);
            default -> bookForm((LibraryBook) rec);
        };
        form.run(valuesOf(rec), updated -> rec.updateFrom(updated));
    }

    // ------------------------------------------------------------------ reports
    private static <T extends Registrable> T pick(String noun, List<T> list, Runnable register, String purpose) {
        while (true) {
            if (list.isEmpty()) {
                System.out.println("No " + noun + " exists. Register a " + noun + " before " + purpose + ".");
                System.out.println("  1 Register prerequisite now   2 Return to menu");
                String c = readLine("Select: ").trim();
                if (c.equals("1")) { register.run(); continue; }
                if (c.equals("2")) return null;
                System.out.println("! Enter 1 or 2.");
                continue;
            }
            System.out.println("Select " + noun + ":");
            for (int i = 0; i < list.size(); i++) System.out.println("  " + (i + 1) + ". " + label(list.get(i)));
            System.out.println("  0. Cancel");
            try {
                int k = Integer.parseInt(readLine("Select: ").trim());
                if (k == 0) return null;
                if (k >= 1 && k <= list.size()) return list.get(k - 1);
            } catch (NumberFormatException e) {
                // handled by the message below
            }
            System.out.println("! Invalid selection. Pick one of the numbers shown.");
        }
    }

    private static void studentSummary() {
        Student st = pick("student", students, Main::registerStudent, "viewing an academic summary");
        if (st == null) return;
        System.out.println("\n===== ACADEMIC SUMMARY: " + st.getDisplayName() + " =====");
        System.out.println("Program: " + st.getProgramName() + " | Year " + st.getStudyYear() + " | Department: "
                + st.getDepartment().getDepartmentName() + " | Status: " + st.getAcademicStatus()
                + " | Scholarship: " + (st.isScholarshipStatus() ? "YES" : "NO"));
        List<Enrollment> mine = enrollments.stream().filter(e -> e.getStudent() == st).toList();
        if (mine.isEmpty()) System.out.println("No enrollments recorded for this student.");
        double sumPct = 0;
        int count = 0;
        for (Enrollment e : mine) {
            System.out.println("\n" + e.getCourse().getCourseCode() + " - " + e.getCourse().getCourseTitle()
                    + " | " + e.getSemester() + " | " + e.getEnrollmentStatus() + " | Teacher: "
                    + e.getCourse().getAssignedTeacher().getDisplayName());
            List<Assessment> as = assessments.stream().filter(a -> a.getEnrollment() == e).toList();
            if (as.isEmpty()) System.out.println("   (no assessments yet)");
            for (Assessment a : as) {
                double pct = a.getObtainedScore() * 100.0 / a.getMaximumScore();
                sumPct += pct;
                count++;
                System.out.println(String.format("   %-11s %6.1f / %-6.1f  %5.1f%%  (%s)", a.getAssessmentType(),
                        a.getObtainedScore(), a.getMaximumScore(), pct, a.getAssessmentDate()));
            }
        }
        if (count > 0) System.out.println(String.format("%nOverall average across %d assessment(s): %.1f%%", count, sumPct / count));
        readLine("\nPress ENTER to return to the main menu...");
    }

    private static void paymentHistory() {
        Student st = pick("student", students, Main::registerStudent, "viewing a payment history");
        if (st == null) return;
        List<FeePayment> mine = payments.stream().filter(p -> p.getStudent() == st)
                .sorted((a, b) -> a.getPaymentDate().compareTo(b.getPaymentDate())).toList();
        System.out.println("\n===== PAYMENT HISTORY: " + st.getDisplayName() + " =====");
        if (mine.isEmpty()) System.out.println("No payments recorded for this student.");
        BigDecimal total = BigDecimal.ZERO;
        for (FeePayment p : mine) {
            System.out.println("  " + p.getPaymentDate() + " | " + p.getAmount() + " | " + p.getPaymentMethod()
                    + " | Receipt " + p.getReceiptNumber());
            total = total.add(p.getAmount());
        }
        if (!mine.isEmpty()) System.out.println("Total paid: " + total + " (" + mine.size() + " payment(s))");
        readLine("\nPress ENTER to return to the main menu...");
    }

    private static void availableBooks() {
        List<LibraryBook> avail = books.stream().filter(b -> b.getAvailableCopies() > 0).toList();
        System.out.println("\n===== AVAILABLE BOOKS =====");
        if (avail.isEmpty()) System.out.println("No books are currently available (register books or wait for returns).");
        for (LibraryBook b : avail) System.out.println("  " + b.getRegistrationSummary());
        readLine("\nPress ENTER to return to the main menu...");
    }

    // ------------------------------------------------------------------ polymorphism demonstration
    private static void demonstratePolymorphism() {
        System.out.println("\n===== RUNTIME POLYMORPHISM DEMONSTRATION =====");
        List<Person> people = people();                       // Student, Teacher, Administrator in ONE list
        System.out.println("\n1) List<Person> - one call, different overridden behaviour per runtime type:");
        if (people.isEmpty()) System.out.println("   (no Student/Teacher/Administrator registered yet - register some first)");
        for (Person p : people) {
            String category = (p instanceof AcademicMember am) ? " | category: " + am.getAcademicCategory() : "";
            System.out.println("   " + p.getClass().getSimpleName() + " -> getRoleDescription(): " + p.getRoleDescription()
                    + category + " | active: " + p.isActive());
            System.out.println("      getRegistrationSummary(): " + p.getRegistrationSummary());
        }
        List<Identifiable> all = allRecords();                // unrelated concrete entities via the interface
        System.out.println("\n2) List<Identifiable> - unrelated classes used through one interface:");
        if (all.isEmpty()) System.out.println("   (no records registered yet)");
        for (Identifiable x : all)
            System.out.println("   " + String.format("%-14s", x.getClass().getSimpleName()) + " id=" + x.getId()
                    + " | " + x.getDisplayName() + " | active=" + x.isActive());
        System.out.println("\n3) Design notes for the examiner:");
        System.out.println("   - Multilevel inheritance : Student/Teacher -> AcademicMember -> Person");
        System.out.println("   - Hierarchical           : Student, Teacher (and Administrator) share the Person parent");
        System.out.println("   - Constructor chaining   : new Student(..) calls super(..) in AcademicMember, which calls super(..) in Person");
        System.out.println("   - Overriding             : getRoleDescription(), getDisplayName(), getRegistrationSummary(), validate()");
        System.out.println("   - Person and AcademicMember are abstract: they declare abstract methods (getRoleDescription,");
        System.out.println("     getAcademicCategory) with no body, so 'new Person(..)' would create an incomplete object;");
        System.out.println("     the compiler therefore forbids it. Only concrete subclasses can be instantiated.");
        readLine("\nPress ENTER to return to the main menu...");
    }
}
