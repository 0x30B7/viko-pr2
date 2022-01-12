package dev.mantas.vikop2app.model;

import java.util.Date;

public class Admin extends AbstractUser {

    public Admin(long id, String name, String lastName, Date createdAt) {
        super(id, createdAt, name, lastName);
    }

}
