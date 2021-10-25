package ru.victormalkov.reportchecker.service;

public class DriveFile {
    private String name;
    private String id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DriveFile driveFile = (DriveFile) o;

        return id != null ? id.equals(driveFile.id) : driveFile.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public DriveFile(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return getName();
    }
}
