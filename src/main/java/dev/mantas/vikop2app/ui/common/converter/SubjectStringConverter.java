package dev.mantas.vikop2app.ui.common.converter;

import dev.mantas.vikop2app.model.Subject;
import javafx.util.StringConverter;

public class SubjectStringConverter extends StringConverter<Subject> {

    @Override
    public String toString(Subject subject) {
        return subject == null ? null : subject.getTitle();
    }

    @Override
    public Subject fromString(String s) {
        return null;
    }

}
