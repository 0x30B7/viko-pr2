package dev.mantas.vikop2app.data.dao.impl.memory;

import dev.mantas.vikop2app.data.dao.impl.memory.util.MemoryDataPool;
import dev.mantas.vikop2app.data.dao.impl.memory.util.MemoryDataPool.StudentGroupToSubjectMapping;
import dev.mantas.vikop2app.data.dao.type.StudentGroupSubjectLinkDao;
import dev.mantas.vikop2app.data.source.impl.MemoryDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.StudentGroup;
import dev.mantas.vikop2app.model.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MemStudentGroupSubjectLinkDao implements StudentGroupSubjectLinkDao {

    private final MemoryDataSource dataSource;
    private List<StudentGroup> studentGroups;
    private List<Subject> subjects;
    private Set<StudentGroupToSubjectMapping> studentGroupToSubjectMapping;

    public MemStudentGroupSubjectLinkDao(@NotNull MemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MemoryDataPool dataPool = dataSource.provide();
        this.studentGroups = dataPool.getStudentGroups();
        this.subjects = dataPool.getSubjects();
        this.studentGroupToSubjectMapping = dataPool.getStudentGroupToSubjectMapping();
    }

    @Override
    public List<Subject> getSubjectsByStudentGroupId(int studentGroupId) throws DatabaseException {
        return studentGroupToSubjectMapping.stream()
                .filter(link -> link.studentGroupId() == studentGroupId)
                .map(link -> subjects.stream().filter(subject -> subject.getId() == link.subjectId()).findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentGroup> getStudentGroupsBySubjectId(int subjectId) throws DatabaseException {
        return studentGroupToSubjectMapping.stream()
                .filter(link -> link.subjectId() == subjectId)
                .map(link -> studentGroups.stream().filter(studentGroup -> studentGroup.getId() == link.studentGroupId())
                        .findFirst().orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void attachSubjectsToStudentGroup(List<Integer> subjectIds, int studentGroupId) throws DatabaseException {
        subjectIds.stream()
                .map(subjectId -> new StudentGroupToSubjectMapping(studentGroupId, subjectId))
                .forEach(studentGroupToSubjectMapping::add);
    }

    @Override
    public void detachAllSubjectsFromStudentGroup(int studentGroupId) throws DatabaseException {
        studentGroupToSubjectMapping.removeIf(link -> link.studentGroupId() == studentGroupId);
    }

    @Override
    public void detachAllStudentGroupsFromSubject(int subjectId) throws DatabaseException {
        studentGroupToSubjectMapping.removeIf(link -> link.subjectId() == subjectId);
    }

}
