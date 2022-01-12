package dev.mantas.vikop2app.ui.common.converter;

import dev.mantas.vikop2app.model.LecturerStatus;
import javafx.util.StringConverter;

public class LecturerStatusStringConverter extends StringConverter<LecturerStatus> {

    @Override
    public String toString(LecturerStatus status) {
        return status == null ? null : status.getName();
    }

    @Override
    public LecturerStatus fromString(String s) {
        return null;
    }

}
