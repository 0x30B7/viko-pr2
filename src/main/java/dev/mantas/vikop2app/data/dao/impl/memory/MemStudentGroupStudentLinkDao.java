package dev.mantas.vikop2app.data.dao.impl.memory;

import dev.mantas.vikop2app.data.dao.impl.memory.util.MemoryDataPool;
import dev.mantas.vikop2app.data.dao.type.StudentGroupStudentLinkDao;
import dev.mantas.vikop2app.data.source.impl.MemoryDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Student;
import dev.mantas.vikop2app.model.StudentGroup;
import dev.mantas.vikop2app.model.helper.StudentWithStudentGroup;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MemStudentGroupStudentLinkDao implements StudentGroupStudentLinkDao {

    private final MemoryDataSource dataSource;
    private List<StudentGroup> studentGroups;
    private List<Student> students;
    private Map<Long, Integer> studentToStudentGroupMapping;

    public MemStudentGroupStudentLinkDao(@NotNull MemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MemoryDataPool dataPool = dataSource.provide();
        this.studentGroups = dataPool.getStudentGroups();
        this.students = dataPool.getStudents();
        this.studentToStudentGroupMapping = dataPool.getStudentToStudentGroupMapping();
    }

    @Override
    public Map<Integer, Integer> getStudentCountInStudentGroupsByIds(List<Integer> studentGroupIds) throws DatabaseException {
        Set<Integer> groupIdsSet = new HashSet<>(studentGroupIds);
        Map<Integer, Integer> result = new HashMap<>();

        for (Integer groupId : studentToStudentGroupMapping.values()) {
            if (groupIdsSet.contains(groupId)) {
                result.compute(groupId, (key, oldValue) -> {
                    if (oldValue == null) return 1;
                    return oldValue + 1;
                });
            }
        }

        return result;
    }

    @Override
    public List<StudentWithStudentGroup> getAllStudentsWithOptStudentGroups() throws DatabaseException {
        return students.stream().map(student -> {
            Integer optStudentGroupId = studentToStudentGroupMapping.get(student.getId());
            StudentGroup optStudentGroup = optStudentGroupId == null ? null : studentGroups.stream()
                    .filter(studentGroup -> studentGroup.getId() == optStudentGroupId)
                    .findFirst().orElse(null);

            return new StudentWithStudentGroup(student, optStudentGroup);
        }).collect(Collectors.toList());
    }

    @Override
    public List<Student> getStudentsByStudentGroupId(int studentGroupId) throws DatabaseException {
        return students.stream()
                .filter(student -> studentToStudentGroupMapping.get(student.getId()) == studentGroupId)
                .collect(Collectors.toList());
    }

    @Override
    public StudentGroup getStudentGroupByStudentId(long studentId) throws DatabaseException {
        Integer studentGroupId = studentToStudentGroupMapping.get(studentId);

        if (studentGroupId == null) {
            return null;
        }

        return studentGroups.stream().filter(studentGroup -> studentGroup.getId() == studentGroupId)
                .findFirst().orElse(null);
    }

    @Override
    public void attachStudentToStudentGroup(long studentId, int studentGroupId) throws DatabaseException {
        studentToStudentGroupMapping.put(studentId, studentGroupId);
    }

    @Override
    public void detachStudentFromStudentGroup(long studentId) throws DatabaseException {
        studentToStudentGroupMapping.remove(studentId);
    }

    @Override
    public void detachAllStudentsFromStudentGroup(int studentGroupId) throws DatabaseException {
        studentToStudentGroupMapping.values().removeIf(id -> id == studentGroupId);
    }

}
