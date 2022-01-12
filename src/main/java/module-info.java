module vikop2app {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.se;
    requires org.jetbrains.annotations;
    requires com.zaxxer.hikari;
    requires mongo.java.driver;

    opens dev.mantas.vikop2app.ui to javafx.fxml;
    exports dev.mantas.vikop2app.ui;

    opens dev.mantas.vikop2app.ui.student to javafx.fxml;
    exports dev.mantas.vikop2app.ui.student;
    opens dev.mantas.vikop2app.ui.teacher to javafx.fxml;
    exports dev.mantas.vikop2app.ui.teacher;
    opens dev.mantas.vikop2app.ui.admin to javafx.fxml;
    exports dev.mantas.vikop2app.ui.admin;

    exports dev.mantas.vikop2app.data.dao;
    exports dev.mantas.vikop2app.data.dao.impl.memory;
    exports dev.mantas.vikop2app.data.dao.impl.memory.util;
    exports dev.mantas.vikop2app.data.dao.impl.mongo;
    exports dev.mantas.vikop2app.data.dao.impl.sql;
    exports dev.mantas.vikop2app.data.dao.manager;
    exports dev.mantas.vikop2app.data.dao.manager.impl;
    exports dev.mantas.vikop2app.data.dao.type;
    exports dev.mantas.vikop2app.data.source;
    exports dev.mantas.vikop2app.data.source.provider;
    exports dev.mantas.vikop2app.data.source.impl;
    exports dev.mantas.vikop2app.exception;
    exports dev.mantas.vikop2app.model;
    exports dev.mantas.vikop2app.model.helper;
    exports dev.mantas.vikop2app.ui.common;
    exports dev.mantas.vikop2app.ui.util;
    exports dev.mantas.vikop2app.util;
    exports dev.mantas.vikop2app.data;
    exports dev.mantas.vikop2app.util.async;
}