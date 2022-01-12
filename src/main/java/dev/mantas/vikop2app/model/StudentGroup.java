package dev.mantas.vikop2app.model;

import java.util.Date;
import java.util.Objects;

public class StudentGroup {

    private final int id;
    private final Date createdAt;
    private String title;

    public StudentGroup(int id, Date createdAt, String title) {
        this.id = id;
        this.createdAt = createdAt;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentGroup that = (StudentGroup) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
