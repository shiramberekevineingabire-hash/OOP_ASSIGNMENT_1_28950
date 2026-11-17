package com.auca.classroom;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

public class Classroom implements Identifiable, Registrable {

    private String classroomId;
    private String buildingName;
    private String roomNumber;
    private int capacity;
    private int floorNumber;
    private boolean available;

    public Classroom(String classroomId, String buildingName, String roomNumber, int capacity, int floorNumber, boolean available) {
        this.classroomId = classroomId;
        this.buildingName = buildingName;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.floorNumber = floorNumber;
        this.available = available;
    }

    @Override public String getId() { return classroomId; }

    public String getClassroomId() { return classroomId; }
    public void setClassroomId(String classroomId) { this.classroomId = classroomId; }
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getFloorNumber() { return floorNumber; }
    public void setFloorNumber(int floorNumber) { this.floorNumber = floorNumber; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public void updateFrom(Registrable source) throws ValidationException {
        if (!(source instanceof Classroom)) throw new ValidationException("Record type mismatch: expected Classroom.");
        Classroom other = (Classroom) source;
        this.classroomId = other.classroomId;
        this.buildingName = other.buildingName;
        this.roomNumber = other.roomNumber;
        this.capacity = other.capacity;
        this.floorNumber = other.floorNumber;
        this.available = other.available;
    }

    @Override
    public void printDetails() {
        System.out.println("--- " + getClass().getSimpleName() + " (entity v" + ENTITY_VERSION + ") ---");
        System.out.println("  Classroom id: " + (classroomId == null ? "(none)" : classroomId));
        System.out.println("  Building name: " + (buildingName == null ? "(none)" : buildingName));
        System.out.println("  Room number: " + (roomNumber == null ? "(none)" : roomNumber));
        System.out.println("  Capacity: " + capacity);
        System.out.println("  Floor number: " + floorNumber);
        System.out.println("  Available: " + available);
    }

    @Override public String toString() { return getRegistrationSummary(); }


    @Override public String getDisplayName() { return buildingName + " " + roomNumber; }
    @Override public boolean isActive() { return available; }

    @Override
    public String getRegistrationSummary() {
        return "Classroom [" + classroomId + "] " + getDisplayName() + " | Capacity " + capacity
                + " | Floor " + floorNumber + " | " + (available ? "AVAILABLE" : "NOT AVAILABLE");
    }

    @Override
    public void validate() throws ValidationException {
        Registrable.id(classroomId, "Classroom ID");
        Registrable.text(buildingName, "Building name", 2, 60);
        Registrable.text(roomNumber, "Room number", 1, 20);
        if (capacity <= 0) throw new ValidationException("Capacity must be greater than zero.");
        if (floorNumber < 0) throw new ValidationException("Floor number must be zero or greater.");
    }
}
