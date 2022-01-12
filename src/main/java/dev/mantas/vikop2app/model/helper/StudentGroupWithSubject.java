package dev.mantas.vikop2app.model.helper;

import dev.mantas.vikop2app.model.StudentGroup;
import dev.mantas.vikop2app.model.Subject;

public class StudentGroupWithSubject {

    private StudentGroup studentGroup;
    private Subject subject;

    public StudentGroupWithSubject(StudentGroup studentGroup, Subject subject) {
        this.studentGroup = studentGroup;
        this.subject = subject;
    }

    public StudentGroup getStudentGroup() {
        return studentGroup;
    }

    public Subject getSubject() {
        return subject;
    }

}
