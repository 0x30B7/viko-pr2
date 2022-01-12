package dev.mantas.vikop2app.data;

import dev.mantas.vikop2app.data.dao.DataAccessObject;
import dev.mantas.vikop2app.data.dao.manager.DAOManager;
import dev.mantas.vikop2app.data.dao.type.AdminDao;
import dev.mantas.vikop2app.data.dao.type.LecturerStatusDao;

public class ServiceManager {

    private static ServiceManager instance;

    public static ServiceManager getInstance() {
        return instance;
    }

    public static void init(DAOManager<?> daoManager) {
        if (instance != null) {
            throw new IllegalStateException("Service manager already initialized");
        }

        instance = new ServiceManager(daoManager);
    }

    private final DAOManager<?> daoManager;
    private boolean initialStateCheckPerformed;

    private ServiceManager(DAOManager<?> daoManager) {
        this.daoManager = daoManager;
    }

    public void performInitialStateCheck() throws Exception {
        if (initialStateCheckPerformed) {
            return;
        }

        initialStateCheckPerformed = true;

        try {
            daoManager.initDataSource();
        } catch (Exception ex) {
            throw new Exception("Nepavyko prisijungti prie duomenų bazės/šaltinio", ex);
        }

        AdminDao adminDao = daoManager.getDAO(AdminDao.class);
        if (adminDao.getTotalCount() <= 0) {
            throw new Exception("Nerasta nei vieno administracinio naudotojo.");
        }

        LecturerStatusDao lecturerStatusDao = daoManager.getDAO(LecturerStatusDao.class);
        if (lecturerStatusDao.getTotalCount() <= 0) {
            throw new Exception("Nerasta nei vieno dėstytojo laipsnio.");
        }
    }

    public void shutdown() {
        try {
            daoManager.closeDataSource();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public <T extends DataAccessObject> T getDAO(Class<T> abstraction) {
        return daoManager.getDAO(abstraction);
    }

}
