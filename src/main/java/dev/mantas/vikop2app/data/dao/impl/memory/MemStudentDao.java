package dev.mantas.vikop2app.data.dao.impl.memory;

import dev.mantas.vikop2app.data.dao.impl.memory.util.MemoryDataPool;
import dev.mantas.vikop2app.data.dao.type.StudentDao;
import dev.mantas.vikop2app.data.source.impl.MemoryDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Student;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public class MemStudentDao implements StudentDao {

    private final MemoryDataSource dataSource;
    private List<Student> students;

    public MemStudentDao(@NotNull MemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MemoryDataPool dataPool = dataSource.provide();
        this.students = dataPool.getStudents();
    }

    @Override
    public int getTotalCount() {
        return students.size();
    }

    @Override
    public Student getById(long id) throws DatabaseException {
        return students.stream()
                .filter(student ->
                        student.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Student getByNameAndLastName(String name, String lastName) throws DatabaseException {
        return students.stream()
                .filter(student ->
                        student.getName().equalsIgnoreCase(name) && student.getLastName().equalsIgnoreCase(lastName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void createStudent(long id, Date createdAt, String name, String lastName) {
        students.add(new Student(id, createdAt, name, lastName));
    }

    @Override
    public void updateStudent(long id, @Nullable String name, @Nullable String lastName) throws DatabaseException {
        for (Student teacher : students) {
            if (teacher.getId() == id) {
                if (name != null) teacher.setName(name);
                if (lastName != null) teacher.setLastName(lastName);
                return;
            }
        }
    }

    @Override
    public void removeStudent(long id) throws DatabaseException {
        for (int i = 0; i < students.size(); i++) {
            Student teacher = students.get(i);
            if (teacher.getId() == id) {
                students.remove(i);
                return;
            }
        }
    }

}
