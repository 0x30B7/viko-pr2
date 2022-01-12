package dev.mantas.vikop2app.data.dao.type;

import dev.mantas.vikop2app.data.dao.DataAccessObject;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Grade;
import dev.mantas.vikop2app.model.helper.GradeWithSubject;

import java.util.Date;
import java.util.List;

public interface GradeDao extends DataAccessObject {

    List<Grade> getGradesBySubject(int subjectId) throws DatabaseException;

    GradeWithSubject getGradeWithSubject(int gradeId) throws DatabaseException;

    Grade getById(int id) throws DatabaseException;

    Grade getGradeByTitleAndSubjectId(String title, int subjectId) throws DatabaseException;

    void createGrade(Date createdAt, String title, int subjectId) throws DatabaseException;

    void removeGrade(int id) throws DatabaseException;

    void removeGradesBySubjectId(int subjectId) throws DatabaseException;

}
