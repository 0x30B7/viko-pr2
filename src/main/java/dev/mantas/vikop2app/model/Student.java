package dev.mantas.vikop2app.model;

import java.util.Date;

public class Student extends AbstractUser {

    public Student(long id, Date createdAt, String name, String lastName) {
        super(id, createdAt, name, lastName);
    }

}
