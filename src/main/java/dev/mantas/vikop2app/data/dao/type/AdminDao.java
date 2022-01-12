package dev.mantas.vikop2app.data.dao.type;

import dev.mantas.vikop2app.data.dao.DataAccessObject;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Admin;

public interface AdminDao extends DataAccessObject {

    int getTotalCount() throws DatabaseException;

    Admin getByNameAndLastName(String name, String lastName) throws DatabaseException;

}
