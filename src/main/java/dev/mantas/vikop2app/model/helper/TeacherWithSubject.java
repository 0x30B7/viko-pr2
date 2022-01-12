package dev.mantas.vikop2app.model.helper;

import dev.mantas.vikop2app.model.Subject;
import dev.mantas.vikop2app.model.Teacher;

public class TeacherWithSubject {

    private Teacher teacher;
    private Subject subject;

    public TeacherWithSubject(Teacher teacher) {
        this.teacher = teacher;
    }

    public TeacherWithSubject(Teacher teacher, Subject subject) {
        this.teacher = teacher;
        this.subject = subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

}
