package dev.mantas.vikop2app.model.helper;

import dev.mantas.vikop2app.model.Student;
import dev.mantas.vikop2app.model.StudentGroup;

public class StudentWithStudentGroup {

    private Student student;
    private StudentGroup studentGroup;

    public StudentWithStudentGroup(Student student, StudentGroup studentGroup) {
        this.student = student;
        this.studentGroup = studentGroup;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public StudentGroup getStudentGroup() {
        return studentGroup;
    }

    public void setStudentGroup(StudentGroup studentGroup) {
        this.studentGroup = studentGroup;
    }

}
