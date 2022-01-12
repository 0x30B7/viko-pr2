package dev.mantas.vikop2app.data.dao.type;

import dev.mantas.vikop2app.data.dao.DataAccessObject;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.LecturerStatus;

import java.util.List;

public interface LecturerStatusDao extends DataAccessObject {

    int getTotalCount() throws DatabaseException;

    List<LecturerStatus> getStatues() throws DatabaseException;

}
