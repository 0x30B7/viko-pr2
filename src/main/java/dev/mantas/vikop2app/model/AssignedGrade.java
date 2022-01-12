package dev.mantas.vikop2app.model;

import java.util.Date;
import java.util.Objects;

public class AssignedGrade {

    private int id;
    private Date createdAt;
    private int gradeId;
    private long studentId;
    private int value;

    public AssignedGrade(int id, Date createdAt, int gradeId, long studentId, int value) {
        this.id = id;
        this.createdAt = createdAt;
        this.gradeId = gradeId;
        this.studentId = studentId;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignedGrade that = (AssignedGrade) o;
        return gradeId == that.gradeId && studentId == that.studentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studentId);
    }

}
