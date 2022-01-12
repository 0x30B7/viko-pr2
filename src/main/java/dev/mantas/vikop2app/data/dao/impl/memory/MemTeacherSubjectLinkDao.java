package dev.mantas.vikop2app.data.dao.impl.memory;

import dev.mantas.vikop2app.data.dao.impl.memory.util.MemoryDataPool;
import dev.mantas.vikop2app.data.dao.type.TeacherSubjectLinkDao;
import dev.mantas.vikop2app.data.source.impl.MemoryDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Subject;
import dev.mantas.vikop2app.model.Teacher;
import dev.mantas.vikop2app.model.helper.TeacherWithSubject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MemTeacherSubjectLinkDao implements TeacherSubjectLinkDao {

    private final MemoryDataSource dataSource;
    private List<Teacher> teachers;
    private List<Subject> subjects;
    private Map<Long, Integer> teacherToSubjectMapping;

    public MemTeacherSubjectLinkDao(MemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MemoryDataPool dataPool = dataSource.provide();
        this.teachers = dataPool.getTeachers();
        this.subjects = dataPool.getSubjects();
        this.teacherToSubjectMapping = dataPool.getTeacherToSubjectMapping();
    }

    @Override
    public List<TeacherWithSubject> getAllTeachersWithOptSubjects() throws DatabaseException {
        return teachers.stream().map(teacher -> {
            Integer subjectId = teacherToSubjectMapping.get(teacher.getId());

            if (subjectId == null) {
                return new TeacherWithSubject(teacher);
            }

            Subject subject = subjects.stream().filter(o -> o.getId() == subjectId).findFirst().orElse(null);
            if (subject == null) {
                return new TeacherWithSubject(teacher);
            }

            return new TeacherWithSubject(teacher, subject);
        }).collect(Collectors.toList());
    }

    @Override
    public TeacherWithSubject getByName(String name, String lastName) throws DatabaseException {
        Teacher result = teachers.stream()
                .filter(teacher ->
                        teacher.getName().equalsIgnoreCase(name) && teacher.getLastName().equalsIgnoreCase(lastName))
                .findFirst()
                .orElse(null);

        if (result == null) {
            return null;
        }

        return new TeacherWithSubject(result, getSubjectByTeacherId(result.getId()));
    }

    @Override
    public Subject getSubjectByTeacherId(long teacherId) {
        Integer exist = teacherToSubjectMapping.get(teacherId);

        if (exist == null) {
            return null;
        }

        return subjects.stream().filter(o -> o.getId() == exist).findFirst().orElse(null);
    }

    @Override
    public Teacher getTeacherBySubjectId(int subjectId) {
        return teacherToSubjectMapping.entrySet().stream().filter(entry -> entry.getValue() == subjectId)
                .map(entry -> teachers.stream().filter(teacher -> teacher.getId() == entry.getKey()).findFirst().orElse(null))
                .findFirst().orElse(null);
    }

    @Override
    public void attachSubjectToTeacher(long teacherId, int subjectId) {
        Integer exist = teacherToSubjectMapping.get(teacherId);

        if (exist != null && exist == subjectId) {
            return;
        }

        teacherToSubjectMapping.put(teacherId, subjectId);
    }

    @Override
    public void detachSubjectFromTeacher(long teacherId) {
        teacherToSubjectMapping.remove(teacherId);
    }

    @Override
    public void detachTeachersFromSubject(int subjectId) {
        teacherToSubjectMapping.values().removeIf(id -> id == subjectId);
    }

}
