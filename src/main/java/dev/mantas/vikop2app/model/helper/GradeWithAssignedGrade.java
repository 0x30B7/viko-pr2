package dev.mantas.vikop2app.model.helper;

import dev.mantas.vikop2app.model.AssignedGrade;
import dev.mantas.vikop2app.model.Grade;

import java.util.Objects;

public class GradeWithAssignedGrade {

    private Grade grade;
    private AssignedGrade assignedGrade;

    public GradeWithAssignedGrade(Grade grade, AssignedGrade assignedGrade) {
        this.grade = grade;
        this.assignedGrade = assignedGrade;
    }

    public boolean hasGrade() {
        return grade != null;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public boolean hasAssignedGrade() {
        return assignedGrade != null;
    }

    public AssignedGrade getAssignedGrade() {
        return assignedGrade;
    }

    public void setAssignedGrade(AssignedGrade assignedGrade) {
        this.assignedGrade = assignedGrade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GradeWithAssignedGrade that = (GradeWithAssignedGrade) o;
        return Objects.equals(grade, that.grade) && Objects.equals(assignedGrade, that.assignedGrade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grade, assignedGrade);
    }

}
