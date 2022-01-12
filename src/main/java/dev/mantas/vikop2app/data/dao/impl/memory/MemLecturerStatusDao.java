package dev.mantas.vikop2app.data.dao.impl.memory;

import dev.mantas.vikop2app.data.dao.impl.memory.util.MemoryDataPool;
import dev.mantas.vikop2app.data.dao.type.LecturerStatusDao;
import dev.mantas.vikop2app.data.source.impl.MemoryDataSource;
import dev.mantas.vikop2app.exception.DatabaseException;
import dev.mantas.vikop2app.model.LecturerStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MemLecturerStatusDao implements LecturerStatusDao {

    private final MemoryDataSource dataSource;
    private List<LecturerStatus> statuses;

    public MemLecturerStatusDao(@NotNull MemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onAvailable() {
        MemoryDataPool dataPool = dataSource.provide();
        this.statuses = dataPool.getLecturerStatuses();
    }

    @Override
    public int getTotalCount() throws DatabaseException {
        return statuses.size();
    }

    @Override
    public List<LecturerStatus> getStatues() {
        return statuses;
    }

}
