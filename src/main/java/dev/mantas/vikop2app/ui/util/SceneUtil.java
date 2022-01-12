package dev.mantas.vikop2app.ui.util;

import dev.mantas.vikop2app.ui.Application;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class SceneUtil {

    public static void load(Event trigger, String resource) throws Exception {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource(resource));
        Parent nextViewRoot = loader.load();
        Stage stage = (Stage) ((Node) trigger.getSource()).getScene().getWindow();
        Scene nextScene = new Scene(nextViewRoot);
        stage.setScene(nextScene);
        stage.show();
    }

    public static <C> void load(Event trigger, String resource, Class<C> type, Consumer<C> controllerModifier) throws Exception {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource(resource));
        Parent nextViewRoot = loader.load();
        C controller = loader.getController();
        controllerModifier.accept(controller);
        Stage stage = (Stage) ((Node) trigger.getSource()).getScene().getWindow();
        Scene nextScene = new Scene(nextViewRoot);
        stage.setScene(nextScene);
        stage.show();
    }

}
