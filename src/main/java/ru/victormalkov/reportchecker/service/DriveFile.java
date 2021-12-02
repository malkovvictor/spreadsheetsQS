package ru.victormalkov.reportchecker.service;

import java.util.Objects;

public class DriveFile {
    private final String name;
    private final String id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DriveFile driveFile = (DriveFile) o;

        return Objects.equals(id, driveFile.id);
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
