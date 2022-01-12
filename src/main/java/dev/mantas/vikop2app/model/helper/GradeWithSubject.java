package dev.mantas.vikop2app.model.helper;

import dev.mantas.vikop2app.model.Grade;
import dev.mantas.vikop2app.model.Subject;

import java.util.Objects;

public class GradeWithSubject {

    private Grade grade;
    private Subject subject;

    public GradeWithSubject(Grade grade, Subject subject) {
        this.grade = grade;
        this.subject = subject;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GradeWithSubject that = (GradeWithSubject) o;
        return Objects.equals(grade, that.grade) && Objects.equals(subject, that.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grade, subject);
    }

}
