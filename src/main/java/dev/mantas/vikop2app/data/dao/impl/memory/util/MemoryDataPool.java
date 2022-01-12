package dev.mantas.vikop2app.data.dao.impl.memory.util;

import dev.mantas.vikop2app.model.Admin;
import dev.mantas.vikop2app.model.AssignedGrade;
import dev.mantas.vikop2app.model.Grade;
import dev.mantas.vikop2app.model.LecturerStatus;
import dev.mantas.vikop2app.model.Student;
import dev.mantas.vikop2app.model.StudentGroup;
import dev.mantas.vikop2app.model.Subject;
import dev.mantas.vikop2app.model.Teacher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryDataPool {

    public static MemoryDataPool instance;

    private final AtomicInteger idGenerator = new AtomicInteger(100);
    private final List<LecturerStatus> lecturerStatuses = new ArrayList<>();
    private final List<Admin> admins = new ArrayList<>();
    private final List<Student> students = new ArrayList<>();
    private final List<StudentGroup> studentGroups = new ArrayList<>();
    private final Map<Long, Integer> studentToStudentGroupMapping = new HashMap<>();
    private final List<Subject> subjects = new ArrayList<>();
    private final Map<Long, Integer> teacherToSubjectMapping = new HashMap<>();
    private final List<Teacher> teachers = new ArrayList<>();
    private final Set<StudentGroupToSubjectMapping> studentGroupToSubjectMapping = new HashSet<>();
    private final List<Grade> grades = new ArrayList<>();
    private final List<AssignedGrade> assignedGrades = new ArrayList<>();

    public MemoryDataPool() {
        instance = this;

        admins.addAll(Arrays.asList(
                new Admin(1, "vardenis", "pavardenis", Date.from(Instant.now()))
        ));

        lecturerStatuses.addAll(Arrays.asList(
                new LecturerStatus(0, "Asistentas", "Asist."),
                new LecturerStatus(1, "Lektorius", "Lekt."),
                new LecturerStatus(2, "Docentas", "Doc."),
                new LecturerStatus(3, "Profesorius", "Prof.")
        ));

    }

    public void populate() {
        students.addAll(Arrays.asList(
                new Student(1, Date.from(Instant.now()), "aidas", "ramaneckas"),
                new Student(2, Date.from(Instant.now()), "jolita", "augaite"),
                new Student(3, Date.from(Instant.now()), "arnas", "peškevičius"),
                new Student(4, Date.from(Instant.now()), "žemyna", "počiūtė"),
                new Student(5, Date.from(Instant.now()), "vytautas", "zimnickas")
        ));

        studentGroups.addAll(Arrays.asList(
                new StudentGroup(1, Date.from(Instant.now()), "PI20A"),
                new StudentGroup(2, Date.from(Instant.now()), "PI20B")
        ));

        studentToStudentGroupMapping.putAll(Map.of(
                1L, 1,
                2L, 1,
                3L, 1,
                4L, 2,
                5L, 2
        ));

        subjects.addAll(Arrays.asList(
                new Subject(1, "Informacinės sistemos"),
                new Subject(2, "Duomenų bazių projektavimas")
        ));

        studentGroupToSubjectMapping.addAll(Arrays.asList(
                new StudentGroupToSubjectMapping(1, 1),
                new StudentGroupToSubjectMapping(1, 2),
                new StudentGroupToSubjectMapping(2, 1)
        ));

        teachers.addAll(Arrays.asList(
                new Teacher(1, Date.from(Instant.now()), "tautvydas", "adomėnas",
                        new LecturerStatus(0, "Asistentas", "Asist."), "tadomenas@viko.lt", 203),
                new Teacher(2, Date.from(Instant.now()), "ignas", "brazinskis",
                        new LecturerStatus(2, "Docentas", "Doc."), "ibrazinskis@viko.lt", 208),
                new Teacher(3, Date.from(Instant.now()), "lukas", "simonaitis",
                        new LecturerStatus(2, "Docentas", "Doc."), "lsimonaitis@viko.lt", 312)
        ));

        teacherToSubjectMapping.putAll(Map.of(
                1L, 1,
                2L, 2,
                3L, 2
        ));

        int gradeId = 1;
        for (Subject subject : subjects) {
            grades.addAll(Arrays.asList(
                    new Grade(gradeId++, Date.from(Instant.now()), "Kontrolinis darbas 1", subject.getId()),
                    new Grade(gradeId++, Date.from(Instant.now()), "Kontrolinis darbas 2", subject.getId()),
                    new Grade(gradeId++, Date.from(Instant.now()), "Savarankiškas darbas", subject.getId())
            ));
        }

        int assignedGradeId = 1;
        for (Student student : students) {
            for (Grade grade : grades) {
                if (ThreadLocalRandom.current().nextDouble(1.0D) > 0.2) {
                    assignedGrades.add(new AssignedGrade(assignedGradeId++, Date.from(Instant.now()),
                            grade.getId(), student.getId(), random(1, 10)));
                }
            }
        }
    }

    public AtomicInteger getIdGenerator() {
        return idGenerator;
    }

    public List<LecturerStatus> getLecturerStatuses() {
        return lecturerStatuses;
    }

    public List<Admin> getAdmins() {
        return admins;
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<StudentGroup> getStudentGroups() {
        return studentGroups;
    }

    public Map<Long, Integer> getStudentToStudentGroupMapping() {
        return studentToStudentGroupMapping;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public Map<Long, Integer> getTeacherToSubjectMapping() {
        return teacherToSubjectMapping;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public Set<StudentGroupToSubjectMapping> getStudentGroupToSubjectMapping() {
        return studentGroupToSubjectMapping;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public List<AssignedGrade> getAssignedGrades() {
        return assignedGrades;
    }

    // ======================================================================================================== //

    public static record StudentGroupToSubjectMapping(
            int studentGroupId,
            int subjectId
    ) { }

    // ======================================================================================================== //

    private static int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(max + 1 - min) + min;
    }

}
