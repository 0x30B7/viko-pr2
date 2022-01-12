package dev.mantas.vikop2app.data.dao.impl.memory;

import dev.mantas.vikop2app.data.dao.impl.memory.util.MemoryDataPool;
import dev.mantas.vikop2app.data.dao.type.StudentGroupDao;
import dev.mantas.vikop2app.data.source.impl.MemoryDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.StudentGroup;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MemStudentGroupDao implements StudentGroupDao {

    private final MemoryDataSource dataSource;
    private AtomicInteger idGenerator;
    private List<StudentGroup> studentGroups;

    public MemStudentGroupDao(@NotNull MemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MemoryDataPool dataPool = dataSource.provide();
        this.idGenerator = dataPool.getIdGenerator();
        this.studentGroups = dataPool.getStudentGroups();
    }

    @Override
    public int getTotalCount() {
        return studentGroups.size();
    }

    @Override
    public List<StudentGroup> getAllStudentGroups() {
        return studentGroups;
    }

    @Override
    public StudentGroup getById(int id) throws DatabaseException {
        for (StudentGroup group : studentGroups) {
            if (group.getId() == id) {
                return group;
            }
        }

        return null;
    }

    @Override
    public StudentGroup getByTitle(String title) throws DatabaseException {
        for (StudentGroup group : studentGroups) {
            if (group.getTitle().equalsIgnoreCase(title)) {
                return group;
            }
        }

        return null;
    }

    @Override
    public void createStudentGroup(String title, Date createdAt) throws DatabaseException {
        studentGroups.add(new StudentGroup(idGenerator.incrementAndGet(), createdAt, title));
    }

    @Override
    public void updateStudentGroup(int id, String title) throws DatabaseException {
        for (StudentGroup group : studentGroups) {
            if (group.getId() == id) {
                group.setTitle(title);
                return;
            }
        }
    }

    @Override
    public void removeStudentGroup(long id) throws DatabaseException {
        for (int i = 0; i < studentGroups.size(); i++) {
            StudentGroup group = studentGroups.get(i);
            if (group.getId() == id) {
                studentGroups.remove(i);
                return;
            }
        }
    }

}
