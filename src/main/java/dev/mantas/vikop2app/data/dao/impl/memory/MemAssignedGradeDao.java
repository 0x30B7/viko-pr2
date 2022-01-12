package dev.mantas.vikop2app.data.dao.impl.memory;

import dev.mantas.vikop2app.data.dao.impl.memory.util.MemoryDataPool;
import dev.mantas.vikop2app.data.dao.type.AssignedGradeDao;
import dev.mantas.vikop2app.data.source.impl.MemoryDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.AssignedGrade;
import dev.mantas.vikop2app.model.Grade;
import dev.mantas.vikop2app.model.helper.GradeWithAssignedGrade;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MemAssignedGradeDao implements AssignedGradeDao {

    private final MemoryDataSource dataSource;
    private AtomicInteger idGenerator;
    private List<AssignedGrade> assignedGrades;
    private List<Grade> grades;
    private Map<Long, Integer> studentToStudentGroupMapping;

    public MemAssignedGradeDao(@NotNull MemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MemoryDataPool dataPool = dataSource.provide();
        this.idGenerator = dataPool.getIdGenerator();
        this.assignedGrades = dataPool.getAssignedGrades();
        this.grades = dataPool.getGrades();
        this.studentToStudentGroupMapping = dataPool.getStudentToStudentGroupMapping();
    }

    @Override
    public Map<Integer, Integer> getAssignedGradeCountInStudentGroupsById(int gradeId, List<Integer> studentGroupIds) {
        Set<Integer> groupIdsSet = new HashSet<>(studentGroupIds);
        Map<Integer, Integer> result = new HashMap<>();

        for (AssignedGrade assignedGrade : assignedGrades) {
            if (assignedGrade.getGradeId() == gradeId) {
                int studentGroupId = studentToStudentGroupMapping.get(assignedGrade.getStudentId());
                if (groupIdsSet.contains(studentGroupId)) {
                    result.compute(studentGroupId, (key, oldValue) -> {
                        if (oldValue == null) return 1;
                        return oldValue + 1;
                    });
                }
            }
        }

        return result;
    }

    @Override
    public List<AssignedGrade> getAssignedGrades(int gradeId, int studentGroupId) {
        List<AssignedGrade> result = new ArrayList<>();

        for (AssignedGrade assignedGrade : assignedGrades) {
            if (assignedGrade.getGradeId() != gradeId) continue;

            Integer existStudentGroupId = studentToStudentGroupMapping.get(assignedGrade.getStudentId());

            if (existStudentGroupId == studentGroupId) {
                result.add(assignedGrade);
            }
        }

        return result;
    }

    @Override
    public List<GradeWithAssignedGrade> getAssignedGradesForStudentAndSubject(long studentId, int subjectId) {
        List<GradeWithAssignedGrade> result = new ArrayList<>();

        for (Grade grade : grades) {
            if (grade.getSubjectId() == subjectId) {
                result.add(new GradeWithAssignedGrade(grade, assignedGrades.stream()
                        .filter(assignedGrade -> assignedGrade.getGradeId() == grade.getId() && assignedGrade.getStudentId() == studentId)
                        .findFirst().orElse(null)));
            }
        }

        return result;
    }

    @Override
    public void assignGrade(int gradeId, Date createdAt, long studentId, int value) throws DatabaseException {
        AssignedGrade assignedGrade = assignedGrades.stream()
                .filter(grade -> grade.getGradeId() == gradeId && grade.getStudentId() == studentId)
                .findFirst().orElse(null);

        if (assignedGrade != null) {
            assignedGrade.setValue(value);
            return;
        }

        assignedGrades.add(new AssignedGrade(idGenerator.incrementAndGet(), createdAt, gradeId, studentId, value));
    }

    @Override
    public void unassignGrade(int gradeId, long studentId) throws DatabaseException {
        for (int i = 0; i < assignedGrades.size(); i++) {
            AssignedGrade grade = assignedGrades.get(i);
            if (grade.getGradeId() == gradeId && grade.getStudentId() == studentId) {
                assignedGrades.remove(i);
                return;
            }
        }
    }

    @Override
    public void unassignAllAssignedGrades(int gradeId) throws DatabaseException {
        assignedGrades.removeIf(grade -> grade.getGradeId() == gradeId);
    }

    @Override
    public void unassignAllGradesByStudentId(long studentId) throws DatabaseException {
        assignedGrades.removeIf(grade -> grade.getStudentId() == studentId);
    }

    @Override
    public void unassignAllGradesBySubjectId(int subjectId) throws DatabaseException {
        for (Grade grade : grades) {
            if (grade.getSubjectId() == subjectId) {
                assignedGrades.removeIf(assignedGrade -> assignedGrade.getGradeId() == grade.getId());
            }
        }
    }

}
