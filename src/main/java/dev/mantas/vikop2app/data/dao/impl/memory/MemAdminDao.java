package dev.mantas.vikop2app.data.dao.impl.memory;

import dev.mantas.vikop2app.data.dao.impl.memory.util.MemoryDataPool;
import dev.mantas.vikop2app.data.dao.type.AdminDao;
import dev.mantas.vikop2app.data.source.impl.MemoryDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.Admin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MemAdminDao implements AdminDao {

    private final MemoryDataSource dataSource;
    private List<Admin> admins;

    public MemAdminDao(@NotNull MemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MemoryDataPool dataPool = dataSource.provide();
        this.admins = dataPool.getAdmins();
    }

    @Override
    public int getTotalCount() throws DatabaseException {
        return admins.size();
    }

    @Override
    public Admin getByNameAndLastName(String name, String lastName) {
        return admins.stream()
                .filter(admin ->
                        admin.getName().equalsIgnoreCase(name) && admin.getLastName().equalsIgnoreCase(lastName))
                .findFirst()
                .orElse(null);
    }

}
