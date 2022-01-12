package dev.mantas.vikop2app.data.dao.impl.memory;

import dev.mantas.vikop2app.data.dao.impl.memory.util.MemoryDataPool;
import dev.mantas.vikop2app.data.dao.type.GradeDao;
import dev.mantas.vikop2app.data.source.impl.MemoryDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Grade;
import dev.mantas.vikop2app.model.Subject;
import dev.mantas.vikop2app.model.helper.GradeWithSubject;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MemGradeDao implements GradeDao {

    private final MemoryDataSource dataSource;
    private AtomicInteger idGenerator;
    private List<Grade> grades;
    private List<Subject> subjects;

    public MemGradeDao(@NotNull MemoryDataSource dataSource) {
        this.dataSource = dataSource;

    }

    @Override
    public void onAvailable() {
        MemoryDataPool dataPool = dataSource.provide();
        this.idGenerator = dataPool.getIdGenerator();
        this.grades = dataPool.getGrades();
        this.subjects = dataPool.getSubjects();
    }

    @Override
    public List<Grade> getGradesBySubject(int subjectId) throws DatabaseException {
        return grades.stream().filter(grade -> grade.getSubjectId() == subjectId).collect(Collectors.toList());
    }

    @Override
    public GradeWithSubject getGradeWithSubject(int gradeId) throws DatabaseException {
        Grade grade = getById(gradeId);

        if (grade != null) {
            return new GradeWithSubject(
                    grade,
                    subjects.stream().filter(subject -> subject.getId() == grade.getSubjectId())
                            .findFirst().orElse(null));
        }

        return null;
    }

    @Override
    public Grade getById(int id) throws DatabaseException {
        return grades.stream()
                .filter(grade ->
                        grade.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Grade getGradeByTitleAndSubjectId(String title, int subjectId) throws DatabaseException {
        return grades.stream()
                .filter(grade ->
                        grade.getSubjectId() == subjectId && grade.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void createGrade(Date createdAt, String title, int subjectId) throws DatabaseException {
        grades.add(new Grade(idGenerator.incrementAndGet(), createdAt, title, subjectId));
    }

    @Override
    public void removeGrade(int id) throws DatabaseException {
        for (int i = 0; i < grades.size(); i++) {
            Grade grade = grades.get(i);
            if (grade.getId() == id) {
                grades.remove(i);
                return;
            }
        }
    }

    @Override
    public void removeGradesBySubjectId(int subjectId) throws DatabaseException {
        grades.removeIf(grade -> grade.getSubjectId() == subjectId);
    }

}
