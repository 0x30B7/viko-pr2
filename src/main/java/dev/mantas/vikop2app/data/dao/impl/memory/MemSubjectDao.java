package dev.mantas.vikop2app.data.dao.impl.memory;

import dev.mantas.vikop2app.data.dao.impl.memory.util.MemoryDataPool;
import dev.mantas.vikop2app.data.dao.type.SubjectDao;
import dev.mantas.vikop2app.data.source.impl.MemoryDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MemSubjectDao implements SubjectDao {

    private final MemoryDataSource dataSource;
    private AtomicInteger idGenerator;
    private List<Subject> subjects;

    public MemSubjectDao(@NotNull MemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MemoryDataPool dataPool = dataSource.provide();
        this.idGenerator = dataPool.getIdGenerator();
        this.subjects = dataPool.getSubjects();
    }

    @Override
    public int getTotalCount() {
        return subjects.size();
    }

    @Override
    public List<Subject> getAllSubjects() {
        return subjects;
    }

    @Override
    public Subject getById(int id) throws DatabaseException {
        return subjects.stream()
                .filter(subject ->
                        subject.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Subject getSubjectByTitle(String title) throws DatabaseException {
        return subjects.stream()
                .filter(subject ->
                        subject.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void createSubject(String title) throws DatabaseException {
        subjects.add(new Subject(idGenerator.incrementAndGet(), title));
    }

    @Override
    public void updateSubject(int id, String title) throws DatabaseException {
        for (Subject subject : subjects) {
            if (subject.getId() == id) {
                subject.setTitle(title);
                return;
            }
        }
    }

    @Override
    public void removeSubject(int id) throws DatabaseException {
        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);
            if (subject.getId() == id) {
                subjects.remove(i);
                return;
            }
        }
    }

}
