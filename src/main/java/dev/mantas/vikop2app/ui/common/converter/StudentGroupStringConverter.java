package dev.mantas.vikop2app.ui.common.converter;

import dev.mantas.vikop2app.model.StudentGroup;
import javafx.util.StringConverter;

public class StudentGroupStringConverter extends StringConverter<StudentGroup> {

    @Override
    public String toString(StudentGroup studentGroup) {
        return studentGroup == null ? null : studentGroup.getTitle();
    }

    @Override
    public StudentGroup fromString(String s) {
        return null;
    }

}
