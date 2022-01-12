package dev.mantas.vikop2app.model;

import java.util.Date;

public class Teacher extends AbstractUser {

    private LecturerStatus lecturerStatus;
    private String email;
    private Integer roomNumber;

    public Teacher(long id, Date createdAt, String name, String surname,
                   LecturerStatus lecturerStatus, String email, Integer roomNumber) {
        super(id, createdAt, name, surname);
        this.lecturerStatus = lecturerStatus;
        this.email = email;
        this.roomNumber = roomNumber;
    }

    public LecturerStatus getLecturerStatus() {
        return lecturerStatus;
    }

    public void setLecturerStatus(LecturerStatus lecturerStatus) {
        this.lecturerStatus = lecturerStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

}
