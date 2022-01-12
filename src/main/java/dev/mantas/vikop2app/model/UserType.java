package dev.mantas.vikop2app.model;

public enum UserType {

    STUDENT,
    TEACHER,
    ADMIN,
    UNKNOWN;

    public int getId() {
        return ordinal();
    }

    public static UserType getById(Integer id) {
        if (id == null) return UNKNOWN;
        return switch (id) {
            case 0 -> STUDENT;
            case 1 -> TEACHER;
            case 2 -> ADMIN;
            default -> UNKNOWN;
        };
    }

}
