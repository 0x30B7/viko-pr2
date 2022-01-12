package dev.mantas.vikop2app.ui.common.converter;

import dev.mantas.vikop2app.model.Teacher;
import javafx.util.StringConverter;

import static dev.mantas.vikop2app.util.TextUtil.capitalize;

public class TeacherStringConverter extends StringConverter<Teacher> {

    @Override
    public String toString(Teacher teacher) {
        return teacher == null ? null : teacher.getLecturerStatus().getAbbreviation() + " " +
                capitalize(teacher.getName()) + " " + capitalize(teacher.getLastName());
    }

    @Override
    public Teacher fromString(String s) {
        return null;
    }

}
