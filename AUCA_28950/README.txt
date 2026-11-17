AUCA_ID - School Management System (console, Java 17+)

Compile (from this folder):
  Linux/macOS:  mkdir -p out && javac -d out $(find src -name "*.java")
  Windows:      dir /s /b src\*.java > sources.txt  &&  javac -d out @sources.txt
Run:
  java -cp out com.auca.main.Main

Package tree: com.auca.interfaces (Identifiable, Registrable), com.auca.abstracts (Person, AcademicMember),
one package per concrete class, com.auca.exceptions (5 custom exceptions), com.auca.main (Main).
