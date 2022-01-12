package dev.mantas.vikop2app.ui;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.mantas.vikop2app.data.ServiceManager;
import dev.mantas.vikop2app.data.dao.impl.memory.MemAdminDao;
import dev.mantas.vikop2app.data.dao.impl.memory.MemAssignedGradeDao;
import dev.mantas.vikop2app.data.dao.impl.memory.MemGradeDao;
import dev.mantas.vikop2app.data.dao.impl.memory.MemLecturerStatusDao;
import dev.mantas.vikop2app.data.dao.impl.memory.MemStudentDao;
import dev.mantas.vikop2app.data.dao.impl.memory.MemStudentGroupDao;
import dev.mantas.vikop2app.data.dao.impl.memory.MemStudentGroupStudentLinkDao;
import dev.mantas.vikop2app.data.dao.impl.memory.MemStudentGroupSubjectLinkDao;
import dev.mantas.vikop2app.data.dao.impl.memory.MemSubjectDao;
import dev.mantas.vikop2app.data.dao.impl.memory.MemTeacherDao;
import dev.mantas.vikop2app.data.dao.impl.memory.MemTeacherSubjectLinkDao;
import dev.mantas.vikop2app.data.dao.impl.mongo.MongoAdminDao;
import dev.mantas.vikop2app.data.dao.impl.mongo.MongoAssignedGradeDao;
import dev.mantas.vikop2app.data.dao.impl.mongo.MongoGradeDao;
import dev.mantas.vikop2app.data.dao.impl.mongo.MongoLecturerStatusDao;
import dev.mantas.vikop2app.data.dao.impl.mongo.MongoStudentDao;
import dev.mantas.vikop2app.data.dao.impl.mongo.MongoStudentGroupDao;
import dev.mantas.vikop2app.data.dao.impl.mongo.MongoStudentGroupStudentLinkDao;
import dev.mantas.vikop2app.data.dao.impl.mongo.MongoStudentGroupSubjectLinkDao;
import dev.mantas.vikop2app.data.dao.impl.mongo.MongoSubjectDao;
import dev.mantas.vikop2app.data.dao.impl.mongo.MongoTeacherDao;
import dev.mantas.vikop2app.data.dao.impl.mongo.MongoTeacherSubjectLinkDao;
import dev.mantas.vikop2app.data.dao.impl.sql.SQLAdminDao;
import dev.mantas.vikop2app.data.dao.impl.sql.SQLAssignedGradeDao;
import dev.mantas.vikop2app.data.dao.impl.sql.SQLGradeDao;
import dev.mantas.vikop2app.data.dao.impl.sql.SQLLecturerStatusDao;
import dev.mantas.vikop2app.data.dao.impl.sql.SQLStudentDao;
import dev.mantas.vikop2app.data.dao.impl.sql.SQLStudentGroupDao;
import dev.mantas.vikop2app.data.dao.impl.sql.SQLStudentGroupStudentLinkDao;
import dev.mantas.vikop2app.data.dao.impl.sql.SQLStudentGroupSubjectLinkDao;
import dev.mantas.vikop2app.data.dao.impl.sql.SQLSubjectDao;
import dev.mantas.vikop2app.data.dao.impl.sql.SQLTeacherDao;
import dev.mantas.vikop2app.data.dao.impl.sql.SQLTeacherSubjectLinkDao;
import dev.mantas.vikop2app.data.dao.manager.DAOManager;
import dev.mantas.vikop2app.data.dao.manager.impl.DAOManagerMongoDB;
import dev.mantas.vikop2app.data.dao.manager.impl.DAOManagerSQL;
import dev.mantas.vikop2app.data.dao.manager.impl.DaoManagerMemory;
import dev.mantas.vikop2app.data.dao.type.AdminDao;
import dev.mantas.vikop2app.data.dao.type.AssignedGradeDao;
import dev.mantas.vikop2app.data.dao.type.GradeDao;
import dev.mantas.vikop2app.data.dao.type.LecturerStatusDao;
import dev.mantas.vikop2app.data.dao.type.StudentDao;
import dev.mantas.vikop2app.data.dao.type.StudentGroupDao;
import dev.mantas.vikop2app.data.dao.type.StudentGroupStudentLinkDao;
import dev.mantas.vikop2app.data.dao.type.StudentGroupSubjectLinkDao;
import dev.mantas.vikop2app.data.dao.type.SubjectDao;
import dev.mantas.vikop2app.data.dao.type.TeacherDao;
import dev.mantas.vikop2app.data.dao.type.TeacherSubjectLinkDao;
import dev.mantas.vikop2app.data.source.DataSourceType;
import dev.mantas.vikop2app.data.source.provider.MemoryDataSourceProvider;
import dev.mantas.vikop2app.data.source.provider.MongoDataSourceProvider;
import dev.mantas.vikop2app.data.source.provider.SQLDataSourceProvider;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Duration;

public class Application extends javafx.application.Application {

    private static final DataSourceType DATA_SOURCE_TYPE = DataSourceType.MONGODB;
    private static final boolean PRE_POPULATE_MEMORY_DATA_SOURCE = true;

    public static void main(String[] args) {
        DAOManager<?> daoManager;

        switch (DATA_SOURCE_TYPE) {
            case MEMORY -> {
                DaoManagerMemory daoManagerMemory = new DaoManagerMemory(new MemoryDataSourceProvider(PRE_POPULATE_MEMORY_DATA_SOURCE));
                daoManager = daoManagerMemory;

                daoManagerMemory.registerDAO(AdminDao.class, MemAdminDao::new);
                daoManagerMemory.registerDAO(AssignedGradeDao.class, MemAssignedGradeDao::new);
                daoManagerMemory.registerDAO(GradeDao.class, MemGradeDao::new);
                daoManagerMemory.registerDAO(LecturerStatusDao.class, MemLecturerStatusDao::new);
                daoManagerMemory.registerDAO(StudentDao.class, MemStudentDao::new);
                daoManagerMemory.registerDAO(StudentGroupDao.class, MemStudentGroupDao::new);
                daoManagerMemory.registerDAO(StudentGroupStudentLinkDao.class, MemStudentGroupStudentLinkDao::new);
                daoManagerMemory.registerDAO(StudentGroupSubjectLinkDao.class, MemStudentGroupSubjectLinkDao::new);
                daoManagerMemory.registerDAO(SubjectDao.class, MemSubjectDao::new);
                daoManagerMemory.registerDAO(TeacherDao.class, MemTeacherDao::new);
                daoManagerMemory.registerDAO(TeacherSubjectLinkDao.class, MemTeacherSubjectLinkDao::new);
            }

            case MYSQL -> {
                HikariConfig config = new HikariDataSource();
                config.setJdbcUrl("jdbc:mysql://localhost:3306/viko-pr2");
                config.setUsername("root");
//                config.setPassword( "" );
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                config.setMaximumPoolSize(5);
                config.setConnectionTimeout(Duration.ofSeconds(5).toMillis());
                config.setIdleTimeout(Duration.ofMinutes(2).toMillis());
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

                DAOManagerSQL daoManagerSQL = new DAOManagerSQL(new SQLDataSourceProvider(config));
                daoManager = daoManagerSQL;

                daoManagerSQL.registerDAO(AdminDao.class, SQLAdminDao::new);
                daoManagerSQL.registerDAO(AssignedGradeDao.class, SQLAssignedGradeDao::new);
                daoManagerSQL.registerDAO(GradeDao.class, SQLGradeDao::new);
                daoManagerSQL.registerDAO(LecturerStatusDao.class, SQLLecturerStatusDao::new);
                daoManagerSQL.registerDAO(StudentDao.class, SQLStudentDao::new);
                daoManagerSQL.registerDAO(StudentGroupDao.class, SQLStudentGroupDao::new);
                daoManagerSQL.registerDAO(StudentGroupStudentLinkDao.class, SQLStudentGroupStudentLinkDao::new);
                daoManagerSQL.registerDAO(StudentGroupSubjectLinkDao.class, SQLStudentGroupSubjectLinkDao::new);
                daoManagerSQL.registerDAO(SubjectDao.class, SQLSubjectDao::new);
                daoManagerSQL.registerDAO(TeacherDao.class, SQLTeacherDao::new);
                daoManagerSQL.registerDAO(TeacherSubjectLinkDao.class, SQLTeacherSubjectLinkDao::new);
            }

            case MONGODB -> {
                MongoClientSettings config = MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                        .build();

                DAOManagerMongoDB daoManagerMongoDB = new DAOManagerMongoDB(new MongoDataSourceProvider(config));
                daoManager = daoManagerMongoDB;

                daoManagerMongoDB.registerDAO(AdminDao.class, MongoAdminDao::new);
                daoManagerMongoDB.registerDAO(AssignedGradeDao.class, MongoAssignedGradeDao::new);
                daoManagerMongoDB.registerDAO(GradeDao.class, MongoGradeDao::new);
                daoManagerMongoDB.registerDAO(LecturerStatusDao.class, MongoLecturerStatusDao::new);
                daoManagerMongoDB.registerDAO(StudentDao.class, MongoStudentDao::new);
                daoManagerMongoDB.registerDAO(StudentGroupDao.class, MongoStudentGroupDao::new);
                daoManagerMongoDB.registerDAO(StudentGroupStudentLinkDao.class, MongoStudentGroupStudentLinkDao::new);
                daoManagerMongoDB.registerDAO(StudentGroupSubjectLinkDao.class, MongoStudentGroupSubjectLinkDao::new);
                daoManagerMongoDB.registerDAO(SubjectDao.class, MongoSubjectDao::new);
                daoManagerMongoDB.registerDAO(TeacherDao.class, MongoTeacherDao::new);
                daoManagerMongoDB.registerDAO(TeacherSubjectLinkDao.class, MongoTeacherSubjectLinkDao::new);
            }

            default -> {
                return;
            }
        }

        // ===================================================================== //

        ServiceManager.init(daoManager);

        // ===================================================================== //

        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("VIKO-PR-2");
        stage.setScene(scene);
        stage.show();

        scene.getWindow().setOnCloseRequest(event -> {
            ServiceManager.getInstance().shutdown();
            Platform.exit();
        });
    }

}