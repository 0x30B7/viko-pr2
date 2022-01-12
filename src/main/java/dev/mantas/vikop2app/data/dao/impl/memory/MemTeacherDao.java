package dev.mantas.vikop2app.data.dao.impl.memory;

import dev.mantas.vikop2app.data.dao.impl.memory.util.MemoryDataPool;
import dev.mantas.vikop2app.data.dao.type.TeacherDao;
import dev.mantas.vikop2app.data.source.impl.MemoryDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.LecturerStatus;
import dev.mantas.vikop2app.model.Teacher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public class MemTeacherDao implements TeacherDao {

    private final MemoryDataSource dataSource;
    private List<Teacher> teachers;

    public MemTeacherDao(@NotNull MemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MemoryDataPool dataPool = dataSource.provide();
        this.teachers = dataPool.getTeachers();
    }

    @Override
    public int getTotalCount() {
        return teachers.size();
    }

    @Override
    public Teacher getById(long id) throws DatabaseException {
        return teachers.stream()
                .filter(teacher ->
                        teacher.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Teacher getByNameAndLastName(String name, String lastName) throws DatabaseException {
        return teachers.stream()
                .filter(teacher ->
                        teacher.getName().equalsIgnoreCase(name) && teacher.getLastName().equalsIgnoreCase(lastName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void createTeacher(long id, Date createdAt, String name, String lastName, LecturerStatus lecturerStatus,
                              @Nullable String email, @Nullable Integer roomNo) {
        teachers.add(new Teacher(id, createdAt, name, lastName, lecturerStatus, email, roomNo));
    }

    @Override
    public void updateTeacher(long id, String name, String lastName,
                              LecturerStatus lecturerStatus, @Nullable String email,
                              @Nullable Integer roomNo) throws DatabaseException {
        for (Teacher teacher : teachers) {
            if (teacher.getId() == id) {
                if (name != null) teacher.setName(name);
                if (lastName != null) teacher.setLastName(lastName);
                if (lecturerStatus != null) teacher.setLecturerStatus(lecturerStatus);
                if (email != null) teacher.setEmail(email);
                if (roomNo != null) teacher.setRoomNumber(roomNo);
                return;
            }
        }
    }

    @Override
    public void removeTeacher(long id) throws DatabaseException {
        for (int i = 0; i < teachers.size(); i++) {
            Teacher teacher = teachers.get(i);
            if (teacher.getId() == id) {
                teachers.remove(i);
                return;
            }
        }
    }

}
